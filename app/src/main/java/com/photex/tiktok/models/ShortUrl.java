package com.photex.tiktok.models;

/**
 * Created by Aurang Zeb on 25-Aug-17.
 */

public class ShortUrl {

    private String kind;
    private String id;
    private String longUrl;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }
}
