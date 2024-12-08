package com.photex.tiktok.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.github.jlmd.animatedcircleloadingview.AnimatedCircleLoadingView;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.photex.tiktok.R;
import com.photex.tiktok.adapters.AllAudiosAdapter;
import com.photex.tiktok.adapters.ServerAudiosAdapter;
import com.photex.tiktok.endlessrecycle.EndlessRecyclerViewScrollListener;
import com.photex.tiktok.models.AudioInfo;
import com.photex.tiktok.models.ResponseAudioInfo;
import com.photex.tiktok.models.restmodels.GetAudios;
import com.photex.tiktok.rest.CallbackWithRetry;
import com.photex.tiktok.rest.RestClient;
import com.photex.tiktok.utils.AudioPlayerHelper;
import com.photex.tiktok.utils.Constants;
import com.photex.tiktok.utils.Util;
import com.wang.avi.AVLoadingIndicatorView;

import net.alhazmy13.gota.Gota;
import net.alhazmy13.gota.GotaResponse;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ServerAudiosActivity extends AppCompatActivity implements
        View.OnClickListener,
        Gota.OnRequestPermissionsBack, ServerAudiosAdapter.OnItemClickListner {

    private File audioDirectory;

    private RecyclerView rvAudioList;
    private ImageView cancelBtn;
    private AVLoadingIndicatorView soundLoadingIndicator;
    private TextView mySoundBtn;
    private AnimatedCircleLoadingView circleLoadingView;

    private SimpleExoPlayer audioPlayer;
    private int downloadAudioId = -1;
    private String lastId;
    private LinearLayoutManager linearLayoutManager;

    private ServerAudiosAdapter serverAudiosAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideStatusBar();
        setContentView(R.layout.activity_server_audios);
        initView();
        initData();
        requestWritePermission();
    }

    private void hideStatusBar() {
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    private void initView() {
        soundLoadingIndicator = findViewById(R.id.sound_loading_indicator);
        circleLoadingView = findViewById(R.id.circle_loading_view);
        circleLoadingView.resetLoading();

        cancelBtn = findViewById(R.id.img_cancel);
        mySoundBtn = findViewById(R.id.btn_my_sounds);
        cancelBtn.setOnClickListener(this);
        mySoundBtn.setOnClickListener(this);

        rvAudioList = findViewById(R.id.rv_audio_list);
        linearLayoutManager = new LinearLayoutManager(this);
        rvAudioList.setLayoutManager(linearLayoutManager);
    }

    private void initData() {
        ServerAudiosAdapter.lastPositionClicked = -1;

        // for SingleAudioInfo player
        AudioPlayerHelper audioPlayerHelper = new AudioPlayerHelper(this);
        audioPlayer = audioPlayerHelper.getStreamingInstance();

        // for downloading audios
        PRDownloader.initialize(this);

        AllAudiosAdapter.lastPositionClicked = -1;
        serverAudiosAdapter = new ServerAudiosAdapter(this)
                .setOnItemClickListner(this);

        rvAudioList.setAdapter(serverAudiosAdapter);
        rvAudioList.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Log.i("loadMore", "Loading" + page + "  " + totalItemsCount);
                if (serverAudiosAdapter.getItemCount() >= 10)
                    getAllAudios();
            }
        });
    }

    private void requestWritePermission() {
        new Gota.Builder(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .requestId(Constants.STORAGE_PERMITTIONS_REQUEST)
                .setListener(this)
                .check();
    }

    private void pauseSound() {
        if (audioPlayer != null) {
            audioPlayer.setPlayWhenReady(false);
        }
    }

    private void prepareAudioPlayerFromStreamUrl(Uri streamUri) {
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this,
                com.google.android.exoplayer2.util.Util.getUserAgent(this, getResources().getString(R.string.app_name)), null);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource audioSource = new ExtractorMediaSource(streamUri, dataSourceFactory, extractorsFactory, null, null);

        audioPlayer.prepare(audioSource);
        audioPlayer.setPlayWhenReady(true);

    }

    private void playSound(String selectedAudioPath) {
        prepareAudioPlayerFromStreamUrl(Uri.parse(selectedAudioPath));
    }


    private void getAllAudios() {
        // TODO: 12/28/2018 Load More implementation missing from server end
        // as well as application end
        if (Util.isNetworkAvailable(this)) {
            soundLoadingIndicator.setVisibility(View.VISIBLE);
            int totalItem = serverAudiosAdapter.getItemCount();
            if (totalItem > 0) {
                lastId = serverAudiosAdapter.getCommentId(totalItem - 1);
            } else {
                lastId = "0";
            }

            GetAudios getAudios = new GetAudios();
            getAudios.setLastId(lastId);
            Call<ResponseAudioInfo> call = new RestClient(Constants.BASE_URL,
                    ServerAudiosActivity.this)
                    .get().getAllAudioWithResponse(getAudios);

            call.enqueue(new CallbackWithRetry<ResponseAudioInfo>(call) {

                @Override
                public void onResponse(Call<ResponseAudioInfo> call,
                                       Response<ResponseAudioInfo> response) {
                    soundLoadingIndicator.setVisibility(View.GONE);

                    if (response.isSuccessful() && response.body() != null &&
                            response.body().getSuccess()) {

                        List<ResponseAudioInfo.SingleAudioInfo> audioInfos = response.body()
                                .getAudios();

                        if (audioInfos != null && audioInfos.size() > 0) {

                            ArrayList<ResponseAudioInfo.SingleAudioInfo> singleAudioInfos
                                    = serverAudiosAdapter.getAudioList();
                            singleAudioInfos.addAll(audioInfos);
                            serverAudiosAdapter.setAudioList(singleAudioInfos);
                            serverAudiosAdapter.notifyDataSetChanged();

                        } else {

/*
                            Util.showToast(getActivity(),Toast.LENGTH_SHORT,
                                    "");
*/
                        }
                        //  List<ResponseAudioInfoOld> serverAudios= response.body();
                    } else {
                        Util.showToast(getActivity(), Toast.LENGTH_SHORT,
                                "Please try again,Something went wrong");

                    }

                }

                @Override
                public void onFinallyFail() {
                    soundLoadingIndicator.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call Call, Throwable t) {
                    super.onFailure(Call, t);

                }


            });


/*
            Call<String> call = new RestClient(Constants.BASE_URL,
                    ServerAudiosActivity.this).get().getAllAudios(getAudios);
*/
/*
            call.enqueue(new CallbackWithRetry<String>(call) {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    soundLoadingIndicator.setVisibility(View.GONE);
                    JSONArray jsonArray = null;
                    boolean success = false;
                    if (response.body() != null && response.isSuccessful()) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            success = jsonObject.getBoolean("success");
                            jsonArray = jsonObject.getJSONArray("audios");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (success && jsonArray != null) {
                            ArrayList<ResponseAudioInfoOld> serverAudiosList = new ArrayList<>();
                            Gson gson = new Gson();

                            List<ResponseAudioInfoOld> serverAudios;
                            serverAudios = Arrays.asList(gson.fromJson(jsonArray.toString(), ResponseAudioInfoOld[].class));

                            if (serverAudios.size() > 0) {
                                lastId = serverAudios.get(serverAudios.size() - 1).get_id();

                                for (ResponseAudioInfoOld serverAudio : serverAudios) {
                                    serverAudio.setAudioUrl(Constants.STREAMING_URL +
                                            serverAudio.getAudioUrl());
                                    serverAudiosList.add(serverAudio);
                                }
                                serverAudiosAdapter.setAudioList(serverAudiosList);
                                serverAudiosAdapter.notifyDataSetChanged();
                            }

                        } else {
                            Toast.makeText(ServerAudiosActivity.this, "Some thing goes wrong please try again!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFinallyFail() {
                    Log.e("getComments", "onFinallyFail");
                    Toast.makeText(ServerAudiosActivity.this, "Some thing goes wrong please try again!", Toast.LENGTH_SHORT).show();
                    soundLoadingIndicator.setVisibility(View.GONE);
                }
            });
*/

        } else {
            Util.internetNotAvailableDialouge(this);
        }
    }


    private Activity getActivity() {
        return ServerAudiosActivity.this;
    }

    private void downloadAudioFile(ResponseAudioInfo.SingleAudioInfo audioInfo) {


        audioDirectory = new File(Environment.getExternalStorageDirectory(),
                Constants.TEMP_AUDIO_FOLDER);
        if (!audioDirectory.exists()) {
            audioDirectory.mkdirs();
        }

        circleLoadingView.setVisibility(View.VISIBLE);
        circleLoadingView.startDeterminate();

        /*Making full url */
        downloadAudioId = PRDownloader.download(Constants.STREAMING_URL + audioInfo.getAudioUrl(),
                audioDirectory.getAbsolutePath(), audioInfo.getTitle())
                .build()
                .setOnProgressListener(progress -> {
                    long progressPercent = progress.currentBytes * 100 / progress.totalBytes;
                    circleLoadingView.setPercent((int) progressPercent);
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        circleLoadingView.stopOk();

                        AudioInfo localAudioInfo = new AudioInfo();
                        localAudioInfo.setAudioId(audioInfo.get_id());
                        localAudioInfo.setPath(audioDirectory.getAbsolutePath()
                                + File.separator + audioInfo.getTitle());
                        localAudioInfo.setCatId(audioInfo.getCatId());
                        localAudioInfo.setOwner(audioInfo.getUserName());
                        localAudioInfo.setTitle(audioInfo.getTitle());
                        localAudioInfo.setDuration(Integer.parseInt(audioInfo.getDuration()));
                        localAudioInfo.setLocalAudio(false);


                        serverAudiosAdapter.updateItem(ServerAudiosAdapter.lastPositionClicked);
                        onItemClicked(audioInfo, ServerAudiosAdapter.lastPositionClicked);

                        setResult(RESULT_OK, new Intent().putExtra(Constants.audioInfo, localAudioInfo));
                        finish();
                        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                    }

                    @Override
                    public void onError(Error error) {
                        circleLoadingView.stopFailure();
                        Toast.makeText(ServerAudiosActivity.this, "Some thing goes wrong please try again", Toast.LENGTH_SHORT).show();
                        circleLoadingView.setVisibility(View.GONE);
                    }
                });
    }


    @Override
    public void onBackPressed() {
        pauseSound();
        if (downloadAudioId != -1) {
            PRDownloader.cancel(downloadAudioId);
        }
        setResult(RESULT_CANCELED);
        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    @Override
    public void onClick(View view) {
        if (view == cancelBtn) {
            onBackPressed();
        } else if (view == mySoundBtn) {
            pauseSound();

            startActivityForResult(new Intent(this, MyAudiosActivity.class),
                    Constants.SELECT_AUDIO_REQUEST);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        }
    }

    @Override
    public void onRequestBack(int requestId, @NonNull GotaResponse gotaResponse) {
        if (requestId == Constants.STORAGE_PERMITTIONS_REQUEST) {
            if (gotaResponse.isAllGranted()) {
                getAllAudios();
            } else {
                Toast.makeText(this, "Storage Permittion is required to continue...", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.SELECT_AUDIO_REQUEST) {
            if (resultCode == RESULT_OK) {
                AudioInfo audioInfo = (AudioInfo) data.getExtras().get(Constants.audioInfo);
                if (audioInfo != null)
                {
                    //Converting sec to milliseconds
                    audioInfo.setDuration(audioInfo.getDuration() *1000);
                    audioInfo.setLocalAudio(true);
                    pauseSound();
                    setResult(RESULT_OK,
                            new Intent().putExtra(Constants.audioInfo, audioInfo));
                    finish();
                    overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                }

            }
        }
    }

    @Override
    public void onShootBtnClicked(ResponseAudioInfo.SingleAudioInfo audioInfo, int position) {
        //Todo check audio files

        downloadAudioFile(audioInfo);
    }

    @Override
    public void onItemClicked(ResponseAudioInfo.SingleAudioInfo audioInfo, int position) {
        if (ServerAudiosAdapter.lastPositionClicked == -1) {
            ServerAudiosAdapter.lastPositionClicked = position;
            playSound(Constants.STREAMING_URL + audioInfo.getAudioUrl());

        } else if (ServerAudiosAdapter.lastPositionClicked == position) {
            ServerAudiosAdapter.lastPositionClicked = -1;
            pauseSound();

        } else {
            serverAudiosAdapter.updateItem(ServerAudiosAdapter.lastPositionClicked);

            ServerAudiosAdapter.lastPositionClicked = position;
            playSound(Constants.STREAMING_URL + audioInfo.getAudioUrl());
        }

        if (position == serverAudiosAdapter.getItemCount() - 1) {
            rvAudioList.smoothScrollToPosition(position);
        }
    }


}
