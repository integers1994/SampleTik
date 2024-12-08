package com.photex.tiktok.gallery.filter;


import com.photex.tiktok.gallery.model.Media;

/**
 * Created by dnld on 4/10/17.
 */

public interface IMediaFilter {
    boolean accept(Media media);
}
