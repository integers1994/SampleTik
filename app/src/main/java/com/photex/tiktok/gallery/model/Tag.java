package com.photex.tiktok.gallery.model;


import android.content.Context;


import com.photex.tiktok.R;

import java.io.Serializable;
import java.util.ArrayList;

import androidx.annotation.NonNull;

/**
 * Created by Aurang Zeb on 09-Dec-17.
 */
public class Tag implements Serializable {

    @NonNull
    private int tagId;

    private String tagName;
    private int tagIcon;

    private boolean selected = false;

    public Tag(String tagName, int tagIcon) {
        this.tagName = tagName;
        this.tagIcon = tagIcon;
    }

    public Tag(String tagName) {
        this.tagName = tagName;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public int getTagIcon() {
        return tagIcon;
    }

    public void setTagIcon(int tagIcon) {
        this.tagIcon = tagIcon;
    }

/*
    public static ArrayList<Tag> defaultTags(Context context) {
        ArrayList<Tag> tags = new ArrayList<>();
        tags.add(new Tag(context.getString(R.string.food)));
        tags.add(new Tag(context.getString(R.string.information)));
        tags.add(new Tag(context.getString(R.string.pet)));
        tags.add(new Tag(context.getString(R.string.travel)));

        return tags;
    }
*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tag tag = (Tag) o;

        return tagName.equals(tag.tagName);
    }

    @Override
    public int hashCode() {
        return tagName.hashCode();
    }
}
