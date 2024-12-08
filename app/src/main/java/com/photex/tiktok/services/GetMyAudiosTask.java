package com.photex.tiktok.services;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.photex.tiktok.models.AudioInfo;
import com.photex.tiktok.setting.SettingManager;

import java.io.File;
import java.util.ArrayList;

public class GetMyAudiosTask extends AsyncTask<Void, Void, Void> {
    private ArrayList<AudioInfo> audioInfoList;

    private Context context;
    private AudioLoadListner audioLoadListner;

    public GetMyAudiosTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        audioInfoList = new ArrayList<>();
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION
        };

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {

                    String song_name = cursor.getString(
                            cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    String fullpath = cursor.getString(
                            cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    int duration = cursor.getInt(
                            cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)) / 1000;


//                    int song_id = cursor.getInt(cursor
//                            .getColumnIndex(MediaStore.SingleAudioInfo.Media._ID));
//                    String album_name = cursor.getString(cursor
//                            .getColumnIndex(MediaStore.SingleAudioInfo.Media.ALBUM));
//                    int album_id = cursor.getInt(cursor
//                            .getColumnIndex(MediaStore.SingleAudioInfo.Media.ALBUM_ID));
//                    String artist_name = cursor.getString(cursor
//                            .getColumnIndex(MediaStore.SingleAudioInfo.Media.ARTIST));
//                    int artist_id = cursor.getInt(cursor
//                            .getColumnIndex(MediaStore.SingleAudioInfo.Media.ARTIST_ID));

                    if(duration > 0){
                        AudioInfo audioInfo = new AudioInfo();
                        audioInfo.setTitle(song_name);
                        audioInfo.setOwner(SettingManager.getUserName(context));
                        audioInfo.setPath(fullpath);
                        audioInfo.setDuration(duration);
                        audioInfoList.add(audioInfo);
                    }
                } while (cursor.moveToNext());

            }
            cursor.close();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        audioLoadListner.onAudioLoaded(audioInfoList);
    }

    public GetMyAudiosTask setAudioLoadListner(AudioLoadListner audioLoadListner) {
        this.audioLoadListner = audioLoadListner;
        return this;
    }

    public interface AudioLoadListner {
        void onAudioLoaded(ArrayList<AudioInfo> audioInfoList);
    }

}
