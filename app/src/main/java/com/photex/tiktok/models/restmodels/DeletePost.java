package com.photex.tiktok.models.restmodels;

/**
 * Created by Ameer Hamza on 8/2/2016.
 */
public class DeletePost {

    String postId;
    String userId;

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

}
