package com.photex.tiktok.services;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.photex.tiktok.utils.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MoveLocalAudiosTask extends AsyncTask<Void, Void, Void> {
    private Context context;
    private File audioDirectory;
    private AudioMovedListner audioMovedListner;
    private boolean isSuccess;

    public MoveLocalAudiosTask(Context context, File audioDirectory) {
        this.context = context;
        this.audioDirectory = audioDirectory;
        this.isSuccess = true;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        for (int i = 0; i < Constants.audiolist.length; i++) {
            String name = context.getResources().getResourceEntryName(Constants.audiolist[i]);
            if (!moveAudioToLocalStorage(name, Constants.audiolist[i])) {
                isSuccess = false;
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (isSuccess) {
            audioMovedListner.onSuccess();
        } else {
            audioMovedListner.onFailure();
        }

    }

    private boolean moveAudioToLocalStorage(String name, int rawvalue) {
        boolean isAudioMoved = true;
        name = name + ".mp3";
        File audioFile = new File(audioDirectory, name);
        if (!audioFile.exists()) {
            InputStream in = null;
            FileOutputStream out = null;
            try {
                in = context.getResources().openRawResource(rawvalue);
                out = new FileOutputStream(audioFile);
                byte[] buff = new byte[1024];
                int read = 0;

                while ((read = in.read(buff)) > 0) {
                    out.write(buff, 0, read);
                }
            } catch (IOException e) {
                e.printStackTrace();
                isAudioMoved = false;
            } finally {
                try {
                    if (in != null)
                        in.close();
                    if (out != null)
                        out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return isAudioMoved;
    }


    public MoveLocalAudiosTask setAudioMovedListner(AudioMovedListner audioMovedListner) {
        this.audioMovedListner = audioMovedListner;
        return this;
    }

    public interface AudioMovedListner {
        void onSuccess();

        void onFailure();
    }

}
