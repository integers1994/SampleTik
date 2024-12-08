package com.photex.tiktok.models;

import java.io.Serializable;

/**
 * Created by Ameer Hamza on 7/29/2016.
 */
public class Post implements Serializable {

    String _id;
    String postImageUrl;
    String postVideoUrl;
    String userId;
    String userName;
    String userDisplayPicture;
    String location;
    String caption;
    String fullName;
    String duration;
    int comments;
    int likes;
    int shares;

    String date;
    Comment[] latestComments;
    private boolean isLiked;
    int width;
    int height;
    int allComments;
    int views;

    private boolean isUploading = false;
    private boolean isLocal = false;
    private String postImageUrlLocal;
    private boolean isFollowed = false; //For explore posts

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public boolean isFollowed() {
        return isFollowed;
    }

    public void setFollowed(boolean followed) {
        isFollowed = followed;
    }


    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getAllComments() {
        return allComments;
    }

    public void setAllComments(int allComments) {
        this.allComments = allComments;
    }

    public Comment[] getLatestComments() {
        return latestComments;
    }

    public void setLatestComments(Comment[] latestComments) {
        this.latestComments = latestComments;
    }


    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }


    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }


    public int getLikes() {
        return likes;
    }


    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getShares() {
        return shares;
    }

    public void setShares(int shares) {
        this.shares = shares;
    }

    boolean isLoacal = false;

    public boolean isLoacal() {
        return isLoacal;
    }

    public void setLoacal(boolean loacal) {
        isLoacal = loacal;
    }


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getPostImageUrl() {
        return postImageUrl;
    }

    public void setPostImageUrl(String postImageUrl) {
        this.postImageUrl = postImageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserDisplayPicture() {
        return userDisplayPicture;
    }

    public void setUserDisplayPicture(String userDisplayPicture) {
        this.userDisplayPicture = userDisplayPicture;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isUploading() {
        return isUploading;
    }

    public void setUploading(boolean uploading) {
        isUploading = uploading;
    }


    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }

    public String getPostImageUrlLocal() {
        return postImageUrlLocal;
    }

    public void setPostImageUrlLocal(String postImageUrlLocal) {
        this.postImageUrlLocal = postImageUrlLocal;
    }

    public String getPostVideoUrl() {
        return postVideoUrl;
    }

    public void setPostVideoUrl(String postVideoUrl) {
        this.postVideoUrl = postVideoUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Post post = (Post) o;

        return get_id().equals(post.get_id());

    }

    @Override
    public int hashCode() {
        return get_id().hashCode();
    }
}
