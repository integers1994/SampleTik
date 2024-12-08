package com.photex.tiktok.models.restmodels;

/**
 * Created by Ameer Hamza on 8/9/2016.
 */
public class PostLike {

    String userName;
    String userId;
    String userDisplayPicture;
    String postId;
    String fullName;
    String postBy;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }



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

    public String getPostBy() {
        return postBy;
    }

    public void setPostBy(String postBy) {
        this.postBy = postBy;
    }
}
