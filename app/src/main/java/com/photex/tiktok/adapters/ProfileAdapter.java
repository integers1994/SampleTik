package com.photex.tiktok.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.photex.tiktok.R;
import com.photex.tiktok.interfaces.ProfileClickListner;
import com.photex.tiktok.models.Post;
import com.photex.tiktok.models.User;
import com.photex.tiktok.setting.SettingManager;
import com.photex.tiktok.utils.Constants;
import com.photex.tiktok.utils.Util;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int HEADER = 0;
    private static final int ITEM = 1;

    private Context context;
    private int cellSize;

    ArrayList<Object> allFeeds;
    private User userInfo;
    private String myId;

    ProfileClickListner profileClickListner;

    public ProfileAdapter(Context context) {
        this.context = context;
        cellSize = Util.getScreenWidth(context) / 3;
        allFeeds = new ArrayList<>();
        myId = SettingManager.getUserId(context);
    }

    public String getPostId(int index) {
        if (allFeeds.get(index) instanceof Post) {
            Post lastPost = (Post) allFeeds.get(index);
            return lastPost.get_id();
        } else if (allFeeds.get(index - 1) instanceof Post) {
            Post lastPost = (Post) allFeeds.get(index - 1);
            return lastPost.get_id();
        } else {
            Post lastPost = (Post) allFeeds.get(index - 1);
            return lastPost.get_id();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER) {
            View v = LayoutInflater.from(context).inflate(R.layout.item_profile_header,
                    parent, false);

            return new HeaderViewHolder(v);

        } else {
            View v = LayoutInflater.from(context).inflate(R.layout.item_profile_videos,
                    parent, false);

       /*     GridLayoutManager.LayoutParams layoutParams =
                    (GridLayoutManager.LayoutParams) v.getLayoutParams();
            layoutParams.height = cellSize;
            layoutParams.width = cellSize;
            v.setLayoutParams(layoutParams);
*/
            return new ItemViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof HeaderViewHolder) {

            HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;

            if (userInfo != null) {
                headerViewHolder.tvUserName.setText("@" + userInfo.getUserName());
                headerViewHolder.tvTotalFollowing.setText(userInfo.getFollowingCount());
                headerViewHolder.tvTotalFollowers.setText(userInfo.getFollowersCount());
                headerViewHolder.tvTotalVideos.setText(userInfo.getTotalPostsCount());
                headerViewHolder.tvUserBio.setText(userInfo.getBio());

                Glide.with(context)
                        .load(Constants.BASE_URL + Constants.SERVER_DIRECTORY +
                                userInfo.getDisplayPicture())
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.user_image_place_holder).dontAnimate()
                                .signature(new ObjectKey(SettingManager.getProfilePicTime(context)))
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        )
                        .into(headerViewHolder.ivUserProfilePic);

                updateHeaderBtn(headerViewHolder);
            }

        } else if (viewHolder instanceof ItemViewHolder) {

            ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
            int currentPosition = itemViewHolder.getAdapterPosition();
            Post postInfo = (Post) allFeeds.get(currentPosition);

            Glide.with(context)
                    .load(Constants.STREAMING_URL + postInfo.getPostImageUrl())
                    .apply(new RequestOptions()
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                    .into(itemViewHolder.ivProfileVideoThumb);

            itemViewHolder.itemView.setOnClickListener(v -> {
                        ArrayList<Object> allVideos = new ArrayList<>();
                        allVideos.addAll(allFeeds);
                        allVideos.remove(0);

                        profileClickListner.onVideoClick(currentPosition - 1, allVideos);
                    }
            );
        }
    }

    private void updateHeaderBtn(HeaderViewHolder headerViewHolder) {
        if (userInfo.get_id().equals(myId)) {
            headerViewHolder.editProfileBtn.setVisibility(View.VISIBLE);

            headerViewHolder.unFollowBtn.setVisibility(View.GONE);
            headerViewHolder.followBtn.setVisibility(View.GONE);

        } else {
            headerViewHolder.editProfileBtn.setVisibility(View.GONE);

            if (userInfo.isFollowed()) {
                headerViewHolder.unFollowBtn.setVisibility(View.VISIBLE);
                headerViewHolder.followBtn.setVisibility(View.GONE);
            } else {
                headerViewHolder.followBtn.setVisibility(View.VISIBLE);
                headerViewHolder.unFollowBtn.setVisibility(View.GONE);
            }
        }

        if (userInfo.getBio() != null && userInfo.getBio().isEmpty()) {
            headerViewHolder.tvUserBio.setText("No User Detail Found");
        }


        headerViewHolder.editProfileBtn.setOnClickListener(v ->
                profileClickListner.onEditProfileBtn());

        headerViewHolder.followBtn.setOnClickListener(v ->
                profileClickListner.onFollowBtn(headerViewHolder.followBtn));

        headerViewHolder.unFollowBtn.setOnClickListener(v ->
                profileClickListner.onUnFollowBtn(headerViewHolder.unFollowBtn));

        headerViewHolder.userFollowingBtn.setOnClickListener(v ->
                profileClickListner.onUserFollowingBtn());

        headerViewHolder.userFansBtn.setOnClickListener(v ->
                profileClickListner.onUserFansBtn());

    }

    @Override
    public int getItemCount() {
        return (allFeeds != null) ? allFeeds.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == HEADER) ? HEADER : ITEM;
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        CircleImageView ivUserProfilePic;
        TextView tvUserName, tvTotalFollowing, tvTotalFollowers, tvTotalVideos, tvUserBio, editProfileBtn, followBtn, unFollowBtn;
        LinearLayout userFollowingBtn, userFansBtn;

        public HeaderViewHolder(View view) {
            super(view);
            initView(view);
        }

        private void initView(View view) {
            ivUserProfilePic = view.findViewById(R.id.iv_user_profile_pic);
            tvUserName = view.findViewById(R.id.tv_user_name);
            tvUserBio = view.findViewById(R.id.tv_user_bio);
            tvTotalFollowing = view.findViewById(R.id.tv_total_following);
            tvTotalFollowers = view.findViewById(R.id.tv_total_followers);
            tvTotalVideos = view.findViewById(R.id.tv_total_videos);

            editProfileBtn = view.findViewById(R.id.edit_profile_btn);
            followBtn = view.findViewById(R.id.follow_btn);
            unFollowBtn = view.findViewById(R.id.un_follow_btn);
            userFollowingBtn = view.findViewById(R.id.user_following_btn);
            userFansBtn = view.findViewById(R.id.user_fans_btn);
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProfileVideoThumb;

        public ItemViewHolder(View view) {
            super(view);
            initView(view);
        }

        private void initView(View view) {
            ivProfileVideoThumb = view.findViewById(R.id.ivProfileVideoThumb);
        }
    }

    public void setData(ArrayList<Object> allFeeds) {
        if (allFeeds != null && allFeeds.size() > 0)
            this.allFeeds = allFeeds;
    }

    public ProfileAdapter setProfileClickListner(ProfileClickListner profileClickListner) {
        this.profileClickListner = profileClickListner;
        return this;
    }

    public void setUserInfo(User userInfo) {
        if (userInfo != null)
            this.userInfo = userInfo;
    }
}
