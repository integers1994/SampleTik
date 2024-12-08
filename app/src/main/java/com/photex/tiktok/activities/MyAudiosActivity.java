package com.photex.tiktok.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.photex.tiktok.R;
import com.photex.tiktok.adapters.MyAudiosAdapter;
import com.photex.tiktok.models.AudioInfo;
import com.photex.tiktok.services.GetMyAudiosTask;
import com.photex.tiktok.utils.AudioPlayerHelper;
import com.photex.tiktok.utils.Constants;
import com.wang.avi.AVLoadingIndicatorView;

public class MyAudiosActivity extends AppCompatActivity implements
        View.OnClickListener,
        MyAudiosAdapter.OnItemClickListner {

    private RecyclerView rvAudioList;
    private MyAudiosAdapter myAudiosAdapter;
    private ImageView cancelBtn;
    private AVLoadingIndicatorView indicatorView;
    private SimpleExoPlayer audioPlayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideStatusBar();
        setContentView(R.layout.activity_my_audios);

        initView();
        initData();
        getMyLocalAudios();
    }

    private void initView() {
        indicatorView = findViewById(R.id.sound_loading_indicator);
//        indicatorView.hide();

        cancelBtn = findViewById(R.id.img_cancel);
        cancelBtn.setOnClickListener(this);

        rvAudioList = findViewById(R.id.rv_audio_list);
        rvAudioList.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initData() {
        MyAudiosAdapter.lastPositionClicked = -1;

        myAudiosAdapter = new MyAudiosAdapter(this)
                .setOnItemClickListner(this);
        rvAudioList.setAdapter(myAudiosAdapter);
    }

    private void getMyLocalAudios() {
        GetMyAudiosTask getMyAudiosTask = new GetMyAudiosTask(this)
                .setAudioLoadListner(audioInfoList -> {
                    indicatorView.hide();
                    myAudiosAdapter.setAudioList(audioInfoList);
                    myAudiosAdapter.notifyDataSetChanged();

                });
        getMyAudiosTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        indicatorView.show();
    }

    private void pasuseSound() {
        if (audioPlayer != null){
            audioPlayer.stop();
        }
    }

    /**
     * Prepares exoplayer for audio playback from a local file
     *
     * @param uri
     */
    private void prepareAudioPlayerFromFileUri(Uri uri) {
        AudioPlayerHelper audioPlayerHelper = new AudioPlayerHelper(this);
        audioPlayer = audioPlayerHelper.getInstance();

        DataSpec dataSpec = new DataSpec(uri);
        final FileDataSource fileDataSource = new FileDataSource();
        try {
            fileDataSource.open(dataSpec);
        } catch (FileDataSource.FileDataSourceException e) {
            e.printStackTrace();
        }

        DataSource.Factory factory = new DataSource.Factory() {
            @Override
            public DataSource createDataSource() {
                return fileDataSource;
            }
        };
        MediaSource audioSource = new ExtractorMediaSource(fileDataSource.getUri(),
                factory, new DefaultExtractorsFactory(), null, null);

        audioPlayer.prepare(audioSource);
    }

    private void playSound(String selectedAudioPath) {
        prepareAudioPlayerFromFileUri(Uri.parse(selectedAudioPath));
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

    @Override
    public void onBackPressed() {
        pasuseSound();
        setResult(RESULT_CANCELED);
        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    @Override
    public void onClick(View v) {
        if (v == cancelBtn) {
            onBackPressed();
        }
    }

    @Override
    public void onShootBtnClicked(AudioInfo audioInfo, int position) {
        pasuseSound();
        setResult(RESULT_OK, new Intent().putExtra(Constants.audioInfo, audioInfo));
        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    @Override
    public void onItemClicked(AudioInfo audioInfo, int position) {
        if (MyAudiosAdapter.lastPositionClicked == -1) {
            MyAudiosAdapter.lastPositionClicked = position;
            playSound(audioInfo.getPath());

        } else if (MyAudiosAdapter.lastPositionClicked == position) {
            MyAudiosAdapter.lastPositionClicked = -1;
            pasuseSound();

        } else {
            myAudiosAdapter.updateItem(MyAudiosAdapter.lastPositionClicked);

            MyAudiosAdapter.lastPositionClicked = position;
            playSound(audioInfo.getPath());

        }

        if (position == myAudiosAdapter.getItemCount() - 1) {
            rvAudioList.smoothScrollToPosition(position);
        }
    }
}
