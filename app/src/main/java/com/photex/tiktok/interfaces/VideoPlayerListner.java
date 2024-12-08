package com.photex.tiktok.interfaces;

public interface VideoPlayerListner {
    void onVideoBuffering();

    void onVideoPlaying();

    void onVideoPause();

    void onVideoComplete();
}