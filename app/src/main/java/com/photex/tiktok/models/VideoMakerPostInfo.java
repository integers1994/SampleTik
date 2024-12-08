package com.photex.tiktok.models;

import java.io.File;
import java.io.Serializable;

/**
 * Created by Muhammad Noman on 8/15/2017.
 */

public class VideoMakerPostInfo implements Serializable {
    String _id;
    String postImageUrl;
    String postVideoUrl;

    String userId;
    String userName;
    String fullName;
    String userDisplayPicture;
    String location;

    String tags;
    String caption;
    String height;
    String width;

    File videoFilePath;
    File thumbFilePath;
    String videoDuration;
    String localPath;

    String catId;

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

    public String getPostVideoUrl() {
        return postVideoUrl;
    }

    public void setPostVideoUrl(String postVideoUrl) {
        this.postVideoUrl = postVideoUrl;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public File getVideoFilePath() {
        return videoFilePath;
    }

    public void setVideoFilePath(File videoFilePath) {
        this.videoFilePath = videoFilePath;
    }

    public File getThumbFilePath() {
        return thumbFilePath;
    }

    public void setThumbFilePath(File thumbFilePath) {
        this.thumbFilePath = thumbFilePath;
    }

    public String getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(String videoDuration) {
        this.videoDuration = videoDuration;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }
}
