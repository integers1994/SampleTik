package com.photex.tiktok.models;

/**
 * Created by Hamza on 17/07/2017.
 */

public class ServerAd {

    String _id;
    String ad_network_id;
    String ad_network_name;
    String ad_name;
    String ad_small_image;
    String description;
    String ad_large_image;
    String app_url;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getAd_network_id() {
        return ad_network_id;
    }

    public void setAd_network_id(String ad_network_id) {
        this.ad_network_id = ad_network_id;
    }

    public String getAd_network_name() {
        return ad_network_name;
    }

    public void setAd_network_name(String ad_network_name) {
        this.ad_network_name = ad_network_name;
    }

    public String getAd_name() {
        return ad_name;
    }

    public void setAd_name(String ad_name) {
        this.ad_name = ad_name;
    }

    public String getAd_small_image() {
        return ad_small_image;
    }

    public void setAd_small_image(String ad_small_image) {
        this.ad_small_image = ad_small_image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAd_large_image() {
        return ad_large_image;
    }

    public void setAd_large_image(String ad_large_image) {
        this.ad_large_image = ad_large_image;
    }

    public String getApp_url() {
        return app_url;
    }

    public void setApp_url(String app_url) {
        this.app_url = app_url;
    }
}
