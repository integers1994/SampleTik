package com.photex.tiktok.models;

/**
 * Created by Hamza on 08/07/2017.
 */

public class GroupDetail {

    String _id;
    String createdBy;
    String groupName;
    String description;
    String date;
    boolean memberOrNot;


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isMemberOrNot() {
        return memberOrNot;
    }

    public void setMemberOrNot(boolean memberOrNot) {
        this.memberOrNot = memberOrNot;
    }
}
