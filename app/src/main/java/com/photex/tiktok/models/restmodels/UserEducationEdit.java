package com.photex.tiktok.models.restmodels;

/**
 * Created by Aurang Zeb on 30-Dec-16.
 */

public class UserEducationEdit {
    String educationId;
    String userId;
    private String organization;
    private String country;
    private String title;
    private String city;
    private boolean isPhysical = true;
    private String from;
    private String to;
    private boolean isGraduated;
    private String attendedFor;

    public String getEducationId() {
        return educationId;
    }

    public void setEducationId(String educationId) {
        this.educationId = educationId;
    }

    public String getAttendedFor() {
        return attendedFor;
    }

    public void setAttendedFor(String attendedFor) {
        this.attendedFor = attendedFor;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public boolean isPhysical() {
        return isPhysical;
    }

    public void setPhysical(boolean physical) {
        isPhysical = physical;
    }

    public boolean isGraduated() {
        return isGraduated;
    }

    public void setGraduated(boolean graduated) {
        isGraduated = graduated;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }


    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }
}
