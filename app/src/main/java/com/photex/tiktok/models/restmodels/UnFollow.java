package com.photex.tiktok.models.restmodels;

/**
 * Created by Ameer Hamza on 8/8/2016.
 */
public class UnFollow {


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMyId() {
        return myId;
    }

    public void setMyId(String myId) {
        this.myId = myId;
    }

    String userId;
    String myId;
}
