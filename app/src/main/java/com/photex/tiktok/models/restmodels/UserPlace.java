package com.photex.tiktok.models.restmodels;

/**
 * Created by Aurang Zeb on 31-Dec-16.
 */

public class UserPlace {
    private String userId;
    private String country;
    private String city;
    private String address;
    private String from;
    private String to;
    private boolean isCurrentlyLiving = true;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public boolean isCurrentlyLiving() {
        return isCurrentlyLiving;
    }

    public void setCurrentlyLiving(boolean currentlyLiving) {
        isCurrentlyLiving = currentlyLiving;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
