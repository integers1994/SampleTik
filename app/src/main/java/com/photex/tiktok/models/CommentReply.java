package com.photex.tiktok.models;

import java.io.Serializable;

/**
 * Created by Ameer Hamza on 1/11/2017.
 */

public class CommentReply implements Serializable {

    String userName;
    String fullName;
    String  userId;
    String comment;
    String userDisplayPicture;
    String postId;
    String parentCommentId;
    String parentCommentUserId;
    String postBy;
    String _id;
    String date;

   /* "_id":"58f89dccde1f61c9620a102f",
            "userName":"ameer.hamza",
            "fullName":"Hamza",
            "userId":"57c053e229912980270ed480",
            "comment":"x i guh",
            "userDisplayPicture":"B10lJP6q/hamza20000.jpeg",
            "postId":"58c676e943132c2f2efbe84f",
            "parentCommentId":"58d8e4ef39e88bda75fd1ce5",
            "__v":0,
            "date":"2017-04-20T11:38:52.978Z"*/

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getParentCommentUserId() {
        return parentCommentUserId;
    }

    public void setParentCommentUserId(String parentCommentUserId) {
        this.parentCommentUserId = parentCommentUserId;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserDisplayPicture() {
        return userDisplayPicture;
    }

    public void setUserDisplayPicture(String userDisplayPicture) {
        this.userDisplayPicture = userDisplayPicture;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(String parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public String getPostBy() {
        return postBy;
    }

    public void setPostBy(String postBy) {
        this.postBy = postBy;
    }

}
