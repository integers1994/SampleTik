package com.photex.tiktok.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Ameer Hamza on 8/4/2016.
 */
public class Comment implements Serializable {

    String _id;
    String userName;
    String userId;
    String comment;
    String userDisplayPicture;
    String postId;
    String date;
    String fullName;
    String postBy;
    List<CommentReply> commentReply;
    int numOfCommentReplies;

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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getPostBy() {
        return postBy;
    }

    public void setPostBy(String postBy) {
        this.postBy = postBy;
    }

    public List<CommentReply> getCommentReply() {
        return commentReply;
    }

    public void setCommentReply(List<CommentReply> commentReply) {
        this.commentReply = commentReply;
    }

    public int getNumOfCommentReplies() {
        return numOfCommentReplies;
    }

    public void setNumOfCommentReplies(int numOfCommentReplies) {
        this.numOfCommentReplies = numOfCommentReplies;
    }
}
