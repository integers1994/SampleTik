package com.photex.tiktok.models.restmodels;

/**
 * Created by Hamza on 13/07/2017.
 */

public class ActiveAdId {
    String _id;
    String ad_network_name;
    boolean status;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getAd_network_name() {
        return ad_network_name;
    }

    public void setAd_network_name(String ad_network_name) {
        this.ad_network_name = ad_network_name;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
