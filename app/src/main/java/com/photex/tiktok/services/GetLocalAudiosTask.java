package com.photex.tiktok.services;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;

import com.photex.tiktok.models.AudioInfo;

import java.io.File;
import java.util.ArrayList;

public class GetLocalAudiosTask extends AsyncTask<Void, Void, Void> {
    private String[] audioFileNames;
    private File[] audioFiles;
    private ArrayList<AudioInfo> audioInfoList;

    private Context context;
    private File audioDirectory;
    private AudioLoadListner audioLoadListner;

    public GetLocalAudiosTask(Context context, File audioDirectory) {
        this.context = context;
        this.audioDirectory = audioDirectory;
    }

    @Override
    protected void onPreExecute() {
        audioFileNames = audioDirectory.list();
        audioFiles = audioDirectory.listFiles();
        audioInfoList = new ArrayList<>();
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        for (int i = audioFileNames.length - 1; i >= 0; i--) {
            int duration = 0;
            MediaPlayer mp = MediaPlayer.create(context, Uri.parse(audioFiles[i].toString()));
            if (mp != null)
                duration = mp.getDuration() / 1000;
            else
                duration = 1;

            AudioInfo audioInfo = new AudioInfo();
            audioInfo.setTitle(audioFileNames[i]);
            audioInfo.setPath(audioFiles[i].getAbsolutePath());
            audioInfo.setOwner("TikTok Audios");
            audioInfo.setDuration(duration);
            audioInfoList.add(audioInfo);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        audioLoadListner.onAudioLoaded(audioInfoList);
    }

    public GetLocalAudiosTask setAudioLoadListner(AudioLoadListner audioLoadListner) {
        this.audioLoadListner = audioLoadListner;
        return this;
    }

    public interface AudioLoadListner {
        void onAudioLoaded(ArrayList<AudioInfo> audioInfoList);
    }

}
