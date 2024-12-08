package com.photex.tiktok.models.restmodels;

/**
 * Created by Aurang Zeb on 8/11/2016.
 */
public class GetLikes {
    String postId;
    String lastId;
    String myId;

    public String getMyId() {
        return myId;
    }

    public void setMyId(String myId) {
        this.myId = myId;
    }


    public String getPostId() {

        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getLastId() {
        return lastId;
    }

    public void setLastId(String lastId) {
        this.lastId = lastId;
    }


}
