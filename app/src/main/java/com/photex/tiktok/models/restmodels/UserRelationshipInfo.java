package com.photex.tiktok.models.restmodels;

/**
 * Created by Aurang Zeb on 07-Dec-16.
 */

public class UserRelationshipInfo {

    String userId;
    private String maritalStatus;
    private boolean isMaritalStatusPrivate = false;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public boolean isMaritalStatusPrivate() {
        return isMaritalStatusPrivate;
    }

    public void setMaritalStatusPrivate(boolean maritalStatusPrivate) {
        isMaritalStatusPrivate = maritalStatusPrivate;
    }


}
