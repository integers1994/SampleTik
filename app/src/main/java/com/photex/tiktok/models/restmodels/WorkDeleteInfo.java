package com.photex.tiktok.models.restmodels;

/**
 * Created by Aurang Zeb on 22-Dec-16.
 */

public class WorkDeleteInfo {
    String userId;
    private String workId;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getWorkId() {
        return workId;
    }

    public void setWorkId(String workId) {
        this.workId = workId;
    }
}
