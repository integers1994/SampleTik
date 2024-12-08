package com.photex.tiktok.gallery.interfaces;


import com.photex.tiktok.gallery.model.Media;

/**
 * Created by dnld on 6/28/17.
 */

public interface IAlbum {
    String getName();
    String getPath();
    int getCount();
    Media getCover();
}
