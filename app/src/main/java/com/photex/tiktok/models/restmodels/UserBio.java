package com.photex.tiktok.models.restmodels;

import androidx.annotation.NonNull;

/**
 * Created by Aurang Zeb on 08-Dec-16.
 */

public class UserBio {
    @NonNull
    String userId;
    @NonNull
    private String bio;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
