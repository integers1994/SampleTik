package com.photex.tiktok.models.restmodels;

/**
 * Created by Ameer Hamza on 8/2/2016.
 */
public class AllPosts {


    String lastId;
    String myId;
    String ad_network_id;
    String last_ad_id;


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

    public String getAd_network_id() {
        return ad_network_id;
    }

    public void setAd_network_id(String ad_network_id) {
        this.ad_network_id = ad_network_id;
    }

    public String getLast_ad_id() {
        return last_ad_id;
    }

    public void setLast_ad_id(String last_ad_id) {
        this.last_ad_id = last_ad_id;
    }
}
