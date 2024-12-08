package com.photex.tiktok.utils;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

public class CleanFoldersTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = CleanFoldersTask.class.getSimpleName();
    private ArrayList<String> folderList;

    public CleanFoldersTask(ArrayList<String> folderName) {
        this.folderList = folderName;

    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            for (int i = 0; i < folderList.size(); i++) {
                File file = new File(folderList.get(i));
                if (file.exists()){
                    boolean success = deleteRecursive(file);
                    Log.d(TAG, "folder delete " + success);
                }
            }

        } catch (Exception e) {
            Log.d(TAG, "file delete exception" + e.toString());
            e.printStackTrace();
        }
        return null;
    }

    private boolean deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        return fileOrDirectory.delete();
    }
}
