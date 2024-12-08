package com.photex.tiktok.models;

/**
 * Created by Aurang Zeb on 06-Sep-16.
 */
public class ImageInfo {
    int height;
    int width;
    long size;

    public ImageInfo(int height, int width, long size) {

        this.height = height;
        this.width = width;
        this.size = size;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }


    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }


}
