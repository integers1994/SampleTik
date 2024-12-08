package com.photex.tiktok.gallery.model;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;


import com.bumptech.glide.signature.ObjectKey;
import com.photex.tiktok.gallery.interfaces.CursorHandler;
import com.photex.tiktok.utils.StringUtils;



import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * fix date problem plus
 */
/*@oldPath from where its copied
 * @newPath is basically trash path
 * */public class Media implements CursorHandler, Parcelable {

    @NonNull
    int _id;

    private String path = null;
    private long dateModified = -1;
    private String mimeType = "unknown";
    private int orientation = 0;
    private String uriString = null;
    private long size = -1;

    private boolean selected = false;

    private String longitude, latitude;
    private String description;
    private String tags;


    private long duration;
    private String humanReadAbleTime = "";

    /*Own purpose fields*/
    private String oldPath, newPath;
    private boolean isFavourite = false;


    List<String> tagsList;


    @NonNull
    public int get_id() {
        return _id;
    }

    public void set_id(@NonNull int _id) {
        this._id = _id;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public List<String> getTagsList() {
        return tagsList;
    }

    public void setTagsList(List<String> tagsList) {
        this.tagsList = tagsList;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    public void setDateModified(long dateModified) {
        this.dateModified = dateModified;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getUriString() {
        return uriString;
    }

    public void setUriString(String uriString) {
        this.uriString = uriString;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getOldPath() {
        return oldPath;
    }

    public void setOldPath(String oldPath) {
        this.oldPath = oldPath;
    }

    public String getNewPath() {
        return newPath;
    }

    public void setNewPath(String newPath) {
        this.newPath = newPath;
    }


    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public Media() {
    }

    public Media(String path, long dateModified) {
        this.path = path;
        this.dateModified = dateModified;
        this.mimeType = StringUtils.getMimeType(path);
    }

    public Media(File file) {
        this(file.getPath(), file.lastModified());
        this.size = file.length();
        this.mimeType = StringUtils.getMimeType(path);
    }

    public Media(String path) {
        this(path, -1);
    }

    public Media(Uri mediaUri) {
        this.uriString = mediaUri.toString();
        this.path = null;
        this.mimeType = StringUtils.getMimeType(uriString);
    }

    public Media(@NonNull Cursor cur) {
        this.path = cur.getString(0);
        this.dateModified = cur.getLong(1);
        this.mimeType = cur.getString(2);
        this.size = cur.getLong(3);
        this.orientation = cur.getInt(4);
        this.latitude = cur.getString(5/*cur.getColumnIndexOrThrow(
                MediaStore.Images.Media.LATITUDE)*/);
        this.longitude = cur.getString(6/*cur.getColumnIndexOrThrow(
                MediaStore.Images.Media.LONGITUDE)*/);
        this.description = cur.getString(7/*
                cur.getColumnIndexOrThrow(MediaStore.Images.Media.DESCRIPTION)*/);

        this.duration = cur.getLong(8/*cur.getColumnIndexOrThrow(MediaStore.Video.Media
                .DURATION)*/);

    }


    public String getHumanReadAbleTime() {
        return humanReadAbleTime;
    }

    public void setHumanReadAbleTime(String humanReadAbleTime) {
        this.humanReadAbleTime = humanReadAbleTime;
    }

    @Override
    public Media handle(Cursor cu) throws SQLException {
        return new Media(cu);
    }

    public static String[] getProjection() {
        return new String[]{
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.SIZE,
                /*todo chanage orientation functionality*/
                MediaStore.Images.Media.ORIENTATION,
                MediaStore.Images.Media.LATITUDE,
                MediaStore.Images.Media.LONGITUDE,
                MediaStore.Images.Media.DESCRIPTION

        };
    }

    public static String[] getProjectionVideo() {
        return new String[]{
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DATE_TAKEN,
                MediaStore.Video.Media.MIME_TYPE,
                MediaStore.Video.Media.SIZE,
                /*todo chanage orientation functionality*/
                MediaStore.Images.Media.ORIENTATION,
                MediaStore.Video.Media.LATITUDE,
                MediaStore.Video.Media.LONGITUDE,
                MediaStore.Video.Media.DESCRIPTION,

                MediaStore.Video.Media.DURATION
        };
    }

    public void setUri(String uriString) {
        this.uriString = uriString;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMimeType() {
        return mimeType;
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean setSelected(boolean selected) {
        if (this.selected == selected) return false;
        this.selected = selected;
        return true;
    }

    public boolean toggleSelected() {
        selected = !selected;
        return selected;
    }

    public boolean isGif() {
        return mimeType.endsWith("gif");
    }

    public boolean isImage() {
        return mimeType.startsWith("image");
    }

    public boolean isVideo() {
        return mimeType.startsWith("video");
    }

    public Uri getUri() {
        return uriString != null ? Uri.parse(uriString) :
                Uri.fromFile(new File(path));
    }

    public String getDisplayPath() {
        return path != null ?
                path : getUri().getEncodedPath();
    }

    public String getName() {
        return StringUtils.getPhotoNameByPath(path);
    }

    public long getSize() {
        return size;
    }

    public String getPath() {
        return path;
    }

    public Long getDateModified() {
        return dateModified;
    }


    public ObjectKey getSignature() {
        return new ObjectKey(getDateModified() + getPath() + getOrientation());
    }

    public int getOrientation() {
        return orientation;
    }

    //<editor-fold desc="Exif & More">
// TODO remove from here!
    @Deprecated
    public Bitmap getBitmap() {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
        bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
        return bitmap;
    }

//    @Deprecated
//    public GeoLocation getGeoLocation() {
//        return /*metadata != null ? metadata.getLocation():*/  null;
//    }

    public boolean setOrientation(final int orientation) {
        this.orientation = orientation;
        // TODO: 28/08/16  find a better way
        // TODO update also content provider
        new Thread(new Runnable() {
            public void run() {
                int exifOrientation = -1;
                try {
                    ExifInterface exif = new ExifInterface(path);
                    switch (orientation) {
                        case 90:
                            exifOrientation = ExifInterface.ORIENTATION_ROTATE_90;
                            break;
                        case 180:
                            exifOrientation = ExifInterface.ORIENTATION_ROTATE_180;
                            break;
                        case 270:
                            exifOrientation = ExifInterface.ORIENTATION_ROTATE_270;
                            break;
                        case 0:
                            exifOrientation = ExifInterface.ORIENTATION_NORMAL;
                            break;
                    }
                    if (exifOrientation != -1) {
                        exif.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(exifOrientation));
                        exif.saveAttributes();
                    }
                } catch (IOException ignored) {
                    // TODO: 20-Nov-17 log Exception
                }
            }
        }).start();
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Media)
            return getPath().equals(((Media) obj).getPath());

        return super.equals(obj);
    }

    @Deprecated
    private long getDateTaken() {
        /*// TODO: 16/08/16 improved
        Date dateOriginal = metadata.getDateOriginal();
        if (dateOriginal != null) return metadata.getDateOriginal().getTime();
        return -1;*/
        return 1;
    }

    @Deprecated
    public boolean fixDate() {
        long newDate = getDateTaken();
        if (newDate != -1) {
            File f = new File(path);
            if (f.setLastModified(newDate)) {
                dateModified = newDate;
                return true;
            }
        }
        return false;
    }

    //</editor-fold>

    public File getFile() {
        if (path != null) {
            File file = new File(path);
            if (file.exists()) return file;
        }
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.path);
        dest.writeLong(this.dateModified);
        dest.writeString(this.mimeType);
        dest.writeInt(this.orientation);
        dest.writeString(this.uriString);
        dest.writeLong(this.size);
        dest.writeByte(this.selected ? (byte) 1 : (byte) 0);
    }

    protected Media(Parcel in) {
        this.path = in.readString();
        this.dateModified = in.readLong();
        this.mimeType = in.readString();
        this.orientation = in.readInt();
        this.uriString = in.readString();
        this.size = in.readLong();
        this.selected = in.readByte() != 0;
    }

    public static final Creator<Media> CREATOR = new Creator<Media>() {
        @Override
        public Media createFromParcel(Parcel source) {
            return new Media(source);
        }

        @Override
        public Media[] newArray(int size) {
            return new Media[size];
        }
    };
}