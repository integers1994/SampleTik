package com.photex.tiktok.models;

import java.io.Serializable;

/**
 * Created by Ameer Hamza on 8/5/2016.
 */
public class UserProfileInfo implements Serializable {

    String userId;
    String userName;//full name
    String userDisplayPicture;
    String fullName;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserDisplayPicture() {
        return userDisplayPicture;
    }

    public void setUserDisplayPicture(String userDisplayPicture) {
        this.userDisplayPicture = userDisplayPicture;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


}
