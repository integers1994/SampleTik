package com.photex.tiktok.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aurang Zeb on 8/3/2016.
 */
public class User implements Parcelable {
    private String _id;
    private String userName;
    private String fullName;
    private String bio;
    private String emailId;
    private String phoneNo;
    private String displayPicture;
    private String gender;
    private String followingCount;
    private String followersCount;
    private String totalPostsCount;
    private String date;
    private String backCover;
    private boolean isFollowed;
    private String __v;


    private List<Work> work;
    private List<Education> education;
    private List<Place> places;

    private boolean isWorkPrivate = false;

    private String country;
    private boolean isCountryPrivate = false;

    private String dob;
    private boolean isDobPrivate = false;

    private String maritalStatus;
    private boolean isMaritalStatusPrivate = true;


    private boolean isPhoneNoPrivate = true;
    private boolean isGenderPrivate = false;

    private String fullDisplayPicture;
    private String backCoverDisplayPicture;

    private String religiousViews;
    private String politicalViews;
    private String languages;

    private String web;
    private boolean isWebPrivate = false;

    private String professionalSkills;
    private boolean isProfessionalSkillsPrivate = false;
    private  boolean isEmailPrivate=true;

    public void setTotalPostsCount(String totalPostsCount) {
        this.totalPostsCount = totalPostsCount;
    }

    public boolean isEmailPrivate() {
        return isEmailPrivate;
    }

    public void setEmailPrivate(boolean emailPrivate) {
        isEmailPrivate = emailPrivate;
    }

    public List<Place> getPlaces() {
        return places;
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
    }

    public List<Education> getEducation() {
        return education;
    }

    public void setEducation(List<Education> education) {
        this.education = education;
    }


    public List<Work> getWork() {
        return work;
    }


    public void setWork(List<Work> work) {
        this.work = work;
    }

    public boolean isProfessionalSkillsPrivate() {
        return isProfessionalSkillsPrivate;
    }

    public void setProfessionalSkillsPrivate(boolean professionalSkillsPrivate) {
        isProfessionalSkillsPrivate = professionalSkillsPrivate;
    }

    public String getProfessionalSkills() {
        return professionalSkills;
    }

    public void setProfessionalSkills(String professionalSkills) {
        this.professionalSkills = professionalSkills;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public boolean isWebPrivate() {
        return isWebPrivate;
    }

    public void setWebPrivate(boolean webPrivate) {
        isWebPrivate = webPrivate;
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


    public String getBackCoverDisplayPicture() {
        return backCoverDisplayPicture;
    }

    public void setBackCoverDisplayPicture(String backCoverDisplayPicture) {
        this.backCoverDisplayPicture = backCoverDisplayPicture;
    }

    public String getFullDisplayPicture() {
        return fullDisplayPicture;
    }

    public void setFullDisplayPicture(String fullDisplayPicture) {
        this.fullDisplayPicture = fullDisplayPicture;
    }


    public boolean getIsProfessionalSkillsPrivate() {
        return isProfessionalSkillsPrivate;
    }

    public void setIsProfessionalSkillsPrivate(boolean isProfessionalSkillsPrivate) {
        this.isProfessionalSkillsPrivate = isProfessionalSkillsPrivate;
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

    public boolean isPhoneNoPrivate() {
        return isPhoneNoPrivate;
    }

    public void setPhoneNoPrivate(boolean phoneNoPrivate) {
        isPhoneNoPrivate = phoneNoPrivate;
    }

    public boolean isGenderPrivate() {
        return isGenderPrivate;
    }

    public void setGenderPrivate(boolean genderPrivate) {
        isGenderPrivate = genderPrivate;
    }


    public String getBackCover() {
        return backCover;
    }

    public void setBackCover(String backCover) {
        this.backCover = backCover;
    }

    public boolean isFollowed() {
        return isFollowed;
    }

    public void setFollowed(boolean followed) {
        isFollowed = followed;
    }


    public String[] getPosts() {
        return posts;
    }

    public void setPosts(String[] posts) {
        this.posts = posts;
    }

    String[] posts;

    public String get__v() {

        return __v;
    }

    public void set__v(String __v) {
        this.__v = __v;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }


    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public String getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(String followersCount) {
        this.followersCount = followersCount;
    }

    public String getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(String followingCount) {
        this.followingCount = followingCount;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }


    public String getTotalPostsCount() {
        return totalPostsCount;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this._id);
        dest.writeString(this.userName);
        dest.writeString(this.fullName);
        dest.writeString(this.bio);
        dest.writeString(this.emailId);
        dest.writeString(this.phoneNo);
        dest.writeString(this.displayPicture);
        dest.writeString(this.gender);
        dest.writeString(this.followingCount);
        dest.writeString(this.followersCount);
        dest.writeString(this.totalPostsCount);
        dest.writeString(this.date);
        dest.writeString(this.backCover);
        dest.writeByte(this.isFollowed ? (byte) 1 : (byte) 0);
        dest.writeString(this.__v);
        dest.writeList(this.work);
        dest.writeList(this.education);
        dest.writeList(this.places);
        dest.writeByte(this.isWorkPrivate ? (byte) 1 : (byte) 0);
        dest.writeString(this.country);
        dest.writeByte(this.isCountryPrivate ? (byte) 1 : (byte) 0);
        dest.writeString(this.dob);
        dest.writeByte(this.isDobPrivate ? (byte) 1 : (byte) 0);
        dest.writeString(this.maritalStatus);
        dest.writeByte(this.isMaritalStatusPrivate ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isPhoneNoPrivate ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isGenderPrivate ? (byte) 1 : (byte) 0);
        dest.writeString(this.fullDisplayPicture);
        dest.writeString(this.backCoverDisplayPicture);
        dest.writeString(this.religiousViews);
        dest.writeString(this.politicalViews);
        dest.writeString(this.languages);
        dest.writeString(this.web);
        dest.writeByte(this.isWebPrivate ? (byte) 1 : (byte) 0);
        dest.writeString(this.professionalSkills);
        dest.writeByte(this.isProfessionalSkillsPrivate ? (byte) 1 : (byte) 0);
        dest.writeStringArray(this.posts);
    }

    public User() {
    }

    protected User(Parcel in) {
        this._id = in.readString();
        this.userName = in.readString();
        this.fullName = in.readString();
        this.bio = in.readString();
        this.emailId = in.readString();
        this.phoneNo = in.readString();
        this.displayPicture = in.readString();
        this.gender = in.readString();
        this.followingCount = in.readString();
        this.followersCount = in.readString();
        this.totalPostsCount = in.readString();
        this.date = in.readString();
        this.backCover = in.readString();
        this.isFollowed = in.readByte() != 0;
        this.__v = in.readString();
        this.work = new ArrayList<Work>();
        in.readList(this.work, Work.class.getClassLoader());
        this.education = new ArrayList<Education>();
        in.readList(this.education, Education.class.getClassLoader());
        this.places = new ArrayList<Place>();
        in.readList(this.places, Place.class.getClassLoader());
        this.isWorkPrivate = in.readByte() != 0;
        this.country = in.readString();
        this.isCountryPrivate = in.readByte() != 0;
        this.dob = in.readString();
        this.isDobPrivate = in.readByte() != 0;
        this.maritalStatus = in.readString();
        this.isMaritalStatusPrivate = in.readByte() != 0;
        this.isPhoneNoPrivate = in.readByte() != 0;
        this.isGenderPrivate = in.readByte() != 0;
        this.fullDisplayPicture = in.readString();
        this.backCoverDisplayPicture = in.readString();
        this.religiousViews = in.readString();
        this.politicalViews = in.readString();
        this.languages = in.readString();
        this.web = in.readString();
        this.isWebPrivate = in.readByte() != 0;
        this.professionalSkills = in.readString();
        this.isProfessionalSkillsPrivate = in.readByte() != 0;
        this.posts = in.createStringArray();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
