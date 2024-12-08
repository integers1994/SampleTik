package com.photex.tiktok.models.restmodels;

import androidx.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by Aurang Zeb on 06-Dec-16.
 */

public class UserBasicInfo implements Serializable {
    @NonNull
    private String userId;
    @NonNull
    private String fullName;
    @NonNull
    private String gender;

    private String dob;
    private String bio;

    private String religiousViews;
    private String politicalViews;
    private String languages;
    private boolean isGenderPrivate = false;
    private boolean isDobPrivate = false;

    public boolean isGenderPrivate() {
        return isGenderPrivate;
    }

    public void setGenderPrivate(boolean genderPrivate) {
        isGenderPrivate = genderPrivate;
    }

    public String getLanguages() {
        return languages;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }

    public String getPoliticalViews() {
        return politicalViews;
    }

    public void setPoliticalViews(String politicalViews) {
        this.politicalViews = politicalViews;
    }

    public String getReligiousViews() {
        return religiousViews;
    }

    public void setReligiousViews(String religiousViews) {
        this.religiousViews = religiousViews;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public boolean isDobPrivate() {
        return isDobPrivate;
    }

    public void setDobPrivate(boolean dobPrivate) {
        isDobPrivate = dobPrivate;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
