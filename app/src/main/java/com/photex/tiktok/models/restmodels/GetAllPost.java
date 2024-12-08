package com.photex.tiktok.models.restmodels;


public class GetAllPost {
    String myId;
    String lastId;

    public String getUserId() {
        return myId;
    }

    public void setUserId(String myId) {
        this.myId = myId;
    }

    public String getLastId() {
        return lastId;
    }

    public void setLastId(String lastId) {
        this.lastId = lastId;
    }
}
