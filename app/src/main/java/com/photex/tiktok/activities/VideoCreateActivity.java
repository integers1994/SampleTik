package com.photex.tiktok.activities;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Process;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.github.ybq.android.spinkit.SpinKitView;
import com.otaliastudios.cameraview.CameraException;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraOptions;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.VideoResult;
import com.otaliastudios.cameraview.controls.Audio;
import com.otaliastudios.cameraview.controls.Facing;
import com.otaliastudios.cameraview.controls.Hdr;
import com.otaliastudios.cameraview.controls.Mode;
import com.otaliastudios.cameraview.controls.VideoCodec;
import com.otaliastudios.cameraview.controls.WhiteBalance;
import com.photex.tiktok.BuildConfig;
import com.photex.tiktok.R;
import com.photex.tiktok.gallery.fragment.GallerySheetFragment;
import com.photex.tiktok.models.AudioInfo;
import com.photex.tiktok.models.MediaInfo;
import com.photex.tiktok.models.VideoInfo;
import com.photex.tiktok.mp4parser.AppendMp4Media;
import com.photex.tiktok.utils.CommonUtils;
import com.photex.tiktok.utils.Constants;
import com.photex.tiktok.utils.FileUtils;
import com.photex.tiktok.utils.StorageUtil;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class VideoCreateActivity extends AppCompatActivity implements
        View.OnClickListener,
        EasyPermissions.PermissionCallbacks {

    private String TAG = VideoCreateActivity.class.getSimpleName();

    private CameraView camera_view;
    private ImageView btn_change_camera;
    private ImageView btn_audio_trim;
    private ImageView btn_preview_video;
    private ImageView btn_delete_video;
    private ImageView img_select_gallery_video;
    private ImageView btn_capture_video;

    private SpinKitView video_capture_indicator;
    private AVLoadingIndicatorView video_preparing_indicator;

    private TextView tv_pick_sound, txt_processing;

    private AudioInfo audioInfo;
    private MediaInfo mediaInfo;
    ArrayList<VideoInfo> videosList;

    private MediaPlayer audioPlayer;
    private RoundCornerProgressBar time_progress_bar;
    private CountDownTimer seekBarTimer;

    private boolean isTimerRuning;
    private int maxDurationMain = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CommonUtils.transparentStatusBar(getActivity());
        setContentView(R.layout.activity_create_video);
        initData();
        initView();
        cameraViewListeners();
        setCamera();
        loadFFmpegBinnary();
    }

    private void loadFFmpegBinnary() {
        FFmpeg fFmpeg = FFmpeg.getInstance(getActivity());
        try {
            fFmpeg.loadBinary(new LoadBinaryResponseHandler() {

                @Override
                public void onStart() {
                    CommonUtils.showToast(getActivity(), "onStart = ");

                }

                @Override
                public void onFailure() {
                    CommonUtils.showToast(getActivity(), "onFailure = ");
                }

                @Override
                public void onSuccess() {
                    CommonUtils.showToast(getActivity(), "onSuccess = ");
                }

                @Override
                public void onFinish() {
                    CommonUtils.showToast(getActivity(), "onFinish = ");
                }
            });
        } catch (FFmpegNotSupportedException e) {
            // Handle if FFmpeg is not supported by device

            CommonUtils.showToast(getActivity(), "Exception = " + e.getLocalizedMessage());

        }
    }

    private void setCamera() {
        if (!hasCameraPermission())
            requestCameraPermission(Constants.RC_CAMERA_CAPTURE);
        else {
            setCameraSettings();
            camera_view.open();
        }
    }

    private void initView() {
        camera_view = findViewById(R.id.camera_view);
        video_capture_indicator = findViewById(R.id.video_capture_indicator);
        video_preparing_indicator = findViewById(R.id.video_preparing_indicator);
        time_progress_bar = findViewById(R.id.time_progress_bar);
        tv_pick_sound = findViewById(R.id.tv_pick_sound);

        img_select_gallery_video = findViewById(R.id.img_select_gallery_video);
        btn_change_camera = findViewById(R.id.btn_change_camera);
        btn_audio_trim = findViewById(R.id.btn_audio_trim);
        btn_preview_video = findViewById(R.id.btn_preview_video);
        btn_delete_video = findViewById(R.id.btn_delete_video);
        btn_capture_video = findViewById(R.id.btn_capture_video);

        txt_processing = findViewById(R.id.txt_processing);
        video_capture_indicator.setVisibility(View.GONE);
        video_preparing_indicator.show();
        time_progress_bar.setProgress(0);
        // Reason for selection
        tv_pick_sound.setSelected(true);
        btn_change_camera.setOnClickListener(this);
        tv_pick_sound.setOnClickListener(this);
        btn_audio_trim.setOnClickListener(this);
        btn_preview_video.setOnClickListener(this);
        btn_delete_video.setOnClickListener(this);

        /*Bind  Camera View  with  Activity Life Cycle*/
        camera_view.setLifecycleOwner(this);
    }

    private void initData() {
        mediaInfo = new MediaInfo();
        videosList = new ArrayList<>();
    }

    private void cameraViewListeners() {
        camera_view.addCameraListener(new CameraListener() {


            @Override
            public void onCameraError(@NonNull CameraException exception) {
                super.onCameraError(exception);
                Log.d(TAG, "Camera Error = ");
            }

            @Override
            public void onCameraOpened(CameraOptions options) {
                super.onCameraOpened(options);
                video_preparing_indicator.hide();
            }

            @Override
            public void onVideoTaken(VideoResult videoResult) {
                super.onVideoTaken(videoResult);

                showVideoPreparing();
                File videoFile = videoResult.getFile();
                Log.i(TAG, "onVideoTaken " + videoFile.getAbsolutePath());
                pauseSound();

                VideoInfo videoInfo = new VideoInfo();
                videoInfo.setPath(videoFile.getAbsolutePath());
                int duration = mediaInfo.getDuration();
                float progress = time_progress_bar.getProgress();

                if (videosList.size() != 0) {
                    int prevVideoDurations = 0;
                    float previousVideoProgress = 0;
                    for (VideoInfo prevVideoInfo : videosList) {
                        prevVideoDurations = prevVideoDurations + prevVideoInfo.getDuration();
                        previousVideoProgress = previousVideoProgress + prevVideoInfo.getCurrentProgress();
                    }
                    duration = duration - prevVideoDurations;
                    progress = progress - previousVideoProgress;
                }
                videoInfo.setDuration(duration);
                videoInfo.setCurrentProgress(progress);
                videosList.add(videoInfo);

                video_capture_indicator.setVisibility(View.GONE);
                btn_change_camera.setVisibility(View.VISIBLE);

                hideVideoPreparing();


                if (time_progress_bar.getProgress() == 100) {
                    startPreviewActivity();
                } else {
                    updateTextSoundNameVisibility(true);
                    updatePreviewBtn(true);
                }


            }
        });
    }

    private void updatePreviewBtn(boolean isVisible) {
        if (isVisible) {
            btn_preview_video.setVisibility(View.VISIBLE);
            btn_preview_video.setEnabled(true);
            btn_delete_video.setVisibility(View.VISIBLE);
            btn_delete_video.setEnabled(true);

        } else {
            btn_preview_video.setVisibility(View.GONE);
            btn_preview_video.setEnabled(false);
            btn_delete_video.setVisibility(View.GONE);
            btn_delete_video.setEnabled(false);
        }
    }

    private Activity getActivity() {
        return VideoCreateActivity.this;
    }

    private void startPreviewActivity() {

        showVideoPreparing();

        final String[] videoUri = new String[1];
        pauseSound();
        mediaInfo.setAudioInfo(audioInfo);
        mediaInfo.setInputVideos(videosList);

        if (BuildConfig.DEBUG) {
            for (int i = 0; i < videosList.size(); i++) {
                File file = new File(videosList.get(i).getPath());
                Log.d(TAG, "file Size = " +
                        StorageUtil.convertStorage(file.length()));
            }

        }


        //Concatenate All Videos with audios

        long startTime = System.currentTimeMillis();

        Log.d(TAG, "Start Time  = " + startTime);

        ArrayList<String> listOfMediaFiles = new ArrayList<>();

        for (int i = 0; i < videosList.size(); i++) {
            listOfMediaFiles.add(videosList.get(i).getPath());
        }
        AppendMp4Media appendMp4Media = new AppendMp4Media(getActivity()
                , listOfMediaFiles
                , audioInfo.getPath());
        //  AndroidSchedulers.mainThread()
        //Schedulers.computation()
        appendMp4Media.append()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        Log.d(TAG, "onNext" + s);
                        videoUri[0] = s;
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError" + e.getLocalizedMessage());
                    }

                    @Override
                    public void onComplete() {

                        if (videoUri[0] != null && !videoUri[0].isEmpty()) {
                            muxAudioWithVideo(videoUri[0]);
                        }
                    }
                });

    }

    private void muxAudioWithVideo(String videoUri) {

        String muxWithAudioVideoPath = FileUtils.getTempVideoFileName(getActivity())
                .getAbsolutePath().replace("mergerd.mp4", "") + "_muxed.mp4";

        ArrayList<String> command = new ArrayList<>();
        command.add("-y");
        command.add("-i");
        command.add(videoUri);
        command.add("-i");
        command.add(audioInfo.getPath());
        command.add("-c");
        command.add("copy");
        command.add("-map");
        command.add("0:v:0");
        command.add("-map");
        command.add("1:a:0");

        if (audioInfo.getPath().toLowerCase().endsWith("aac")) {
//            -bsf:a aac_adtstoasc
            command.add("-bsf:a");
            command.add("aac_adtstoasc");
        }

        command.add("-shortest");
        command.add(muxWithAudioVideoPath);
/*
        String[] cmd = {
                "-y",
                "-i",
                videoUri,
                "-i",
                audioInfo.getPath(),
                "-c",
                "copy",
                "-map",
                "0:v:0",
                "-map",
                "1:a:0",

                "-bsf:a aac_adtstoasc",
                */
/* "-vcodec",
                 "libx264",
                 "-preset",
                 "ultrafast",*//*


                "-shortest",
                muxWithAudioVideoPath
        };
*/

/*
        String[] cmd = {
                "-y",
                "-i",
                videoUri,
                "-i",
                audioInfo.getPath(),
                "-codec",
                "copy",
                "-shortest",
                muxWithAudioVideoPath
        };
*/

        FFmpeg fFmpeg = FFmpeg.getInstance(getActivity());

        String[] saveVideoCommand = new String[command.size()];
        command.toArray(saveVideoCommand);
        try {
            fFmpeg.execute(saveVideoCommand, new ExecuteBinaryResponseHandler() {
                @Override
                public void onStart() {
                    Log.d(TAG, "onStart");

                }

                @Override
                public void onProgress(String message) {
                    Log.d(TAG, "onProgress =" + message);
                }

                @Override
                public void onFailure(String message) {
                    Log.d(TAG, "onFailure = " + message);
                    CommonUtils.showToast(getActivity(), "onFailure Audio Video Mapping ");
                    hideVideoPreparing();

                }

                @Override
                public void onSuccess(String message) {
                    updatePreviewBtn(true);
                    Log.d(TAG, "onSuccess = " + message);
                    Log.d(TAG, muxWithAudioVideoPath);
                    hideVideoPreparing();
                    VideoPreviewActivity.intent(getActivity(),
                            muxWithAudioVideoPath, mediaInfo, Constants.PREVIEW_VIDEO_REQUEST);
                }

                @Override
                public void onFinish() {
                    Log.d(TAG, "onFinish = FFempeg Command");

                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
        }

        // muxWithNb(cmd);


        //With out re- encoding
/*
                            String[] cmd = {
                                 */
        /*   "-y",*//*

                                    "-i",
                                    videoUri[0],
                                    "-i",
                                    audioInfo.getPath(),
                                    "-codec",
                                    "copy",
                                    "-shortest",
                                    muxWithAudioVideoPath
                            };
*/


    }

/*
    private void muxAudioWithVideoMobileFFempeg(String videoUri) {
        String muxWithAudioVideoPath = FileUtils.getTempVideoFileName(getActivity())
                .getAbsolutePath();

        String[] cmd = {
                */
    /*  "-y",*//*

                "-i",
                videoUri,
                "-i",
                audioInfo.getPath(),
                "-c",
                "copy",
                "-map",
                "0:v:0",
                "-map",
                "1:a:0",
                "-shortest",
                muxWithAudioVideoPath
        };




      */
/*  if (FFmpeg.getInstance(getActivity()).isSupported()) {
            fFtask = FFmpeg.getInstance(getActivity()).execute(cmd, new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {
                    Log.d(TAG, "onStart");
                    *//*
     */
    /*showVideoPreparing();*//*
     */
/*
                }

                @Override
                public void onProgress(String message) {
                    Log.d(TAG, "onProgress =" + message);
                }

                @Override
                public void onFailure(String message) {
                    Log.d(TAG, "onFailure = " + message);
                    CommonUtils.showToast(getActivity(), "onFailure Audio Video Mapping ");
                    hideVideoPreparing();
                }

                @Override
                public void onSuccess(String message) {
                    Log.d(TAG, "onSuccess = " + message);
                    *//*
     */
    /*FFmpeg.getInstance(getActivity()).killRunningProcesses(fFtask);*//*
     */
/*
                    hideVideoPreparing();
                    VideoPreviewExoActivity.Companion.intent(getActivity(),
                            muxWithAudioVideoPath, mediaInfo);
                }

                @Override
                public void onFinish() {
                    Log.d(TAG, "onFinish = FFempeg Command");

                }

            });

        } else {
            CommonUtils.showToast(getActivity(), "FFempeg not initialiazed ");
        }*//*



        //With out re- encoding
*/
/*
                            String[] cmd = {
                                 *//*

     */
    /*   "-y",*//*
     */
/*

                                    "-i",
                                    videoUri[0],
                                    "-i",
                                    audioInfo.getPath(),
                                    "-codec",
                                    "copy",
                                    "-shortest",
                                    muxWithAudioVideoPath
                            };
*//*



    }
*/


    private void showVideoPreparing() {
        video_preparing_indicator.show();
        txt_processing.setVisibility(View.VISIBLE);
        /*time_progress_bar.setVisibility(View.VISIBLE);*/
    }

    private void hideVideoPreparing() {
        video_preparing_indicator.hide();
        txt_processing.setVisibility(View.GONE);
        /*time_progress_bar.setVisibility(View.GONE);*/
    }


    private void startVideoCapturing(int maxDuration) {
        btn_change_camera.setVisibility(View.GONE);

        File newVideoFile = FileUtils.getTempVideoFileName(getActivity());

        camera_view.setVideoMaxDuration(maxDuration); // millisecond
        camera_view.takeVideo(newVideoFile);

        if (audioInfo != null) {
            playSound(audioInfo.getPath(), mediaInfo.getDuration());
            startSeekBar(maxDuration);
        } else
            startSeekBar(maxDuration);
        updateTextSoundNameVisibility(true);
    }

    private void stopVideoCapturing() {

        btn_change_camera.setVisibility(View.VISIBLE);
        camera_view.stopVideo();
    }


    private void changeCamera() {
        camera_view.toggleFacing();
    }


    private void updateTextSoundNameVisibility(boolean isOptionsVisible) {
        if (isOptionsVisible) {
            tv_pick_sound.setVisibility(View.VISIBLE);
            // video_capture_indicator.setVisibility(View.VISIBLE);
        } else {
            tv_pick_sound.setVisibility(View.GONE);
            // video_capture_indicator.setVisibility(View.GONE);
        }
    }

    private void pauseSound() {
        if (audioPlayer != null) {
            audioPlayer.pause();
        }

        if (seekBarTimer != null) {
            seekBarTimer.cancel();
        }
    }

    private void stopSound() {
        if (audioPlayer != null) {
            audioPlayer.stop();
            audioPlayer.release();
            audioPlayer = null;
        }

        if (seekBarTimer != null) {
            seekBarTimer.cancel();
        }
    }

    private void playSound(String selectedAudioPath, int position) {
        if (position == 0) {
            if (audioPlayer != null) {
                audioPlayer.stop();
                audioPlayer.release();
                audioPlayer = null;
            }
            audioPlayer = new MediaPlayer();
            audioPlayer = MediaPlayer.create(this, Uri.parse(selectedAudioPath));
            audioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            audioPlayer.start();
        } else {
            audioPlayer.pause();
            audioPlayer.seekTo(position);
            audioPlayer.start();
        }

    }

    private void startSeekBar(int maxDuration) {

        /*  final long duration = audioInfo != null ? audioPlayer.getDuration() :
                Constants.VIDEO_DURATION_ONE_MINUTE_MILLI_SECONDS;*/
        final long amongToUpdate = maxDuration / 100;


        seekBarTimer = new CountDownTimer(maxDuration, amongToUpdate) {
            @Override
            public void onTick(long millisUntilFinished) {

                if (amongToUpdate * time_progress_bar.getProgress() < maxDuration) {
                    time_progress_bar.setProgress(time_progress_bar.getProgress() + 1);

                    if (audioInfo != null) {
                        mediaInfo.setDuration(audioPlayer.getCurrentPosition());

                    } else {
                        //  "Seconds Remaining: " + millisUntilFinished / 1000
                      /*  long duration = Constants.VIDEO_DURATION_ONE_MINUTE_MILLI_SECONDS -
                                millisUntilFinished;
                        EXRTA_MEDIA_INFO.setDuration((int) duration);*/
                    }

                  /*  long totalTimeCapture = maxDuration - millisUntilFinished;

                    if (totalTimeCapture >= Constants.VIDEO_DURATION_FIFTEEN_SEC_MILLI_SECONDS) {

                        btn_preview_video.setVisibility(View.VISIBLE);
                    }*/
                }
                Log.d("startSeekBar", "Progress: " + time_progress_bar.getProgress());

            }

            @Override
            public void onFinish() {


                if (camera_view.isTakingVideo()) {
                    time_progress_bar.setProgress(100);
                    pauseSound();
                    camera_view.stopVideo();
                    video_capture_indicator.setVisibility(View.GONE);
                    btn_capture_video.setVisibility(View.VISIBLE);
                    showVideoPreparing();


                }
                Log.i("startSeekBar", "onFinish");
            }
        };
        seekBarTimer.start();
    }

    private void updateSeekbar(int duration) {

        if (audioInfo != null) {
            long diffDuration = maxDurationMain - duration;
            long step = audioPlayer.getDuration() / 100;
            time_progress_bar.setProgress((int) (duration / step));
        } else {

            time_progress_bar.setProgress(0);
        }

    }

    private void setMediaDurationAndProgressBarZero() {
        time_progress_bar.setProgress(0);
        mediaInfo.setDuration(0);
    }

    private void setCameraSettings() {
        /*camera_view.setMode(Mode.VIDEO);*/
        camera_view.setFacing(Facing.FRONT);
        camera_view.setMode(Mode.VIDEO); // for video
        camera_view.setWhiteBalance(WhiteBalance.AUTO);
        //Rotatin only 90 degree
        camera_view.setUseDeviceOrientation(false); // don't

        /*camera_view.setFilter(Filters.VIGNETTE.newInstance());*/

        /*camera_view.setSessionType(SessionType.VIDEO);*/
        camera_view.setVideoCodec(VideoCodec.H_264);
        /*camera_view.setVideoQuality(VideoQuality.MAX_720P);*/
        camera_view.setHdr(Hdr.OFF);
        /*camera_view.setHdr(new Hdr(Hdr.ON));*/
        camera_view.setAudio(Audio.OFF);
        /*camera_view.setVideoBitRate(40000000);*/

/*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            camera_view.getDefaultFocusHighlightEnabled();
        }
*/
        Log.d(TAG, "Bit Rate = " + camera_view.getVideoBitRate());

    }

    private synchronized void deletePreviousFiles() {
        new Thread(() -> {
            try {
                for (VideoInfo videoInfo : videosList) {
                    new File(videoInfo.getPath()).delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                videosList.clear();
            }
        }).run();
    }

    private void deletePrevVideo() {

        if (videosList.size() > 0) {

            if (videosList.size() == 1) {
                updatePreviewBtn(false);
            }
            VideoInfo videoInfo = videosList.get(videosList.size() - 1);
            int durationDiff = mediaInfo.getDuration() - videoInfo.getDuration();
            mediaInfo.setDuration(durationDiff);
            float currentProgress = time_progress_bar.getProgress() - videoInfo.getCurrentProgress();
            time_progress_bar.setProgress(currentProgress);
            //updateSeekbar(durationDiff);

            if (audioInfo != null)
                audioPlayer.seekTo(durationDiff);
            new File(videoInfo.getPath()).delete();

            videosList.remove(videosList.size() - 1);

        }
    }

    private void showVideoDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(getString(R.string.delete_video_segment));
        String message = getString(R.string.are_you_sure_want_to_delete_last_segment);
        builder.setMessage(message);
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deletePrevVideo();
            }
        }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        builder.create().show();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseSound();
        if (camera_view.isTakingVideo()) {
            camera_view.stopVideo();
        }
        /*stopVideoCapturingBy();*/
    }

    @Override
    protected void onDestroy() {

        /*killFfmpegProcess();*/
        stopSound();
        camera_view.destroy();

        super.onDestroy();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btn_change_camera:
                if (!camera_view.isTakingVideo()) {
                    changeCamera();
                } else {

                }
                break;
            case R.id.tv_pick_sound:
                intentPickSound();
                break;
            case R.id.btn_preview_video:
                if (camera_view.isTakingVideo()) {
                    camera_view.stopVideo();
                    pauseSound();
                } else {
                    startPreviewActivity();
                }

                break;
            case R.id.img_select_gallery_video:
                selectVideoFromGallery();
                break;
            case R.id.btn_delete_video:
                showVideoDeleteDialog();
                break;
            case R.id.btn_audio_trim:
                intentAudioTrim();
                break;
            case R.id.img_cancel:
                finish();
                break;
            case R.id.btn_capture_video:
                if (time_progress_bar.getProgress() != 100) {
                    if (audioInfo != null) {

                        time_progress_bar.setVisibility(View.VISIBLE);
                        video_capture_indicator.setVisibility(View.VISIBLE);

                        btn_capture_video.setVisibility(View.GONE);
                        img_select_gallery_video.setVisibility(View.GONE);

                        if (camera_view.isTakingVideo()) {
                            pauseSound();
                            stopVideoCapturing();


                        } else {
                        /*Decide Max Video Duration.if have audio file then turn off audio
                         from speaker otherwise turn on them*/
                            /*if already have video*/
                            if (audioInfo != null) {
                                camera_view.setAudio(Audio.OFF);
                                //
                                if (audioInfo.isLocalAudio()) {

                                    /*In case if audio size is less 15 seconds*/
                                    if (audioInfo.getDuration() < Constants.VIDEO_DURATION_FIFTEEN_SEC_MILLI_SECONDS) {
                                        maxDurationMain = audioInfo.getDuration();
                                    } else {
                                        maxDurationMain = Constants.VIDEO_DURATION_FIFTEEN_SEC_MILLI_SECONDS;
                                    }

                                    if (videosList != null && videosList.size() > 0) {

                                        maxDurationMain = maxDurationMain - mediaInfo.getDuration();
                                    }


                                } else {
                                    /* if (videosList != null && videosList.size() > 0)*/
                                    maxDurationMain = (audioInfo.getDuration() * 1000/*Converting to  milliseconds*/)
                                            - mediaInfo.getDuration();
                                }

                            } else {
                                camera_view.setAudio(Audio.ON);
                                maxDurationMain = Constants.VIDEO_DURATION_ONE_MINUTE_MILLI_SECONDS;
                            }

                            if (maxDurationMain > Constants.MINIMUM_VIDEO_SIZE) {


                                startVideoCapturing(maxDurationMain);

                            }

                       /*else {
                        startPreviewActivity();
                    }*/
                        }
                    } else {
                        showSelectSoundMessage();
                    }
                } else {
                    video_capture_indicator.setVisibility(View.GONE);
                    btn_capture_video.setVisibility(View.VISIBLE);
                }

                break;
            case R.id.video_capture_indicator:

                stopVideoCapturingBy();
                break;

        }

    }

    private void stopVideoCapturingBy() {
        if (camera_view.isTakingVideo()) {
            video_capture_indicator.setVisibility(View.GONE);
            btn_capture_video.setVisibility(View.VISIBLE);

            stopVideoCapturing();
            mediaInfo.setDuration(audioPlayer.getCurrentPosition());
            pauseSound();

            if (seekBarTimer != null)
                seekBarTimer.cancel();
        }

    }

    private void intentAudioTrim() {
        if (audioInfo != null) {
            try {
                Intent intent = new Intent(this,
                        AudioTrimmerActivity.class);
                intent.putExtra(Constants.audioInfo, audioInfo);
                startActivityForResult(intent, Constants.SELECT_AUDIO_REQUEST);
                overridePendingTransition(R.anim.enter, R.anim.exit);

            } catch (Exception e) {
                Log.e("Ringdroid", "Couldn't start editor");
                Toast.makeText(this, "Something goes wrong please try again! ", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please Pic a Sound First! ", Toast.LENGTH_SHORT).show();
        }
    }

    private void intentPickSound() {
        startActivityForResult(new Intent(this,
                ServerAudiosActivity.class), Constants.SELECT_AUDIO_REQUEST);
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    private void selectVideoFromGallery() {
        if (!hasWritePermission()) {
            requestWriteReadPermission(Constants.
                    RC_STORAGE_PERMISSION_GALLERY_VIDEOS);
        } else {
            GallerySheetFragment gallerySheetFragment =
                    GallerySheetFragment.newInstance();
            gallerySheetFragment.show(getSupportFragmentManager()
                    , gallerySheetFragment.getTag());
        }
    }

    private void showSelectSoundMessage() {
        if (!isTimerRuning) {
            isTimerRuning = true;
            new CountDownTimer(500, 250) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    Toast.makeText(VideoCreateActivity.this,
                            "Please Pic a Sound and Hold this button for Video",
                            Toast.LENGTH_SHORT).show();
                    isTimerRuning = false;
                }
            }.start();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.SELECT_AUDIO_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (data != null && data.hasExtra(Constants.audioInfo)) {
                    audioInfo = (AudioInfo) data.getExtras().get(Constants.audioInfo);
                    if (audioInfo != null) {
                        tv_pick_sound.setText(audioInfo.getTitle());
                        deletePreviousFiles();
                        setMediaDurationAndProgressBarZero();
                        updatePreviewBtn(false);
                    }

                }
            }
        } else if (requestCode == Constants.PREVIEW_VIDEO_REQUEST) {
            if (resultCode == RESULT_CANCELED && data != null) {
                if (data.getExtras().get(Constants.message).equals(Constants.error)) {
                    Toast.makeText(this, "Some Thing goes wrong please try again!", Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == RESULT_OK) {
                setMediaDurationAndProgressBarZero();
                updatePreviewBtn(false);
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }


    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

        requestOrPerformAction(requestCode);

    }

    private void requestOrPerformAction(int requestCode) {
        switch (requestCode) {
            case Constants.RC_CAMERA_CAPTURE:
                setCamera();
                break;
            case Constants.RC_STORAGE_PERMISSION_GALLERY_VIDEOS:
                selectVideoFromGallery();
                break;

        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        } else {
            requestOrPerformAction(requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private void requestWriteReadPermission(int requestCode) {

        EasyPermissions.requestPermissions(
                (Activity) getActivity(),
                getString(R.string.msg_permission_please_give_us_storage_permission),
                requestCode, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        );
    }


    private void requestCameraPermission(int requestCode) {

        EasyPermissions.requestPermissions(
                (Activity) getActivity(),
                getString(R.string.msg_permission_camera_permission),
                requestCode, Manifest.permission.CAMERA
        );
    }

    private boolean hasCameraPermission() {

        return EasyPermissions.hasPermissions(getActivity(),
                Manifest.permission.CAMERA);
    }

    private boolean hasWritePermission() {

        return EasyPermissions.hasPermissions(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void killFfmpegProcess() {
        try {
            FFmpeg.getInstance(getActivity()).killRunningProcesses();
            Log.w(TAG, "FFmpeg Stoped");
        } catch (Exception e) {
            Log.w(TAG, "FFmpeg Stoped failed");
            e.printStackTrace();
        }

        try {
            int pid = Process.myPid();
            Process.sendSignal(pid, 15);
            Log.w(TAG, "Process killed Notified");
        } catch (Exception e) {
            Log.w(TAG, "Process killed Notification failed");
            e.printStackTrace();
        }
    }


}
