package com.photex.tiktok.models.restmodels;

/**
 * Created by Ameer Hamza on 1/11/2017.
 */

public class GetAllReplies {

    String parentCommentId;
    String lastId;

    public String getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(String parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public String getLastId() {
        return lastId;
    }

    public void setLastId(String lastId) {
        this.lastId = lastId;
    }
}

