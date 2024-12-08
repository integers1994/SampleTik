package com.photex.tiktok.models;

/**
 * Created by Aurang Zeb on 8/10/2016.
 */
public class FollowingList {
    String _id;
    String userId;
    String followingId;
    String date;
    String followingDisplayPicture;
    String followingFullName;
    String followingUserName;
    boolean isFollowed;

    public boolean isFollowed() {
        return isFollowed;
    }

    public void setFollowed(boolean followed) {
        isFollowed = followed;
    }


    public String getFollowingFullName() {
        return followingFullName;
    }

    public void setFollowingFullName(String followingFullName) {
        this.followingFullName = followingFullName;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFollowingDisplayPicture() {
        return followingDisplayPicture;
    }

    public void setFollowingDisplayPicture(String followingDisplayPicture) {
        this.followingDisplayPicture = followingDisplayPicture;
    }

    public String getFollowingUserName() {
        return followingUserName;
    }

    public void setFollowingUserName(String followingUserName) {
        this.followingUserName = followingUserName;
    }

    public String getFollowingId() {
        return followingId;
    }

    public void setFollowingId(String followingId) {
        this.followingId = followingId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
