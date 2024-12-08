package com.photex.tiktok.models.restmodels;

/**
 * Created by Aurang Zeb on 22-Dec-16.
 */

public class UserWorkEdit {
    String workId;
    String userId;
    private String organization;
    private String position;
    private String city;
    private boolean isPhysical = false;
    private String description;
    private String from;
    private String to;
    private boolean isCurrentlyWorking = false;
    private String country;

    public String getWorkId() {
        return workId;
    }

    public void setWorkId(String workId) {
        this.workId = workId;
    }
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }


    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
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

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public boolean isCurrentlyWorking() {
        return isCurrentlyWorking;
    }

    public void setCurrentlyWorking(boolean currentlyWorking) {
        isCurrentlyWorking = currentlyWorking;
    }
}
