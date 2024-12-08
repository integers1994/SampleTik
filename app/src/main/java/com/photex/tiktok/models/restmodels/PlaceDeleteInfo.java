package com.photex.tiktok.models.restmodels;

/**
 * Created by Aurang Zeb on 02-Jan-17.
 */

public class PlaceDeleteInfo {
    String userId;
    private String placeId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }
}
