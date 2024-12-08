package com.photex.tiktok.models.restmodels;

/**
 * Created by Ameer Hamza on 7/28/2016.
 */
public class UserSignInResponse {

   /* user_id: null,
    success: "false"*/
    String user_id;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    Boolean success;
}
