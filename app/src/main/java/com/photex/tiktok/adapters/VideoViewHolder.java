/*
 * Copyright (c) 2018 Nam Nguyen, nam@ene.im
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.photex.tiktok.adapters;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.photex.tiktok.BaseApplication;
import com.photex.tiktok.R;
import com.photex.tiktok.interfaces.VideoPlayerListner;

import de.hdodenhof.circleimageview.CircleImageView;
import im.ene.toro.ToroPlayer;
import im.ene.toro.ToroUtil;
import im.ene.toro.exoplayer.PlayerViewHelper;
import im.ene.toro.exoplayer.ui.PlayerView;
import im.ene.toro.media.PlaybackInfo;
import im.ene.toro.widget.Container;

/**
 * @author eneim (2018/03/13).
 */

@SuppressWarnings("WeakerAccess") //
public class VideoViewHolder extends RecyclerView.ViewHolder implements
        ToroPlayer,
        ToroPlayer.EventListener {

    protected PlayerView playerView;
    public PlayerViewHelper helper;
    public Uri videoUri;
    public TextView tvUserName, tvCaption, tvTotalComments, tvTotalShares, tv_total_like;
    public ImageView commentBtn, shareBtn, img_like;
    public CircleImageView ivUserProfilePic;
    VideoPlayerListner videoPlayerListner;

    VideoViewHolder(ViewGroup parent, LayoutInflater inflater, int layoutRes) {
        super(inflater.inflate(layoutRes, parent, false));
        initializeView();

    }

    private void initializeView() {
        playerView = itemView.findViewById(R.id.playerView);
        playerView.setUseController(false);

        tv_total_like = itemView.findViewById(R.id.tv_total_like);
        img_like = itemView.findViewById(R.id.img_like);
        ivUserProfilePic = itemView.findViewById(R.id.iv_user_profile_pic);
        tvUserName = itemView.findViewById(R.id.tv_user_name);
        tvCaption = itemView.findViewById(R.id.tv_caption);
        tvTotalComments = itemView.findViewById(R.id.tv_total_comment);
        tvTotalShares = itemView.findViewById(R.id.tv_total_share);
        commentBtn = itemView.findViewById(R.id.comment_btn);
        shareBtn = itemView.findViewById(R.id.share_btn);

    }

    @NonNull
    @Override
    public View getPlayerView() {
        return playerView;
    }

    @NonNull
    @Override
    public PlaybackInfo getCurrentPlaybackInfo() {
        return helper != null ? helper.getLatestPlaybackInfo() : PlaybackInfo.SCRAP;
    }

    @Override
    public void initialize(@NonNull Container container, @NonNull PlaybackInfo playbackInfo) {
        if (videoUri == null) throw new IllegalStateException("Video is null.");
        if (helper == null) {
//            helper = new PlayerViewHelper(this, videoUri);
            helper = new PlayerViewHelper(this, videoUri, null, BaseApplication.exoCreator);
        }
        helper.initialize(container, playbackInfo);
        helper.addPlayerEventListener(this);
    }

    @Override
    public void play() {
        if (helper != null) helper.play();
    }

    @Override
    public void pause() {
        if (helper != null) helper.pause();
    }

    @Override
    public boolean isPlaying() {
        return helper != null && helper.isPlaying();
    }

    @Override
    public void release() {
        if (helper != null) {
            helper.release();
            helper = null;
        }
    }

    @Override
    public boolean wantsToPlay() {
        return ToroUtil.visibleAreaOffset(this, itemView.getParent()) >= 0.65;
    }

    @Override
    public int getPlayerOrder() {
        return getAdapterPosition();
    }

/*
    @Override
    public void onFirstFrameRendered() {

    }
*/

    /* @Override
        public void onFirstFrameRendered() {

        }
    */
    @Override
    public void onBuffering() {

        if (videoPlayerListner != null) {
            videoPlayerListner.onVideoBuffering();
        } else {


        }

    }

    @Override
    public void onPlaying() {
        videoPlayerListner.onVideoPlaying();
    }

    @Override
    public void onPaused() {
        videoPlayerListner.onVideoPause();

    }

    @Override
    public void onCompleted() {
        videoPlayerListner.onVideoComplete();
    }

    public VideoViewHolder setVideoPlayerListner(VideoPlayerListner videoPlayerListner) {
        this.videoPlayerListner = videoPlayerListner;
        return this;
    }
}
