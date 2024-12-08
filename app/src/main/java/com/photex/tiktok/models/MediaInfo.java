package com.photex.tiktok.models;

import java.io.Serializable;
import java.util.ArrayList;

public class MediaInfo implements Serializable {

    private AudioInfo audioInfo;
    private ArrayList<VideoInfo> inputVideos;
    private String outputVideoPath;
    private String title;
    private int duration;

    //For local usage


    public MediaInfo() {

        this.duration = 0;
    }

    public AudioInfo getAudioInfo() {
        return audioInfo;
    }

    public void setAudioInfo(AudioInfo audioInfo) {
        this.audioInfo = audioInfo;
    }

    public ArrayList<VideoInfo> getInputVideos() {
        return inputVideos;
    }

    public void setInputVideos(ArrayList<VideoInfo> inputVideos) {
        this.inputVideos = inputVideos;
    }

    public String getOutputVideoPath() {
        return outputVideoPath;
    }

    public void setOutputVideoPath(String outputVideoPath) {
        this.outputVideoPath = outputVideoPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }



}
