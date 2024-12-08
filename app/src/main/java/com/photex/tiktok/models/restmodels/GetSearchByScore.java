package com.photex.tiktok.models.restmodels;

/**
 * Created by Ameer Hamza on 10/3/2016.
 */
public class GetSearchByScore {

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public int getSkip() {
        return skip;
    }

    public void setSkip(int skip) {
        this.skip = skip;
    }

    public String getMyId() {
        return myId;
    }

    public void setMyId(String myId) {
        this.myId = myId;
    }

    String keyword;
    int skip;
    String myId;
}
