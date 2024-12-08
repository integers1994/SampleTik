package com.photex.tiktok.models;

/**
 * Created by Ameer Hamza on 8/5/2016.
 */
public class UserInfo {

    /*"_id": "57a173bc70aae46139333f23",
            "userName": "",
            "fullName": "usama dragon",
            "bio": "i am differnt from others",
            "emailId": "silence0sea@gmail.com",
            "phoneNo": "03326567008",
            "displayPicture": null,
            "gender": "Male",
            "__v": 0,
            "posts": [],
            "followingCount": 0,
            "followersCount": 0,
            "totalPostsCount": 6,
            "date": "2016-08-03T04:31:56.719Z"*/

    String _id;
    String userName;
    String fullName;
    String bio;
    String emailId;
    String phoneNo;
    String displayPicture;
    String gender;
    Boolean success;
    String folderName;
    String displayPictureLastModified;
    String token;

    public String getDisplayPictureLastModified() {
        return displayPictureLastModified;
    }

    public void setDisplayPictureLastModified(String displayPictureLastModified) {
        this.displayPictureLastModified = displayPictureLastModified;
    }



    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }



    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }



    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
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

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getDisplayPicture() {
        return displayPicture;
    }

    public void setDisplayPicture(String displayPicture) {
        this.displayPicture = displayPicture;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
