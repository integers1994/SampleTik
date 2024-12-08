package com.photex.tiktok.models.restmodels;

/**
 * Created by Ameer Hamza on 8/4/2016.
 */
public class GetComments {

    String postId;
    String lastId;

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getLastId() {
        return lastId;
    }

    public void setLastId(String lastId) {
        this.lastId = lastId;
    }

}
