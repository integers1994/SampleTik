package com.photex.tiktok.models.restmodels;

/**
 * Created by Ameer Hamza on 11/15/2016.
 */

public class GetNotiCountRes {
    String _id;
    int total;
    String userId;

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

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
