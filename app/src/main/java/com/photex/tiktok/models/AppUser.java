package com.photex.tiktok.models;


public class AppUser {

    String google_id;
    String picture_url;
    String user_name;
    String user_email_address;
    String time_and_date;
    String fcmkey;


    public String getGoogle_id() {
        return google_id;
    }

    public void setGoogle_id(String google_id) {
        this.google_id = google_id;
    }


    public String getPicture_url() {
        return picture_url;
    }

    public void setPicture_url(String picture_url) {
        this.picture_url = picture_url;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_email_address() {
        return user_email_address;
    }

    public void setUser_email_address(String user_email_address) {
        this.user_email_address = user_email_address;
    }

    public String getTime_and_date() {
        return time_and_date;
    }

    public void setTime_and_date(String time_and_date) {
        this.time_and_date = time_and_date;
    }

    public String getFcmkey() {
        return fcmkey;
    }

    public void setFcmkey(String fcmkey) {
        this.fcmkey = fcmkey;
    }
}
