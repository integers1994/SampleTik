package com.photex.tiktok.models.restmodels;

import java.io.Serializable;

/**
 * Created by Aurang Zeb on 8/3/2016.
 */
public class UpdateUserProfile implements Serializable {
    String userId;
    private String userName;
    private String fullName;
    private String bio;
    private String emailId;
    private String phoneNo;
    private String displayPicture;
    private String gender;

    private String work;

    private boolean isWorkPrivate=false;

    private String country;
    private boolean isCountryPrivate=false;

    private String dob;
    private boolean isDobPrivate=false;

    private String maritalStatus;
    private boolean isMaritalStatusPrivate=false;


    private boolean isPhoneNoPrivate=false;
    private boolean isGenderPrivate=false;

  /*  private String fullDisplayPicture;
    private String backCoverDisplayPicture;*/


   /* public String getFullDisplayPicture() {
        return fullDisplayPicture;
    }

    public void setFullDisplayPicture(String fullDisplayPicture) {
        this.fullDisplayPicture = fullDisplayPicture;
    }

    public String getBackCoverDisplayPicture() {
        return backCoverDisplayPicture;
    }

    public void setBackCoverDisplayPicture(String backCoverDisplayPicture) {
        this.backCoverDisplayPicture = backCoverDisplayPicture;
    }*/



    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public boolean isWorkPrivate() {
        return isWorkPrivate;
    }

    public void setWorkPrivate(boolean workPrivate) {
        isWorkPrivate = workPrivate;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public boolean isCountryPrivate() {
        return isCountryPrivate;
    }

    public void setCountryPrivate(boolean countryPrivate) {
        isCountryPrivate = countryPrivate;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public boolean isDobPrivate() {
        return isDobPrivate;
    }

    public void setDobPrivate(boolean dobPrivate) {
        isDobPrivate = dobPrivate;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public boolean isMaritalStatusPrivate() {
        return isMaritalStatusPrivate;
    }

    public void setMaritalStatusPrivate(boolean maritalStatusPrivate) {
        isMaritalStatusPrivate = maritalStatusPrivate;
    }

    public boolean isGenderPrivate() {
        return isGenderPrivate;
    }

    public void setGenderPrivate(boolean genderPrivate) {
        isGenderPrivate = genderPrivate;
    }

    public boolean isPhoneNoPrivate() {
        return isPhoneNoPrivate;
    }

    public void setPhoneNoPrivate(boolean phoneNoPrivate) {
        isPhoneNoPrivate = phoneNoPrivate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getDisplayPicture() {
        return displayPicture;
    }

    public void setDisplayPicture(String displayPicture) {
        this.displayPicture = displayPicture;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
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

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


}
