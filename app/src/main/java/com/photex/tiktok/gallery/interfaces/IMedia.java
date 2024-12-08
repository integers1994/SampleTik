package com.photex.tiktok.gallery.interfaces;


import com.photex.tiktok.gallery.model.Media;

import java.util.ArrayList;

/**
 * Created by Aurang Zeb on 06-Dec-17.
 */

public interface IMedia {
    void onSuccess(ArrayList<Media> media);
    void onLoadedFailed();


}
