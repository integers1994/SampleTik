package com.photex.tiktok.models;

import java.io.Serializable;

public class VideoInfo implements Serializable {
    private  String path;
    private  int duration;
    private float currentProgress;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public float getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(float currentProgress) {
        this.currentProgress = currentProgress;
    }
}
