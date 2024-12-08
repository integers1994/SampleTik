package com.photex.tiktok.models;

/**
 * Created by Aurang Zeb on 7/26/2016.
 */
public  class FeedItem {
    public int likesCount;
    public boolean isLiked;

    public FeedItem(int likesCount, boolean isLiked) {
        this.likesCount = likesCount;
        this.isLiked = isLiked;
    }
}