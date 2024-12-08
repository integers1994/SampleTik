
package com.photex.tiktok.models;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ResponseAudioInfo {

    @SerializedName("audios")
    private List<SingleAudioInfo> mAudios;
    @SerializedName("success")
    private Boolean mSuccess;

    public List<SingleAudioInfo> getAudios() {
        return mAudios;
    }

    public void setAudios(List<SingleAudioInfo> audios) {
        mAudios = audios;
    }

    public Boolean getSuccess() {
        return mSuccess;
    }

    public void setSuccess(Boolean success) {
        mSuccess = success;
    }


    public class SingleAudioInfo {

        @SerializedName("audioUrl")
        private String mAudioUrl;
        @SerializedName("catId")
        private String mCatId;
        @SerializedName("duration")
        private String mDuration;
        @SerializedName("folderName")
        private String mFolderName;
        @SerializedName("thumbnailUrl")
        private String mThumbnailUrl;
        @SerializedName("title")
        private String mTitle;
        @SerializedName("userId")
        private String mUserId;
        @SerializedName("userName")
        private String mUserName;
        @SerializedName("__v")
        private Long m_V;
        @SerializedName("_id")
        private String m_id;
        public boolean isShootbtnVisible; // for showing shoot button


        public boolean isShootbtnVisible() {
            return isShootbtnVisible;
        }

        public void setShootbtnVisible(boolean shootbtnVisible) {
            isShootbtnVisible = shootbtnVisible;
        }


        public String getAudioUrl() {
            return mAudioUrl;
        }

        public void setAudioUrl(String audioUrl) {
            mAudioUrl = audioUrl;
        }

        public String getCatId() {
            return mCatId;
        }

        public void setCatId(String catId) {
            mCatId = catId;
        }

        public String getDuration() {
            return mDuration;
        }

        public void setDuration(String duration) {
            mDuration = duration;
        }

        public String getFolderName() {
            return mFolderName;
        }

        public void setFolderName(String folderName) {
            mFolderName = folderName;
        }

        public String getThumbnailUrl() {
            return mThumbnailUrl;
        }

        public void setThumbnailUrl(String thumbnailUrl) {
            mThumbnailUrl = thumbnailUrl;
        }

        public String getTitle() {
            return mTitle;
        }

        public void setTitle(String title) {
            mTitle = title;
        }

        public String getUserId() {
            return mUserId;
        }

        public void setUserId(String userId) {
            mUserId = userId;
        }

        public String getUserName() {
            return mUserName;
        }

        public void setUserName(String userName) {
            mUserName = userName;
        }

        public Long get_V() {
            return m_V;
        }

        public void set_V(Long _V) {
            m_V = _V;
        }

        public String get_id() {
            return m_id;
        }

        public void set_id(String _id) {
            m_id = _id;
        }

    }


}
