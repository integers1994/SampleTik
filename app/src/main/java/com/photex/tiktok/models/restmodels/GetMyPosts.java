package com.photex.tiktok.models.restmodels;

/**
 * Created by Ameer Hamza on 7/29/2016.
 */
public class GetMyPosts {

    String myId; // UserId
    String lastId; // Last PostId


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
