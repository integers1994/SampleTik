package com.photex.tiktok.models.restmodels;

/**
 * Created by Aurang Zeb on 07-Dec-16.
 */

public class UserContactInfo {

    String userId;
    private String emailId;
    private String phoneNo;
    private String web;
    private boolean isPhoneNoPrivate = false;
    private boolean isWebPrivate = false;
    private boolean isEmailPrivate = false;

    public boolean isEmailPrivate() {
        return isEmailPrivate;
    }

    public void setEmailPrivate(boolean emailPrivate) {
        isEmailPrivate = emailPrivate;
    }

    public String getUserId() {

        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public boolean isPhoneNoPrivate() {
        return isPhoneNoPrivate;
    }

    public void setPhoneNoPrivate(boolean phoneNoPrivate) {
        isPhoneNoPrivate = phoneNoPrivate;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public boolean isWebPrivate() {
        return isWebPrivate;
    }

    public void setWebPrivate(boolean webPrivate) {
        isWebPrivate = webPrivate;
    }


}
