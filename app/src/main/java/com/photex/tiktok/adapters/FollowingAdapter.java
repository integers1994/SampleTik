package com.photex.tiktok.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.photex.tiktok.R;
import com.photex.tiktok.interfaces.UserInfoClickListner;
import com.photex.tiktok.models.FollowingList;
import com.photex.tiktok.setting.SettingManager;
import com.photex.tiktok.utils.Constants;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FollowingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    ArrayList<FollowingList> followingList;

    UserInfoClickListner userInfoClickListner;

    public FollowingAdapter(Context context) {
        this.context = context;
        followingList = new ArrayList<>();
    }

    @Override
    public FollowingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_user_following,
                parent, false);

        FollowingViewHolder followingViewHolder = new FollowingViewHolder(view);
        return followingViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int pos) {

        FollowingViewHolder followingViewHolder = (FollowingViewHolder) viewHolder;
        int position = followingViewHolder.getAdapterPosition();

        followingViewHolder.tvFullName.setText(followingList.get(position).getFollowingFullName());
        followingViewHolder.tvUserName.setText("@" + followingList.get(position).getFollowingUserName());

        Glide.with(context)
                .load(Constants.BASE_URL + Constants.SERVER_DIRECTORY + followingList.get(position).getFollowingDisplayPicture())
                .apply(new RequestOptions()
                        .signature(new ObjectKey(SettingManager.getProfilePicTime(context)))
                        .placeholder(R.drawable.user_image_place_holder).dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                .into(followingViewHolder.ivUserImage);

        followingViewHolder.ivUserImage.setOnClickListener(v ->
                userInfoClickListner.onUserImageClick(followingList.get(position).getFollowingId()));
    }

    @Override
    public int getItemCount() {
        return (followingList != null) ? followingList.size() : 0;
    }

    public static class FollowingViewHolder extends RecyclerView.ViewHolder {

        CircleImageView ivUserImage;
        TextView tvFullName, tvUserName, followBtn, unFollowBtn;

        public FollowingViewHolder(View view) {
            super(view);
            initView(view);
        }

        private void initView(View view) {
            ivUserImage = (CircleImageView) view.findViewById(R.id.ivUserImage);
            tvFullName = (TextView) view.findViewById(R.id.tvFullName);
            tvUserName = (TextView) view.findViewById(R.id.tvUserName);

            followBtn = (TextView) view.findViewById(R.id.follow_btn);
            unFollowBtn = (TextView) view.findViewById(R.id.un_follow_btn);
        }
    }

    public void setData(ArrayList<FollowingList> followingList) {
        if (followingList != null && followingList.size() > 0)
            this.followingList = followingList;
    }

    public FollowingAdapter setUserInfoClickListner(UserInfoClickListner userInfoClickListner) {
        this.userInfoClickListner = userInfoClickListner;
        return this;
    }

    public String getUserId(int index) {
        return followingList.get(index).get_id();
    }
}
