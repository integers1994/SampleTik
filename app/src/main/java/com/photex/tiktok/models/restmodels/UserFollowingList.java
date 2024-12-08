package com.photex.tiktok.models.restmodels;

/**
 * Created by Aurang Zeb on 8/10/2016.
 */
public class UserFollowingList {
    String userId;
    String myId;
    String lastId;

    public String getLastId() {
        return lastId;
    }

    public void setLastId(String lastId) {
        this.lastId = lastId;
    }

    public String getMyId() {
        return myId;
    }

    public void setMyId(String myId) {
        this.myId = myId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


}
