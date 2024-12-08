package com.photex.tiktok.models.restmodels;

/**
 * Created by Aurang Zeb on 8/8/2016.
 */
public class UpdateDisplayPicture {
    String userId;
    String displayPicture;
    private String fullDisplayPicture;

    public String getFullDisplayPicture() {
        return fullDisplayPicture;
    }

    public void setFullDisplayPicture(String fullDisplayPicture) {
        this.fullDisplayPicture = fullDisplayPicture;
    }


    public String getDisplayPicture() {
        return displayPicture;
    }

    public void setDisplayPicture(String displayPicture) {
        this.displayPicture = displayPicture;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }



}
