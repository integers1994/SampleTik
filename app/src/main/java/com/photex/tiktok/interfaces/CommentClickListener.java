package com.photex.tiktok.interfaces;

import android.view.View;

public interface CommentClickListener {
    void onClickUserProfile( int position);

    void onClickUserName( int position);

    void onClickMore(View v, int position);

    void onClickReply(int position);
}
