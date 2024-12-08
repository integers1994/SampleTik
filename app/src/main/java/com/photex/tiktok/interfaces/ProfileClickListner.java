package com.photex.tiktok.interfaces;

import android.view.View;
import android.widget.TextView;

import com.photex.tiktok.models.User;

import java.util.ArrayList;

public interface ProfileClickListner {
    void onVideoClick(int position, ArrayList<Object> allFeeds);

    void onEditProfileBtn();

    void onFollowBtn(TextView followBtn);

    void onUnFollowBtn(TextView unFollowBtn);

    void onUserFollowingBtn();

    void onUserFansBtn();
}
