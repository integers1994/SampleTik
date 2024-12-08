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

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.photex.tiktok.R;
import com.photex.tiktok.interfaces.PostClickListner;
import com.photex.tiktok.interfaces.VideoPlayerListner;
import com.photex.tiktok.models.Post;
import com.photex.tiktok.setting.SettingManager;
import com.photex.tiktok.utils.Constants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import im.ene.toro.CacheManager;
import im.ene.toro.PlayerSelector;
import im.ene.toro.ToroPlayer;
import im.ene.toro.ToroUtil;
import im.ene.toro.exoplayer.ui.PlayerView;
import im.ene.toro.exoplayer.ui.ToroControlView;
import im.ene.toro.widget.Container;

/**
 * This Adapter introduces 2 practices for Toro:
 * <p>
 * 1. Acts as a {@link CacheManager}. The implementation is trivial.
 * 2. Acts as a {@link PlayerSelector} and minds the 'UI interaction'.
 * The background is: the {@link VideoViewHolder} has a {@link PlayerView} by default, in which
 * a {@link ToroControlView} is available. User can interact to that widget to play/pause/change
 * volume for a playback. If a playback is paused by User, we should not start it automatically.
 * <p>
 * To be able to do this, we keep track of the player position that User has manually paused,
 * and use the ability of {@link PlayerSelector} to disallow it to start automatically, until
 * User manually do it again. Right now it caches only one position, but the implementation for
 * many should be trivial.
 *
 * @author eneim (2018/03/13).
 */

public class FeedsAdapter extends RecyclerView.Adapter<VideoViewHolder> implements
        PlayerSelector,
        CacheManager,
        VideoPlayerListner {

    private static final int TYPE_VIDEO = 1;
    private ArrayList<Object> allFeeds;
    Context context;
    private LayoutInflater inflater;

    private VideoPlayerListner videoPlayerListner;
    private PostClickListner postClickListner;

    public FeedsAdapter(Context context, PlayerSelector origin) {
        this.context = context;
        this.origin = ToroUtil.checkNotNull(origin);
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (inflater == null || inflater.getContext() != parent.getContext()) {
            inflater = LayoutInflater.from(parent.getContext());
        }

        VideoViewHolder videoViewHolder = new VideoViewHolder(parent, inflater,
                R.layout.item_feeds_video)
                .setVideoPlayerListner(this);
        return videoViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder,
                                 int pos) {
        int position = holder.getAdapterPosition();

        Post postInfo = (Post) allFeeds.get(position);

        holder.tvUserName.setText("@" + postInfo.getUserName());
        holder.tvCaption.setText(postInfo.getCaption());

        int comments = postInfo.getComments();

        if (comments > 0) {
            holder.tvTotalComments.setVisibility(View.VISIBLE);
            holder.tvTotalComments.setText(String.valueOf(comments));

        } else {
            holder.tvTotalComments.setVisibility(View.VISIBLE);
            holder.tvTotalComments.setText("0");
        }



        int totalLikes = postInfo.getLikes();
        if (totalLikes > 0) {
            holder.tv_total_like.setVisibility(View.VISIBLE);
            holder.tv_total_like.setText(String.valueOf(totalLikes));
        } else {
            holder.tv_total_like.setVisibility(View.VISIBLE);
            holder.tv_total_like.setText("0");
        }

        if (postInfo.isLiked()) {
            holder.img_like.setImageResource(R.mipmap.ic_heart_red);
        } else {
            holder.img_like.setImageResource(R.mipmap.ic_heart_white);
        }

        int shares = postInfo.getShares();
        if (shares > 0) {
            holder.tvTotalShares.setVisibility(View.VISIBLE);
            holder.tvTotalShares.setText(String.valueOf(shares));
        } else {
            holder.tvTotalShares.setVisibility(View.INVISIBLE);
        }

        Glide.with(context)
                .load(Constants.BASE_URL + Constants.SERVER_DIRECTORY + postInfo.getUserDisplayPicture())
                .apply(new RequestOptions()
                        .signature(new ObjectKey(SettingManager.getProfilePicTime(context)))
                        .placeholder(R.drawable.user_image_place_holder).dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                .into(holder.ivUserProfilePic);

        holder.itemView.setOnClickListener(view -> {
            if (holder.helper != null) {
                if (holder.helper.isPlaying()) {
                    holder.helper.pause();
                } else {
                    holder.helper.play();
                }
            }
        });

        holder.ivUserProfilePic.setOnClickListener(v -> postClickListner.onProfileBtn(postInfo.getUserId()));

        holder.shareBtn.setOnClickListener(v ->
                postClickListner.onShareBtn(position,
                        Constants.STREAMING_URL + postInfo.getPostVideoUrl(), postInfo.getShares()));

        holder.commentBtn.setOnClickListener(v ->
                postClickListner.onCommentBtn(position, postInfo.get_id(), postInfo.getUserId(), postInfo.getComments())
        );

        String videoUrl = ((Post) allFeeds.get(position)).getPostVideoUrl();
//        holder.videoUri = Uri.parse(Constants.STREAMING_URL + videoUrl);
       // holder.videoUri = Uri.parse("http://115.186.156.172:8000/hls/360p.m3u8"); // simple http

//        holder.videoUri = Uri.parse("https://115.186.156.172/hls/360p.m3u8"); //https

        holder.videoUri = Uri.parse("Http://42.200.146.225:8000/hls/360p.m3u8"); //https

//        holder.videoUri = Uri.parse("https://mnmedias.api.telequebec.tv/m3u8/29880.m3u8");

//        holder.videoUri = Uri.parse(Constants.TEST_SREAMING_URL + videoUrl);
//        holder.videoUri = Uri.parse(Constants.TEST_SREAMING_URL + "2019080715194index.m3u8");
          /*holder.videoUri = Uri.parse(Constants.TEST_SREAMING_URL + "20190807151943index.m3u8");*/



        //RTMP
//        String videoUrl = "/pkpipe/A29Ri2Q-U/1.mp4";
//        holder.videoUri = Uri.parse(Constants.STREAMING_URL + videoUrl + Constants.STREAM_PARAMS + videoUrl);

        //HLS
//        holder.videoUri = Uri.parse("http://42.200.129.59:8080/mp4/index" + position + ".m3u8");
//        holder.videoUri=Uri.parse("http://115.186.156.165:8080/mp4s/index10.m3u8");

        //HTTP
//        holder.videoUri = Uri.parse("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
//        Random rand = new Random();
//        int n = rand.nextInt(20) + 1;
//        holder.videoUri = Uri.parse(Constants.BASE_URL + "test/video" + n + ".mp4");

        //Local
//        holder.videoUri = Uri.parse("file:///android_asset/one.mp4");
    }

    @Override
    public int getItemCount() {
        return (allFeeds != null) ? allFeeds.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_VIDEO;
    }

    public void setData(ArrayList<Object> allFeeds) {
        this.allFeeds = allFeeds;
    }

    public void clearData() {
        allFeeds = new ArrayList<>();
    }

    public String getPostId(int index) {
        if (allFeeds.get(index) instanceof Post) {
            Post lastPost = (Post) allFeeds.get(index);
            return lastPost.get_id();
        } else if (allFeeds.get(index - 1) instanceof Post) {
            Post lastPost = (Post) allFeeds.get(index - 1);
            return lastPost.get_id();
        } /*else if (allFeeds.get(index - 1) instanceof NativeExpressAdView) {
            if (allFeeds.size() > index + 1 && allFeeds.get(index + 1) instanceof Post) {
                Post lastPost = (Post) allFeeds.get(index + 1);
                return lastPost.get_id();
            } else {
                Post lastPost = (Post) allFeeds.get(index - 2);
                return lastPost.get_id();
            }
        } */ else {
            Post lastPost = (Post) allFeeds.get(index - 1);
            return lastPost.get_id();
        }
    }

    /// PlayerSelector implementation

    @SuppressWarnings("WeakerAccess") //
    final PlayerSelector origin;
    // Keep a cache of the Playback order that is manually paused by User.
    // So that if User scroll to it again, it will not start play.
    // Value will be updated by the ItemViewHolder.
    final AtomicInteger lastUserPause = new AtomicInteger(-1);

    @NonNull
    @Override
    public Collection<ToroPlayer> select(@NonNull Container container,
                                         @NonNull List<ToroPlayer> items) {
        Collection<ToroPlayer> originalResult = origin.select(container, items);
        ArrayList<ToroPlayer> result = new ArrayList<>(originalResult);
        if (lastUserPause.get() >= 0) {
            for (Iterator<ToroPlayer> it = result.iterator(); it.hasNext(); ) {
                if (it.next().getPlayerOrder() == lastUserPause.get()) {
                    it.remove();
                    break;
                }
            }
        }

        return result;
    }

    @NonNull
    @Override
    public PlayerSelector reverse() {
        return origin.reverse();
    }

    /// CacheManager implementation

    @Nullable
    @Override
    public Object getKeyForOrder(int order) {
        return order;
    }

    @Nullable
    @Override
    public Integer getOrderForKey(@NonNull Object key) {
        return key instanceof Integer ? (Integer) key : null;
    }

    public FeedsAdapter setPostClickListner(PostClickListner postClickListner) {
        this.postClickListner = postClickListner;
        return this;
    }

    public FeedsAdapter setVideoPlayerListner(VideoPlayerListner videoPlayerListner) {
        this.videoPlayerListner = videoPlayerListner;
        return this;
    }

    @Override
    public void onVideoBuffering() {
        videoPlayerListner.onVideoBuffering();
    }

    @Override
    public void onVideoPlaying() {
        videoPlayerListner.onVideoPlaying();
    }

    @Override
    public void onVideoPause() {
        videoPlayerListner.onVideoPause();
    }

    @Override
    public void onVideoComplete() {
        videoPlayerListner.onVideoComplete();
    }

    public void updateCommentsInfo(int itemPosition, int totalComments) {
        Post post = (Post) allFeeds.get(itemPosition);
        post.setComments(totalComments);
        allFeeds.set(itemPosition, post);
        notifyItemChanged(itemPosition);
    }

    public void updateShareInfo(int itemPosition, int totalShares) {
        Post post = (Post) allFeeds.get(itemPosition);
        post.setShares(totalShares);
        allFeeds.set(itemPosition, post);
        notifyItemChanged(itemPosition);
    }
}
