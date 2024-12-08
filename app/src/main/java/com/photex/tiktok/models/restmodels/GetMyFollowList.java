package com.photex.tiktok.models.restmodels;

/**
 * Created by Ameer Hamza on 8/8/2016.
 */
public class GetMyFollowList {

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

    String myId;
    String lastId;

}
