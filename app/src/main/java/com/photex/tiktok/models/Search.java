package com.photex.tiktok.models;

import java.io.Serializable;

/**
 * Created by Ameer Hamza on 9/9/2016.
 */
public class Search implements Serializable{


    String _id;
    String userName;
    String fullName;
    String displayPicture;
    String followersCount;
    String totalPostsCount;
    String country;
    boolean isFollowed;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDisplayPicture() {
        return displayPicture;
    }

    public void setDisplayPicture(String displayPicture) {
        this.displayPicture = displayPicture;
    }

    public boolean isFollowed() {
        return isFollowed;
    }

    public void setFollowed(boolean followed) {
        isFollowed = followed;
    }

    public String getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(String followersCount) {
        this.followersCount = followersCount;
    }

    public String getTotalPostsCount() {
        return totalPostsCount;
    }

    public void setTotalPostsCount(String totalPostsCount) {
        this.totalPostsCount = totalPostsCount;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Search search = (Search) o;

        return get_id().equals(search.get_id());


    }

    @Override
    public int hashCode() {

        return get_id().hashCode();
    }
}
