package com.photex.tiktok.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.google.gson.Gson;
import com.photex.tiktok.R;
import com.photex.tiktok.activities.MainActivity;
import com.photex.tiktok.models.MediaInfo;
import com.photex.tiktok.models.ResponseAudioInfoOld;
import com.photex.tiktok.models.VideoInfo;
import com.photex.tiktok.models.VideoMakerPostInfo;
import com.photex.tiktok.rest.RestClient;
import com.photex.tiktok.setting.SettingManager;
import com.photex.tiktok.utils.Constants;
import com.photex.tiktok.utils.FileUtils;
import com.photex.tiktok.utils.NotificationHelper;
import com.photex.tiktok.utils.RealPathUtil;
import com.photex.tiktok.utils.TimeUtil;
import com.photex.tiktok.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadVideoSecondService extends Service implements ProgressRequestBody.UploadCallbacks {


    private String TAG = UploadVideoSecondService.class.getSimpleName();

    public static boolean IS_SERVICE_RUNNING = false;
    public static final int SERVICE_REQUEST_ID = 1;
    private MediaInfo mediaInfo;
    private String Tag = "UploadVideoSecondService";
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotificationManager;
    private int notificationId;

    private CountDownTimer timmer;
    private int currentProgress = 0;
    private boolean isTimerRunning;
    private VideoMakerPostInfo postInfo;
    private Call<String> call;
    private boolean isUploadComplete;

    private Uri videoUri;

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager =
                (NotificationManager)
                        getSystemService(Context.NOTIFICATION_SERVICE);

        timmer = new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                if (!isUploadComplete && IS_SERVICE_RUNNING && currentProgress > 1) {
                    updateNotificationProgress(currentProgress);
                }
            }
        };

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null && intent.getAction() != null && !intent.getAction().isEmpty()) {
            if (intent.getAction().equals(Constants.START_UPLOAD_SERVICE)) {
                initializeData(intent);
                showNotification();
                loadFFmpegBinnary();
            } else if (intent.getAction().equals(Constants.DONE_SAVE_SERVICE)) {
                takeFinalAction();
            } else if (intent.getAction().equals(Constants.CANCEL_SAVE_SERVICE)) {
                cancelSaveService();
            }
        }
        return START_NOT_STICKY;
    }

    private void loadFFmpegBinnary() {
        FFmpeg fFmpeg = FFmpeg.getInstance(getContext());
        try {
            fFmpeg.loadBinary(new LoadBinaryResponseHandler() {

                @Override
                public void onStart() {


                }

                @Override
                public void onFailure() {

                }

                @Override
                public void onSuccess() {
                    /*convertIntoHls();*/
                    compressVideo();
/*
                if (mediaInfo.getAudioInfo() != null) {
                    */
                    /*saveVideo();*//*

                    compressVideo();
                } else {
                    postInfo.setVideoFilePath(
                            new File(mediaInfo.getInputVideos().
                                    get(0).getPath()));
                    uploadVideoPost(postInfo);
                }
*/
                }

                @Override
                public void onFinish() {

                }
            });
        } catch (FFmpegNotSupportedException e) {
            // Handle if FFmpeg is not supported by device
            showSavingFailedNotification();
        }
    }

    private void compressVideo() {

        String videoPath = RealPathUtil.getPathFromUri(getContext(), videoUri);
        String compressFileName = FileUtils.getTempVideoFileName(getContext()).getAbsolutePath()
                .replace("mergerd.mp4", "") + "upload_muxed.mp4";
        String[] cmdCompress = {
                "-y",
                "-i",
                videoPath,
                "-crf",
                "27",
                "-preset",
                "ultrafast",
                compressFileName

        };
        Log.d(TAG, TimeUtil.getDurationBreakdown(System.currentTimeMillis()));

        try {
            FFmpeg.getInstance(getContext()).execute(cmdCompress, new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {
                    Log.d(TAG, "onStart");
                }

                @Override
                public void onProgress(String message) {
                    Log.d(TAG, "onProgress =" + message);
                }

                @Override
                public void onSuccess(String message) {
                    super.onSuccess(message);

                    Log.d(TAG, "onSuccess =" + message);

                    File file = new File(compressFileName);
                    long file_size = (file.length() / 1024);
                    Log.d(TAG, "file Size = " + file_size);
                    //Set Path To Upload
                    postInfo.setVideoFilePath(file);
                    uploadVideoPost(postInfo);
                    videoUri = Uri.fromFile(file);
                    convertIntoHls();


                }

                @Override
                public void onFailure(String message) {
                    Log.d(TAG, "onFailure = " + message);
                    showSavingFailedNotification();
                }

                @Override
                public void onFinish() {
                    Log.d(TAG, "onFinish = ");
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
        }


    }

    private void convertIntoHls() {

        /*
         * ffmpeg -i test_video.mp4 -profile:v baseline
         * -crf 20 -g 48
         * -keyint_min 48 -start_number 0
         * -hls_time 4 -hls_list_size 0
         * -hls_playlist_type vod
         * -f hls C:\ffmpeg\bin\output\index.m3u8
         *
         * */
        Log.d(TAG, "Start Time = " + TimeUtil.getDurationBreakdown(System.currentTimeMillis()));
        String videoPath = RealPathUtil.getPathFromUri(getContext(), videoUri);

        String compressFileName = FileUtils.getTempM3U8FileName(getContext()).getAbsolutePath();

        String[] hlsConvertCompress = {
                "-y",
                "-i",
                videoPath,
                "-profile:v",
                "baseline",
                "-crf",
                "27",
                "-g",
                "48",
                "-keyint_min",
                "48",
                "-start_number",
                "0",
                "-hls_time",
                "4",
                "-hls_list_size",
                "0",
                /*"-hls_playlist_type",
                "vod",*/
                "-f",
                "hls",
                "-preset",
                "ultrafast",
                compressFileName

        };
        Log.d(TAG, TimeUtil.getDurationBreakdown(System.currentTimeMillis()));

        try {
            FFmpeg.getInstance(getContext()).execute(hlsConvertCompress, new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {
                    Log.d(TAG, "onStart");
                }

                @Override
                public void onProgress(String message) {
                    Log.d(TAG, "onProgress =" + message);
                }

                @Override
                public void onSuccess(String message) {
                    super.onSuccess(message);

                    Log.d(TAG, "End Time = " + TimeUtil.getDurationBreakdown(System.currentTimeMillis()));
                  /*  Log.d(TAG, "onSuccess =" + message);

                    File file = new File(compressFileName);
                    long file_size = (file.length() / 1024);
                    Log.d(TAG, "file Size = " + file_size);
                    //Set Path To Upload
                    postInfo.setVideoFilePath(file);
                    uploadVideoPost(pos tInfo);*/



                /*    //todo Turn off Audio Uploading
                    if (mediaInfo.getAudioInfo().isLocalAudio()) {
                        uploadAudio();
                    } else {
                        File file = new File(compressFileName);
                        long file_size = (file.length() / 1024);
                        Log.d(TAG, "file Size = " + file_size);
                        uploadVideoPost(postInfo);
                    }*/


                }

                @Override
                public void onFailure(String message) {
                    Log.d(TAG, "onFailure = " + message);
                    showSavingFailedNotification();
                }

                @Override
                public void onFinish() {
                    Log.d(TAG, "onFinish = ");
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
        }

    }

    private void initializeData(Intent intent) {
        if (intent != null &&
                intent.hasExtra(Constants.EXRTA_MEDIA_INFO)
                && intent.hasExtra(Constants.EXTRA_POST_INFO)
                && intent.hasExtra(Constants.EXTRA_URI)) {

            mediaInfo = (MediaInfo) intent.getExtras().get(Constants.EXRTA_MEDIA_INFO);
            postInfo = (VideoMakerPostInfo) intent.getExtras().get(Constants.EXTRA_POST_INFO);
            videoUri = (Uri) intent.getExtras().get(Constants.EXTRA_URI);

        }
    }

    private void saveVideo() {
        File fileDirectory = new File(Environment.getExternalStorageDirectory(),
                Constants.VIDEO_FOLDER);
        if (!fileDirectory.exists()) {
            fileDirectory.mkdirs();
        }
        File dest = null;
        ArrayList<String> commandsList = new ArrayList<>();
        String[] saveVideoCommand = null;

        try {
            dest = new File(fileDirectory, Util.getSortedStringDate() + ".mp4");
            mediaInfo.setOutputVideoPath(dest.getAbsolutePath());
            postInfo.setVideoFilePath(dest);

            commandsList.add("-y");
            commandsList.add("-i");
            commandsList.add(mediaInfo.getAudioInfo().getPath());

            if (mediaInfo.getInputVideos().size() > 1) {
                // concatinate all videos
                int index = 0;
                String videoMatrix = "";
                for (VideoInfo videoInfo : mediaInfo.getInputVideos()) {
                    commandsList.add("-i");
                    commandsList.add(videoInfo.getPath());
                    videoMatrix = videoMatrix + "[" + ++index + ":v]";
                }
                commandsList.add("-filter_complex");
                commandsList.add(videoMatrix + " concat=n=" + mediaInfo.getInputVideos().size() + ":v=1:a=0 [v]");
                commandsList.add("-filter_complex");
                commandsList.add("[0:a] concat=n=1:v=0:a=1 [a]");

                commandsList.add("-map");
                commandsList.add("[v]");
                commandsList.add("-map");
                commandsList.add("[a]");
            } else {
                commandsList.add("-i");
                commandsList.add(mediaInfo.getInputVideos().get(0).getPath());
            }

            commandsList.add("-shortest");
            commandsList.add("-r");
            commandsList.add("25");
            commandsList.add("-vcodec");
            commandsList.add("libx264");
            commandsList.add(dest.getAbsolutePath());

            saveVideoCommand = new String[commandsList.size()];
            commandsList.toArray(saveVideoCommand);


        } catch (Exception e) {
            e.printStackTrace();
        }

        execFFmpegCommand(saveVideoCommand, dest, "Saving Video");
    }

    private Context getContext() {
        return UploadVideoSecondService.this;
    }


    private void execFFmpegCommand(final String[] command, final File dest, final String operation) {

        try {

            FFmpeg.getInstance((getContext())).execute(command,
                    new ExecuteBinaryResponseHandler() {
                        @Override
                        public void onFailure(String s) {
                            Log.d(TAG, operation + " FAILED with output : " + s);
                            showSavingFailedNotification();
//                    tempFile = dest;
                        }

                        @Override
                        public void onSuccess(String s) {
                            Log.d(TAG, operation + " onSuccess");
                            // created video path to play video
//                    EXRTA_MEDIA_INFO.setURL(dest.getAbsolutePath());
//                    updateNotificationActions("Video Saved in \"MyTikTok/Videos\" Folder");
                            if (mediaInfo.getAudioInfo().isLocalAudio()) {
                                uploadAudio();
                            } else {
                                File file = new File(mediaInfo.getOutputVideoPath());
                                long file_size = (file.length() / 1024);

                                Log.d(TAG, "file Size = " + file_size);
                                uploadVideoPost(postInfo);
                            }
                        }

                        @Override
                        public void onProgress(String s) {
                            Log.d(TAG, operation + " onProgress" + s);
                            showProgress(s);
                            if (!IS_SERVICE_RUNNING) {
                                killFfmpegProcess(dest);
                            }
                        }

                        @Override
                        public void onStart() {
                            Log.d(TAG, operation + " Started");
                        }

                        @Override
                        public void onFinish() {
                            Log.d(TAG, operation + " Finished");
                        }
                    });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // do nothing for now
            Log.d(TAG, "FFmpegCommandAlreadyRunningException : ffmpeg " + command);
            showSavingFailedNotification();
//            tempFile = dest;
            e.printStackTrace();
        }
    }

    private void uploadVideoPost(VideoMakerPostInfo postInfo) {
        showUploadNotification();

        // request multiple file
        ProgressRequestBody videoRequest =
                new ProgressRequestBody(postInfo.getVideoFilePath(), this);
        RequestBody thumbnailRequest = RequestBody.create(
                MediaType.parse("image"),
                postInfo.getThumbFilePath()
        );

        MultipartBody.Part videoBody;
        MultipartBody.Part thumbnailBody;

        try {
            videoBody = MultipartBody.Part.createFormData("video",
                    postInfo.getVideoFilePath().getName().replace(" ", "_"), videoRequest);
            thumbnailBody = MultipartBody.Part.createFormData("video",
                    postInfo.getThumbFilePath().getName().replace(" ", "_"), thumbnailRequest);
        } catch (Exception e) {
            e.printStackTrace();
            videoBody = MultipartBody.Part.createFormData("video",
                    postInfo.getVideoFilePath().getName(), videoRequest);
            thumbnailBody = MultipartBody.Part.createFormData("video",
                    postInfo.getThumbFilePath().getName(), thumbnailRequest);
        }

        call = new RestClient(Constants.BASE_URL, this)
                .get().addPost(
                        mapRequest(postInfo),
                        videoBody,
                        thumbnailBody);

        call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Log.w(Tag, "response.isSuccessful()");

                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        if (Boolean.parseBoolean(jsonObject.getString("success"))) {
                            Log.w(Tag, "uploaded Successfully");
                            isUploadComplete = true;
                            updateNotificationActions(getString(R.string.msg_uploading_complete));
                            timmer.cancel();
                            // todo stop it in forground
                            stopForeground(true);
                            /*stopUploadService();*/

                        } else {
                            Log.w(Tag, "uploading failed");
                            showSavingFailedNotification();
                            stopUploadService();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.w(Tag, "JSONException");
                        stopUploadService();
                    }
                } else {
                    stopUploadService();
                    Log.w(Tag, "Server Error response code" + response.errorBody());
                }

                deleteRecursive(FileUtils.getTempFolder(getContext()));
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.w(Tag, "onFailure");
                if (!call.isCanceled()) {
                    stopUploadService();
                    if (timmer != null)
                        timmer.cancel();
                }

                deleteRecursive(FileUtils.getTempFolder(getContext()));
            }
        });
    }

    private void uploadAudio() {
        showAudioUploadNotification();

        File audioFile = new File(mediaInfo.getAudioInfo().getPath());
        File audioThumbNailFile;
        if (mediaInfo.getAudioInfo().getThumbNail() == null) {
            Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.audio_place_holder);
            audioThumbNailFile = createthumb(bitmap);
        } else {
            audioThumbNailFile = new File(mediaInfo.getAudioInfo().getThumbNail());
        }

        ResponseAudioInfoOld videoMakerAudioInfo = new ResponseAudioInfoOld();
        videoMakerAudioInfo.setFolderName(SettingManager.getUserFolderName(this));
        videoMakerAudioInfo.setUserName(postInfo.getUserName());
        videoMakerAudioInfo.setUserId(postInfo.getUserId());
        videoMakerAudioInfo.setDuration(String.valueOf(mediaInfo.getAudioInfo()
                .getDuration()));
        videoMakerAudioInfo.setTitle(mediaInfo.getAudioInfo().getTitle());
        videoMakerAudioInfo.setCatId(mediaInfo.getAudioInfo().getCatId());

        // request multiple file
        ProgressRequestBody audioRequest = new ProgressRequestBody(audioFile, this);
        RequestBody thumbnailRequest = RequestBody.create(
                MediaType.parse("image"),
                audioThumbNailFile
        );

        MultipartBody.Part audioBody;
        MultipartBody.Part thumbnailBody;

        try {
            audioBody = MultipartBody.Part.createFormData("audio", audioFile.getName().replace(" ", "_"), audioRequest);
            thumbnailBody = MultipartBody.Part.createFormData("audio", audioThumbNailFile.getName().replace(" ", "_"), thumbnailRequest);
        } catch (Exception e) {
            e.printStackTrace();
            audioBody = MultipartBody.Part.createFormData("audio", audioFile.getName(), audioRequest);
            thumbnailBody = MultipartBody.Part.createFormData("audio", audioThumbNailFile.getName(), thumbnailRequest);
        }

        call = new RestClient(Constants.BASE_URL, this).get().uploadAudio(
                mapAudioUploadRequest(videoMakerAudioInfo),
                audioBody,
                thumbnailBody);

        call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Log.w(Tag, "response.isSuccessful()");

                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        if (Boolean.parseBoolean(jsonObject.getString("success"))) {
                            Log.w(Tag, "SingleAudioInfo uploaded Successfully");
                            Gson gson = new Gson();
                            ResponseAudioInfoOld audioInfo = gson.fromJson(jsonObject.getString("audio"), ResponseAudioInfoOld.class);
                            mediaInfo.getAudioInfo().setAudioId(audioInfo.get_id());
                            uploadVideoPost(postInfo);

                        } else {
                            Log.w(Tag, "uploading failed");
                            stopUploadService();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.w(Tag, "JSONException");
                        stopUploadService();
                    }
                } else {
                    stopUploadService();
                    Log.w(Tag, "Server Error response code" + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.w(Tag, "onFailure");
                if (!call.isCanceled())
                    stopUploadService();
            }
        });
    }

    private HashMap<String, RequestBody> mapAudioUploadRequest(ResponseAudioInfoOld audioInfo) {
        RequestBody folderName = createRequstBody(audioInfo.getFolderName());
        RequestBody userId = createRequstBody(audioInfo.getUserId());
        RequestBody userName = createRequstBody(audioInfo.getUserName());
        RequestBody duration = createRequstBody(audioInfo.getDuration());
        RequestBody title = createRequstBody(audioInfo.getTitle());
        RequestBody catId = createRequstBody(audioInfo.getCatId());

        HashMap<String, RequestBody> map = new HashMap<>();
        map.put("folderName", folderName);
        map.put("userId", userId);
        map.put("userName", userName);
        map.put("title", title);
        map.put("duration", duration);
        map.put("catId", catId);

        return map;
    }

    public File createthumb(Bitmap bm) {
        bm = Bitmap.createScaledBitmap(bm, 100, 100, false);

        File thumbFile = null;

        File fileDirectory = new File(Environment.getExternalStorageDirectory(), Constants.TEMP_IMAGE_FOLDER);
        if (!fileDirectory.exists()) {
            fileDirectory.mkdirs();
        }

        thumbFile = new File(fileDirectory, Util.getSortedStringDate() + "audioThumb.jpeg");
        FileOutputStream ostream1 = null;
        try {
            ostream1 = new FileOutputStream(thumbFile);
            bm.compress(Bitmap.CompressFormat.JPEG, 35, ostream1);
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }
        try {
            ostream1.flush();
            ostream1.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return thumbFile;
    }


    private HashMap<String, RequestBody> mapRequest(VideoMakerPostInfo postInfo) {
        RequestBody folderName = createRequstBody(postInfo.getPostVideoUrl());
        RequestBody postImageUrl = createRequstBody(postInfo.getPostImageUrl());
        RequestBody userId = createRequstBody(postInfo.getUserId());
        RequestBody fullName = createRequstBody(postInfo.getFullName());
        RequestBody userName = createRequstBody(postInfo.getUserName());
        RequestBody userDisplayPicture = createRequstBody(postInfo.getUserDisplayPicture());
        RequestBody location = createRequstBody(postInfo.getLocation());
        RequestBody tags = createRequstBody(postInfo.getTags());
        RequestBody caption = createRequstBody(
                postInfo.getCaption() != null && !postInfo.getCaption().isEmpty()
                        ? postInfo.getCaption() : "");
        RequestBody height = createRequstBody(postInfo.getHeight());
        RequestBody width = createRequstBody(postInfo.getWidth());
        RequestBody duration = createRequstBody(postInfo.getVideoDuration());
        RequestBody localPath = createRequstBody(postInfo.getLocalPath());
        RequestBody catId = createRequstBody("0");
        if (mediaInfo.getAudioInfo() != null && mediaInfo.getAudioInfo().getCatId() != null) {
            catId = createRequstBody(mediaInfo.getAudioInfo().getCatId());
        } else {
            catId = createRequstBody("1");
        }
        RequestBody audioId;

        if (mediaInfo.getAudioInfo() != null && mediaInfo.getAudioInfo().getAudioId() != null) {
            audioId = createRequstBody(mediaInfo.getAudioInfo().getAudioId());
        } else {
            audioId = createRequstBody("1");
        }
        HashMap<String, RequestBody> map = new HashMap<>();
        map.put("folderName", folderName);
        map.put("postImageUrl", postImageUrl);
        map.put("userId", userId);
        map.put("userName", userName);
        map.put("fullName", fullName);
        map.put("userDisplayPicture", userDisplayPicture);
        map.put("location", location);
        map.put("tags", tags);
        map.put("caption", caption);
        map.put("height", height);
        map.put("width", width);
        map.put("duration", duration);
        map.put("localPath", localPath);
        map.put("catId", catId);
        map.put("audioId", audioId);
        return map;
    }

    private RequestBody createRequstBody(String string) {
        return RequestBody.create(
                MultipartBody.FORM, string);
    }


    private void showNotification() {
        final long currentTime = Calendar.getInstance(Locale.ENGLISH).getTimeInMillis();
        notificationId = (int) currentTime;

        // start application
        Intent startAppIntent = new Intent(this, MainActivity.class);
        startAppIntent.setAction(Constants.START_APPLICATION);
        startAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent startAppPendingIntent = PendingIntent.getActivity(this, SERVICE_REQUEST_ID,
                startAppIntent, 0);

        // cancel upload service
        Intent cancelIntent = new Intent(this, UploadVideoSecondService.class);
        cancelIntent.setAction(Constants.CANCEL_SAVE_SERVICE);
        PendingIntent cancelPendingIntent = PendingIntent.getService(this, SERVICE_REQUEST_ID,
                cancelIntent, 0);

        NotificationHelper helper = new NotificationHelper(this);
        mBuilder = helper.getNotificationBuilder();
        mBuilder.setContentTitle(getResources().getString(R.string.app_name))
                .setContentText("Creating video in progress...")
                .setSmallIcon(R.drawable.ic_app_main_notification)
                .setContentIntent(startAppPendingIntent)
                .setOngoing(true)
                .setTicker("Uploading Video...")
                .setSound(null)
                .setProgress(100, 0, false);

        mBuilder.addAction(R.drawable.ic_action_cancel,
                "Cancel", cancelPendingIntent);

        startForeground(notificationId, mBuilder.build());
    }

    private void showUploadNotification() {
        if (timmer != null) {
            isTimerRunning = true;
            timmer.cancel();
        }

        mBuilder.mActions.clear();

        // cancel upload service
        Intent cancelIntent = new Intent(this, UploadVideoSecondService.class);
        cancelIntent.setAction(Constants.CANCEL_SAVE_SERVICE);

        PendingIntent cancelPendingIntent = PendingIntent.getService(this, SERVICE_REQUEST_ID,
                cancelIntent, 0);

        mBuilder.setContentText("Uploading video in progress...")
                .setProgress(100, 0, false);

        mBuilder.addAction(R.drawable.ic_action_cancel,
                "Cancel", cancelPendingIntent);

        mNotificationManager.notify(notificationId, mBuilder.build());
    }

    private void showAudioUploadNotification() {
        if (timmer != null) {
            isTimerRunning = true;
            timmer.cancel();
        }

        mBuilder.mActions.clear();

        // cancel upload service
        Intent cancelIntent = new Intent(this, UploadVideoSecondService.class);
        cancelIntent.setAction(Constants.CANCEL_SAVE_SERVICE);

        PendingIntent cancelPendingIntent = PendingIntent.getService(this, SERVICE_REQUEST_ID,
                cancelIntent, 0);

        mBuilder.setContentText("Uploading video in progress...")
                .setProgress(0, 0, true);

        mBuilder.addAction(R.drawable.ic_action_cancel,
                "Cancel", cancelPendingIntent);

        mNotificationManager.notify(notificationId, mBuilder.build());
    }

    private void showSavingFailedNotification() {
        mBuilder.setContentText("Video Saving Failed, Please! try again")
                .setProgress(0, 0, false);
        mNotificationManager.notify(notificationId, mBuilder.build());
        stopUploadService();
    }

    private void updateNotificationActions(String contentText) {
        mBuilder.mActions.clear();
        mBuilder.setContentText(contentText)
                .setProgress(0, 0, false);

/*
        // done action
        Intent doneIntent = new Intent(this, UploadVideoSecondService.class);
        doneIntent.setAction(Constants.DONE_SAVE_SERVICE);
        PendingIntent donePendingIntent = PendingIntent.getService(this, SERVICE_REQUEST_ID,
                doneIntent, 0);

        mBuilder.addAction(R.drawable.ic_action_done,
                "Done", donePendingIntent);
*/

        mNotificationManager.notify(notificationId, mBuilder.build());
    }

    private void updateNotificationProgress(int progress) {
        mBuilder.setProgress(100, progress, false);
        mNotificationManager.notify(notificationId, mBuilder.build());
    }

    private void showProgress(String s) {
        if (!isTimerRunning) {
            try {
                int totalTime = mediaInfo.getDuration() / 1000;
                int currentTime = 0;
                int hour = 0;
                int min = 0;
                int sec = 0;

                String timePassedAway = s.substring(s.indexOf("time=") + 5, s.indexOf("bitrate=") - 1);
                String[] currentTimeParts = timePassedAway.split(":");
                hour = Integer.parseInt(currentTimeParts[0]);
                min = Integer.parseInt(currentTimeParts[1]);

                try {
                    timePassedAway = currentTimeParts[2].substring(0, currentTimeParts[2].indexOf("."));
                    sec = Integer.parseInt(timePassedAway);
                } catch (Exception e) {
                    sec = Integer.parseInt(currentTimeParts[2]);
                }

                if (hour > 0) {
                    hour = hour * 3600;
                }
                if (min > 0) {
                    min = min * 60;
                }

                currentTime = hour + min + sec;

                currentProgress = (currentTime * 100) / totalTime;
                Log.w(TAG, "total duraion: " + totalTime + " prog: " + currentProgress + " time: " + currentTime);
                timmer.start();
                isTimerRunning = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void killFfmpegProcess(File dest) {
        try {
            FFmpeg.getInstance(getContext()).killRunningProcesses();
            Log.w(Tag, "FFmpeg Stoped");
        } catch (Exception e) {
            Log.w(Tag, "FFmpeg Stoped failed");
            e.printStackTrace();
        }

      /*  try {
            int pid = Process.myPid();
            Process.sendSignal(pid, 15);
            Log.w(Tag, "Process killed Notified");
        } catch (Exception e) {
            Log.w(Tag, "Process killed Notification failed");
            e.printStackTrace();
        }*/
    }

    private synchronized void deleteTempFiles() {
        for (VideoInfo videoInfo : mediaInfo.getInputVideos()) {
            new File(videoInfo.getPath()).delete();
        }
    }

    private void takeFinalAction() {
        deleteTempFiles();
        stopUploadService();
    }

    private void cancelSaveService() {
     /*   try {
            deleteTempFiles();
            if (mediaInfo.getOutputVideoPath() != null) {
                new File(mediaInfo.getOutputVideoPath()).delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        stopUploadService();
    }

    void deleteRecursive(File fileOrDirectory) {

        new DeleteFilesTask(fileOrDirectory).execute();
    }

    private class DeleteFilesTask extends AsyncTask<Void, Void, Void> {

        File fileOrDirectory;

        public DeleteFilesTask(File file) {
            this.fileOrDirectory = file;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (fileOrDirectory.isDirectory())
                for (File child : fileOrDirectory.listFiles())
                    deleteRecursive(child);

            fileOrDirectory.delete();
            return null;
        }
    }


    private void stopUploadService() {
        IS_SERVICE_RUNNING = false;
        stopForeground(true);
        stopSelf(SERVICE_REQUEST_ID);
    }

    @Override
    public void onProgressUpdate(final int percentage) {
        Log.w(Tag, "percentage: " + percentage);
        if ((percentage % 10) == 0 && IS_SERVICE_RUNNING && !isUploadComplete) {
            mBuilder.setProgress(100, percentage, false);
            mNotificationManager.notify(notificationId, mBuilder.build());
        }
    }

    @Override
    public void onError() {
        stopUploadService();
        Log.w(Tag, "onError uploading");
    }

    @Override
    public void onFinish() {
        Log.w(Tag, "onFinish uploading");
    }
}
