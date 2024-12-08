package com.photex.tiktok.models;

import java.io.Serializable;

/**
 * Created by Aurang Zeb on 08-Dec-16.
 */

public class Place implements Serializable {
    String _id;
    private String country;
    private String city;
    private String address;
    private String from;
    private String to;
    private boolean isCurrentlyLiving = true;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public boolean isCurrentlyLiving() {
        return isCurrentlyLiving;
    }

    public void setCurrentlyLiving(boolean currentlyLiving) {
        isCurrentlyLiving = currentlyLiving;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
