package com.photex.tiktok.models.restmodels;

/**
 * Created by Ameer Hamza on 10/3/2016.
 */
public class GetSearchByName {

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getLastId() {
        return lastId;
    }

    public void setLastId(String lastId) {
        this.lastId = lastId;
    }

    String keyword;
    String lastId;
}
