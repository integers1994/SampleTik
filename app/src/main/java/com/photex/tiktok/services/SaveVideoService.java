package com.photex.tiktok.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.IBinder;
import android.os.Process;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import android.util.Log;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.photex.tiktok.R;
import com.photex.tiktok.activities.VideoCreateActivity;
import com.photex.tiktok.models.MediaInfo;
import com.photex.tiktok.models.VideoInfo;
import com.photex.tiktok.utils.Constants;
import com.photex.tiktok.utils.NotificationHelper;
import com.photex.tiktok.utils.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


/**
 * Created by Muhammad Noman on 7/26/2017.
 */

public class SaveVideoService extends Service {
    public static boolean IS_SERVICE_RUNNING = false;
    public static final int SERVICE_REQUEST_ID = 1;
    private MediaInfo mediaInfo;
    private String Tag = "SaveVideoService";

    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotificationManager;
    private int notificationId;

    private String TAG = "SaveVideoService";
    private CountDownTimer timmer;
    private int currentProgress = 0;
    private boolean isTimerRunning;

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        timmer = new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                if (IS_SERVICE_RUNNING && currentProgress > 1) {
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
        if (intent.getAction().equals(Constants.START_SAVE_SERVICE)) {
            initializeData(intent);
            showNotification();
            saveVideo();
        } else if (intent.getAction().equals(Constants.DONE_SAVE_SERVICE)) {
            takeFinalAction();
        } else if (intent.getAction().equals(Constants.CANCEL_SAVE_SERVICE)) {
            cancelSaveService();
        }
        return START_NOT_STICKY;
    }

    private void initializeData(Intent intent) {
        if (intent != null && intent.hasExtra(Constants.EXRTA_MEDIA_INFO)) {
            mediaInfo = (MediaInfo) intent.getExtras().get(Constants.EXRTA_MEDIA_INFO);
            /*ffmpeg = VideoCreateActivity.ffmpeg;*/
        }
    }

    private void saveVideo() {

        //Check  is have save permission
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

/*            saveVideoCommand = new String[]{
                    "-y",
                    "-i", EXRTA_MEDIA_INFO.getInputVideos().get(0).getPath(),
                    "-i", EXRTA_MEDIA_INFO.getInputVideos().get(1).getPath(),
                    "-i", EXRTA_MEDIA_INFO.getAudioFilePath(),

                    "-filter_complex", "[0:v][1:v] concat=n=2:v=1:a=0 [v]",
                    "-filter_complex", "[2:a] concat=n=1:v=0:a=1 [a]",
                    "-map", "[v]",
                    "-map", "[a]",

                    "-t", String.valueOf(EXRTA_MEDIA_INFO.getDuration()),
                    "-r", "25",
                    "-vcodec", "libx264",
                    dest.getAbsolutePath()};*/

     /*       saveVideoCommand = new String[]{
                    "-y",
                    "-i", EXRTA_MEDIA_INFO.getInputVideos().get(0).getPath(),
                    "-i", EXRTA_MEDIA_INFO.getAudioFilePath(),
                    "-t", String.valueOf(EXRTA_MEDIA_INFO.getDuration()),
                    "-r", "25",
                    "-vcodec", "libx264",
                    dest.getAbsolutePath()};*/

        } catch (Exception e) {
            e.printStackTrace();
        }

        execFFmpegCommand(saveVideoCommand, dest, "Saving Video");
    }

    private void execFFmpegCommand(final String[] command, final File dest, final String operation) {
        /* try {*/
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
                    updateNotificationActions("Video Saved in \"MyTikTok/Videos\" Folder");
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
            e.printStackTrace();
            Log.d(TAG, "FFmpegCommandAlreadyRunningException : ffmpeg " + command);
            showSavingFailedNotification();
//            tempFile = dest;
            e.printStackTrace();
        }


/*catch (FFmpegCommandAlreadyRunningException e) {
            // do nothing for now
            Log.d(TAG, "FFmpegCommandAlreadyRunningException : ffmpeg " + command);
            showSavingFailedNotification();
//            tempFile = dest;
            e.printStackTrace();
        }*/

    }


    private Context getContext() {
        return SaveVideoService.this;
    }


    private void showNotification() {
        final long currentTime = Calendar.getInstance(Locale.ENGLISH).getTimeInMillis();
        notificationId = (int) currentTime;

        // start application
        Intent startAppIntent = new Intent(this, VideoCreateActivity.class);
        startAppIntent.setAction(Constants.START_APPLICATION);
        startAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent startAppPendingIntent = PendingIntent.getActivity(this, SERVICE_REQUEST_ID,
                startAppIntent, 0);

        // cancel upload service
        Intent cancelIntent = new Intent(this, SaveVideoService.class);
        cancelIntent.setAction(Constants.CANCEL_SAVE_SERVICE);
        PendingIntent cancelPendingIntent = PendingIntent.getService(this, SERVICE_REQUEST_ID,
                cancelIntent, 0);
        NotificationHelper helper = new NotificationHelper(this);
        mBuilder = helper.getNotificationBuilder();
        mBuilder.setContentTitle(getResources().getString(R.string.app_name))
                .setContentText("Saving video in progress...")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(startAppPendingIntent)
                .setOngoing(true)
                .setTicker("Saving Video...")
                .setSound(null)
                .setProgress(100, 0, false);

        mBuilder.addAction(R.drawable.menu_cancel,
                "Cancel", cancelPendingIntent);

        startForeground(notificationId, mBuilder.build());
    }


    private void resetNotification() {
        mBuilder.setContentText("Saving video in progress...")
                .setProgress(100, 0, false);

        mNotificationManager.notify(notificationId, mBuilder.build());
    }

    private void showSavingFailedNotification() {
        mBuilder.setContentText("Video Saving Failed, Please! try again")
                .setProgress(0, 0, false);
        mNotificationManager.notify(notificationId, mBuilder.build());
    }

    private void updateNotificationActions(String contentText) {
        if (isTimerRunning) {
            timmer.cancel();
        }
        mBuilder.setContentText(contentText)
                .setProgress(0, 0, false);
        mBuilder.mActions.clear();

        // done action
        Intent doneIntent = new Intent(this, SaveVideoService.class);
        doneIntent.setAction(Constants.DONE_SAVE_SERVICE);
        PendingIntent donePendingIntent = PendingIntent.getService(this, SERVICE_REQUEST_ID,
                doneIntent, 0);

/*        // Play Video action
        Intent playMediaIntent = new Intent(this, VideoPlayerActivity.class);
        playMediaIntent.putExtra(Constants.TEMP_AUDIO_FOLDER, EXRTA_MEDIA_INFO.getURL());
        playMediaIntent.setAction(Constants.PLAY_VIDEO_SERVICE);
//        playMediaIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent playMediaPendingIntent = PendingIntent.getActivity(this, SERVICE_REQUEST_ID,
                playMediaIntent, 0);*/

        mBuilder.addAction(R.drawable.done_upload_action,
                "Done", donePendingIntent);
//        mBuilder.addAction(R.drawable.ic_play_edited_video,
//                "Play", playMediaPendingIntent);

        mNotificationManager.notify(notificationId, mBuilder.build());

/*        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(Constants.VIDEO_EDITING_COMPLETE);
        broadCastIntent.putExtra(Constants.SERVICE_MESSAGE, contentText);
        broadCastIntent.putExtra(Constants.MEDIA_INFO, EXRTA_MEDIA_INFO.getURL());
        sendBroadcast(broadCastIntent);*/

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
            }
        }
    }

    private void killFfmpegProcess(File dest) {
/*
        try {
            ffmpeg.killRunningProcesses();
            Log.w(Tag, "FFmpeg Stoped");
        } catch (Exception e) {
            Log.w(Tag, "FFmpeg Stoped failed");
            e.printStackTrace();
        }
*/

        try {
            int pid = Process.myPid();
            Process.sendSignal(pid, 15);
            Log.w(Tag, "Process killed Notified");
        } catch (Exception e) {
            Log.w(Tag, "Process killed Notification failed");
            e.printStackTrace();
        }
    }

    private synchronized void deleteTempFiles() {
        for (VideoInfo videoInfo : mediaInfo.getInputVideos()) {
            new File(videoInfo.getPath()).delete();
        }
    }

    private void takeFinalAction() {
        deleteTempFiles();
        stopSaveService();
    }

    private void cancelSaveService() {
        try {
            deleteTempFiles();
            if (mediaInfo.getOutputVideoPath() != null) {
                new File(mediaInfo.getOutputVideoPath()).delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        stopSaveService();
    }

    private void stopSaveService() {
        IS_SERVICE_RUNNING = false;
        stopForeground(true);
        stopSelf(SERVICE_REQUEST_ID);
    }


}
