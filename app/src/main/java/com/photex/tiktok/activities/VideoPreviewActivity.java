package com.photex.tiktok.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.photex.tiktok.R;
import com.photex.tiktok.models.MediaInfo;
import com.photex.tiktok.services.SaveVideoService;
import com.photex.tiktok.utils.CommonUtils;
import com.photex.tiktok.utils.Constants;

import java.io.File;


public class VideoPreviewActivity extends AppCompatActivity implements
        View.OnClickListener,
        View.OnTouchListener {

    private VideoView videoView;
    private ImageView saveBtn, doneBtn;
    private MediaInfo mediaInfo;
    private MediaPlayer mMediaPlayer;

    private MediaPlayer videoPlayer;
    private boolean isMediaPlaying;
    private int currentVideoIndex = 0;
    private Uri muxedVidoeUri;

    public static void intent(Activity mActivity, String filePath, MediaInfo mediaInfo,int result_code) {
        Intent intent = new Intent(mActivity, VideoPreviewActivity.class);
        intent.putExtra(Constants.EXTRA_VIDEO_FILE_URI, filePath);
        intent.putExtra(Constants.EXTRA_VIDEO_MEDIA_INFO, mediaInfo);
        mActivity.startActivityForResult(intent,result_code);
        mActivity.overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CommonUtils.transparentStatusBar(getActivity());
        setContentView(R.layout.activity_video_preview_alternative);
        initView();
        initData();
    }

    private void initView() {
        videoView = findViewById(R.id.video_view);
        /*saveBtn = findViewById(R.id.save_btn);
        doneBtn = findViewById(R.id.done_btn);*/

        videoView.setOnTouchListener(this);
//        saveBtn.setOnClickListener(this);
//        doneBtn.setOnClickListener(this);
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null &&
                intent.hasExtra(Constants.EXTRA_VIDEO_MEDIA_INFO)
                && intent.hasExtra(Constants.EXTRA_VIDEO_FILE_URI)) {
            String stringUri = intent.getStringExtra(Constants.EXTRA_VIDEO_FILE_URI);
            muxedVidoeUri = Uri.fromFile(new File(stringUri));

            mediaInfo = (MediaInfo) getIntent().getExtras().get(Constants.EXTRA_VIDEO_MEDIA_INFO);
            Uri videoUri = Uri.fromFile(new File(mediaInfo.getInputVideos()
                    .get(currentVideoIndex++).getPath()));
            videoView.setVideoURI(videoUri);
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if (currentVideoIndex < mediaInfo.getInputVideos().size()) {
                        videoView.pause();
                        videoView.setVideoURI(Uri.fromFile(new File(mediaInfo.getInputVideos().get(currentVideoIndex++).getPath())));
                    } else {
                        stopMediaFiles();
                    }
                }
            });
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    videoPlayer = mp;
                  /*  ViewGroup.LayoutParams lp = videoView.getLayoutParams();
                    float videoWidth = mp.getVideoWidth();
                    float videoHeight = mp.getVideoHeight();
                    float layoutWidth = videoView.getWidth();
                    lp.height = (int) (layoutWidth * (videoHeight / videoWidth));
                    videoView.setLayoutParams(lp);*/
                    playMediaFiles();
                }
            });

            videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    setResult(RESULT_CANCELED, new Intent().putExtra(Constants.message, Constants.error));
                    finish();
                    return true;
                }
            });
        }
    }

    private void playMediaFiles() {
        if (mediaInfo.getAudioInfo() != null)
            playSound(mediaInfo.getAudioInfo().getPath());
        playVideo();
    }

    void playVideo() {
        isMediaPlaying = true;
        videoView.start();
    }

    private void playSound(String selectedAudioPath) {
        if (!isMediaPlaying) {
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
                mMediaPlayer = null;
            }
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer = MediaPlayer.create(this, Uri.parse(selectedAudioPath));
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.start();
        }
    }

    private void stopMediaFiles() {
        isMediaPlaying = false;
        currentVideoIndex = 0;

        videoView.pause();
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
            mMediaPlayer.seekTo(0);
        }
    }


    private void resumeMediaFiles() {
        videoView.setVideoURI(Uri.fromFile(new File(mediaInfo.getInputVideos().get(currentVideoIndex++).getPath())));
    }

    private void showVideoSavingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle("MyTikTok Video");
        String message = "Do you want to save this Video ?" +
                "\n\nIt takes some time to process, processing will done in background you can check progress in Notification!.";
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                saveVideo();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        builder.create().show();
    }

    private void saveVideo() {
        // upload video in background
        if (!SaveVideoService.IS_SERVICE_RUNNING) {
            Intent serviceIntent = new Intent(VideoPreviewActivity.this,
                    SaveVideoService.class);
            SaveVideoService.IS_SERVICE_RUNNING = true;
            serviceIntent.putExtra(Constants.EXRTA_MEDIA_INFO, mediaInfo);
            serviceIntent.setAction(Constants.START_SAVE_SERVICE);
            startService(serviceIntent);

            Toast.makeText(this, "Saving Video Started...", Toast.LENGTH_SHORT).show();
            Log.w("saveVideo", "service started");

            setResult(RESULT_OK);
            onBackPressed();
        } else {
            Log.w("saveVideo", "service already running");
            Toast.makeText(getApplicationContext(), "Last Video is still saving...\nPlease try again later!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        stopMediaFiles();
        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_done:
                /*showVideoSavingDialog();*/
               /* startActivityForResult(new Intent(VideoPreviewActivity.this
                        , VideoUploadActivity.class)
                        .putExtra(Constants.EXRTA_MEDIA_INFO, mediaInfo), Constants.UPLOAD_VIDEO_REQUEST);
                overridePendingTransition(R.anim.enter, R.anim.exit);*/

                startActivityForResult(new Intent(getActivity(), VideoUploadSecondActivity.class)
                                .putExtra(Constants.EXTRA_URI, muxedVidoeUri)
                                .putExtra(Constants.EXTRA_VIDEO_MEDIA_INFO, mediaInfo),

                        Constants.UPLOAD_VIDEO_REQUEST);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                break;
            case R.id.img_back:
                finish();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopMediaFiles();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (view == videoView) {
            if (action == MotionEvent.ACTION_DOWN) {
                Log.i("onTouch", "ACTION_DOWN");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (isMediaPlaying) {
                            stopMediaFiles();
                        } else {
                            resumeMediaFiles();
                        }
                    }
                }).run();
                return true;

            }
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.UPLOAD_VIDEO_REQUEST) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK);
                onBackPressed();
            }
        }
    }

    private Activity getActivity() {
        return VideoPreviewActivity.this;
    }
}
