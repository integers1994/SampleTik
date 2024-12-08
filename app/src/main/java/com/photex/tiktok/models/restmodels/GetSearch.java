package com.photex.tiktok.models.restmodels;

/**
 * Created by Ameer Hamza on 9/9/2016.
 */
public class GetSearch {

    public String getMyId() {
        return myId;
    }

    public void setMyId(String myId) {
        this.myId = myId;
    }

    public String getLastId() {
        return posts;
    }

    public void setLastId(String lastId) {
        this.posts = lastId;
    }

    String myId;
    String posts;
    String skip;

    public String getSkip() {
        return skip;
    }

    public void setSkip(String skip) {
        this.skip = skip;
    }
}