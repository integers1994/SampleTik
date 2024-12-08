package com.photex.tiktok.models.restmodels;

/**
 * Created by Ameer Hamza on 4/8/2017.
 */

public class AppVersion {
    String apkVersion;
    String priority;
    String message;

    public String getApkVersion() {
        return apkVersion;
    }

    public void setApkVersion(String apkVersion) {
        this.apkVersion = apkVersion;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
