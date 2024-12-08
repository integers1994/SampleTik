package com.photex.tiktok.models.restmodels;

/**
 * Created by Ameer Hamza on 8/9/2016.
 */
public class GetUserFollowers {


    String myId;
    String lastId;
    String userId;

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

    public String getLastId() {
        return lastId;
    }

    public void setLastId(String lastId) {
        this.lastId = lastId;
    }

}
