package com.photex.tiktok.interfaces;

public interface PostClickListner {
    void onProfileBtn(String userId);

    void onCommentBtn(int itemPosition, String postId, String postById, int totalComments);

    void onShareBtn(int itemPosition, String videoLink, int totalShares);
}
