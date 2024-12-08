package com.photex.tiktok.models.restmodels;

/**
 * Created by Aurang Zeb on 24-Nov-16.
 */

public class UpdateBackCover {
    String userId;
    private String backCoverDisplayPicture;

    public String getBackCoverDisplayPicture() {
        return backCoverDisplayPicture;
    }

    public void setBackCoverDisplayPicture(String backCoverDisplayPicture) {
        this.backCoverDisplayPicture = backCoverDisplayPicture;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


}
