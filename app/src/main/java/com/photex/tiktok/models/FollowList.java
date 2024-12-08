package com.photex.tiktok.models;

/**
 * Created by Ameer Hamza on 8/8/2016.
 */
public class FollowList {


    String _id;
    String userId;
    String followerId;
    String date;
    String followerDisplayPicture;
    String followerFullName;
    boolean isFollowed;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFollowerId() {
        return followerId;
    }

    public void setFollowerId(String followerId) {
        this.followerId = followerId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFollowerDisplayPicture() {
        return followerDisplayPicture;
    }

    public void setFollowerDisplayPicture(String followerDisplayPicture) {
        this.followerDisplayPicture = followerDisplayPicture;
    }

    public String getFollowerFullName() {
        return followerFullName;
    }

    public void setFollowerFullName(String followerFullName) {
        this.followerFullName = followerFullName;
    }

    public boolean isFollowed() {
        return isFollowed;
    }

    public void setFollowed(boolean followed) {
        isFollowed = followed;
    }


}
