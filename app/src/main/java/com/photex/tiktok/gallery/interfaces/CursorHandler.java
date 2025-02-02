package com.photex.tiktok.gallery.interfaces;

import android.database.Cursor;

import java.sql.SQLException;

/**
 * Created by dnld on 3/13/17.
 */

public interface CursorHandler<T> {
    T handle(Cursor cu) throws SQLException;
    static String[] getProjection() {
        return new String[0];
    }
}
