package com.photex.tiktok.models.restmodels;

/**
 * Created by Ameer Hamza on 8/5/2016.
 */
public class DeleteComment {
    String postId;
    String commentId;

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }
}
