package com.photex.tiktok.models.restmodels;

/**
 * Created by Ameer Hamza on 8/4/2016.
 */
public class PostComment {


    String userName;
    String userId;
    String comment;
    String userDisplayPicture;
    String postId;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserDisplayPicture() {
        return userDisplayPicture;
    }

    public void setUserDisplayPicture(String userDisplayPicture) {
        this.userDisplayPicture = userDisplayPicture;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

}
