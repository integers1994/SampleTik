package com.photex.tiktok.models.restmodels;

/**
 * Created by Hamza on 24/08/2017.
 */

public class VideoViewsRequest {

    String postId;
    String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}
