package com.photex.tiktok.models.restmodels;

/**
 * Created by Hamza on 12/07/2017.
 */

public class GetGroupPosts {

    String groupId;
    String lastId;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getLastId() {
        return lastId;
    }

    public void setLastId(String lastId) {
        this.lastId = lastId;
    }
}
