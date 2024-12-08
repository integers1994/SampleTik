package com.photex.tiktok.models.restmodels;

/**
 * Created by Ameer Hamza on 8/10/2016.
 */
public class PostUnlike {

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    String postId;
    String userId;
}
