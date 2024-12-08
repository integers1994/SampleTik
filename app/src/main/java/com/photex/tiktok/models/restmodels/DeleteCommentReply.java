package com.photex.tiktok.models.restmodels;

/**
 * Created by Ameer Hamza on 2/24/2017.
 */

public class DeleteCommentReply {
    String commentReplyId;
    String userId;
    String parentCommentId;

    public String getCommentReplyId() {
        return commentReplyId;
    }

    public void setCommentReplyId(String commentReplyId) {
        this.commentReplyId = commentReplyId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(String parentCommentId) {
        this.parentCommentId = parentCommentId;
    }
}
