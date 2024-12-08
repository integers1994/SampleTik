package com.photex.tiktok.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;

import com.photex.tiktok.R;

import java.io.File;

public class FileUtils {


    public static File getAppMainDirectory(Context mContext) {
        File myDirectory = new File(Environment.getExternalStorageDirectory(),
                mContext.getString(R.string.app_name));

        if (!myDirectory.exists()) {
            boolean result = myDirectory.mkdirs();

            if (!result) {
                myDirectory.mkdir();
            }
        }
        return myDirectory;
    }


    public static File getAppScreenShotDirectory(Context mContext) {
        File myDirectory = new File(Environment.
                getExternalStorageDirectory(),
                mContext.getString(R.string.app_name)
                        + File.separator + "screenshots");

        if (!myDirectory.exists()) {
            boolean result = myDirectory.mkdirs();

            if (!result) {
                myDirectory.mkdir();
            }
        }
        return myDirectory;
    }

    public static File getTempVideoFileName(Context mContext) {


        File myDirectory = new File(Environment.
                getExternalStorageDirectory(),
                mContext.getString(R.string.app_name)
                        + File.separator + Constants.TEMP_VIDEO_FOLDER);

        if (!myDirectory.exists()) {
            boolean result = myDirectory.mkdirs();

            if (!result) {
                myDirectory.mkdir();
            }
        }

        return new File(myDirectory,
                Util.getSortedStringDate() + "mergerd.mp4");
    }

    public static File getTempFolder(Context mContext) {


        File myDirectory = new File(Environment.
                getExternalStorageDirectory(),
                mContext.getString(R.string.app_name)
                        + File.separator + Constants.TEMP_FOLDER);

        if (!myDirectory.exists()) {
            boolean result = myDirectory.mkdirs();

            if (!result) {
                myDirectory.mkdir();
            }
        }

        return myDirectory;
    }



    public static File getTempM3U8FileName(Context mContext) {


        File myDirectory = new File(Environment.
                getExternalStorageDirectory(),
                mContext.getString(R.string.app_name)
                        + File.separator + Constants.TEMP_VIDEO_M3U8_FOLDER +
                        File.separator + Util.getSortedStringDate());

        if (!myDirectory.exists()) {
            boolean result = myDirectory.mkdirs();

            if (!result) {
               boolean otherResult= myDirectory.mkdir();
            }
        }

        return new File(myDirectory,
                Util.getSortedStringDate() + "index.m3u8");
    }


    public static File getCompressFileDir(Activity mContext) {


        File myDirectory = new File(Environment.
                getExternalStorageDirectory(),
                mContext.getString(R.string.app_name)
                        + File.separator + Constants.TEMP_VIDEO_COMPRESS);

        if (!myDirectory.exists()) {
            boolean result = myDirectory.mkdirs();

            if (!result) {
                myDirectory.mkdir();
            }
        }

        return myDirectory;
    }

    public static File getCleanedMp3File(Activity mContext) {


        File myDirectory = new File(Environment.
                getExternalStorageDirectory(),
                mContext.getString(R.string.app_name)
                        + File.separator + Constants.TEMP_AUDIO_FOLDER);

        if (!myDirectory.exists()) {
            boolean result = myDirectory.mkdirs();

            if (!result) {
                myDirectory.mkdir();
            }
        }

        return new File(myDirectory,
                Util.getSortedStringDate() + "cleaned.mp3");
    }

    public static File getTempImageDirectory(Activity mContext) {


        File myDirectory = new File(Environment.
                getExternalStorageDirectory(),
                mContext.getString(R.string.app_name)
                        + File.separator + Constants.TEMP_IMAGE_FOLDER);

        if (!myDirectory.exists()) {
            boolean result = myDirectory.mkdirs();

            if (!result) {
                myDirectory.mkdir();
            }
        }

        return new File(myDirectory,
                Util.getSortedStringDate() + "thumb.jpeg");
    }

    public static File getCropImagePath(Activity mContext) {


        File myDirectory = new File(Environment.
                getExternalStorageDirectory(),
                mContext.getString(R.string.app_name)
                        + File.separator + Constants.TEMP_IMAGE_CROP_FOLDER);

        if (!myDirectory.exists()) {
            boolean result = myDirectory.mkdirs();

            if (!result) {
                myDirectory.mkdir();
            }
        }

        return new File(myDirectory,
                Util.getSortedStringDate() + ".jpeg");
    }

}
