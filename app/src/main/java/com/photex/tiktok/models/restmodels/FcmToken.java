package com.photex.tiktok.models.restmodels;

/**
 * Created by Ameer Hamza on 12/13/2016.
 */

public class FcmToken {
    String userId , fcmKey;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFcmKey() {
        return fcmKey;
    }

    public void setFcmKey(String fcmKey) {
        this.fcmKey = fcmKey;
    }
}