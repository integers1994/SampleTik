package com.photex.tiktok.models;

import java.io.Serializable;

public class AudioInfo implements Serializable {
    private String audioId;
    private String title;
    private String owner;
    private int duration; // seconds
    private String thumbNail;
    private String path; // actual file path
    private String catId;



    private boolean isShootbtnVisible;
    private boolean isLocalAudio;  // if true than upload both audio and video separately to server as well


    public String getAudioId() {
        return audioId;
    }

    public void setAudioId(String audioId) {
        this.audioId = audioId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getThumbNail() {
        return thumbNail;
    }

    public void setThumbNail(String thumbNail) {
        this.thumbNail = thumbNail;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isShootBtnVisible() {
        return isShootbtnVisible;
    }

    public void setShootbtnVisible(boolean shootbtnVisible) {
        isShootbtnVisible = shootbtnVisible;
    }

    public boolean isLocalAudio() {
        return isLocalAudio;
    }

    public void setLocalAudio(boolean localAudio) {
        isLocalAudio = localAudio;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }


}
