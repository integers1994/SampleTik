package com.photex.tiktok.models;

import java.io.Serializable;

/**
 * Created by Aurang Zeb on 08-Dec-16.
 */

public class Education implements Serializable {
    String _id;
    private String country;
    private String city;
    private  String organization;
    private boolean isPhysical = true;
    private String from;
    private String to;
    private boolean isGraduated;
    private String title;

    private  String attendedFor;

    public String getAttendedFor() {
        return attendedFor;
    }

    public void setAttendedFor(String attendedFor) {
        this.attendedFor = attendedFor;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
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


}
