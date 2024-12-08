package com.photex.tiktok.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.photex.tiktok.R;
import com.photex.tiktok.adapters.ProfileAdapter;
import com.photex.tiktok.endlessrecycle.EndlessRecyclerViewScrollListener;
import com.photex.tiktok.interfaces.ProfileClickListner;
import com.photex.tiktok.models.Post;
import com.photex.tiktok.models.User;
import com.photex.tiktok.models.restmodels.Follow;
import com.photex.tiktok.models.restmodels.GetMyPosts;
import com.photex.tiktok.models.restmodels.GetUserProfile;
import com.photex.tiktok.models.restmodels.UnFollow;
import com.photex.tiktok.models.restmodels.UserPost;
import com.photex.tiktok.models.restmodels.UserProfile;
import com.photex.tiktok.rest.CallbackWithRetry;
import com.photex.tiktok.rest.RestClient;
import com.photex.tiktok.setting.SettingManager;
import com.photex.tiktok.utils.Constants;
import com.photex.tiktok.utils.Util;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ProfileAcitvity extends AppCompatActivity implements
        View.OnClickListener, ProfileClickListner {

    private ImageView closeBtn, moreOptionBtn;
    private TextView tvProfileTitle;

    private RecyclerView rvProfile;
    private GridLayoutManager layoutManager;
    private ProfileAdapter profileAdapter;

    private User info;
    private String userId, myId;
    private User userInfo;
    private AVLoadingIndicatorView profileLoadingIndicator;
    private String lastPostId;
    private ArrayList<Object> allPosts;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        initializeView();

        profileAdapter = new ProfileAdapter(this)
                .setProfileClickListner(this);
        rvProfile.setAdapter(profileAdapter);
        rvProfile.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Log.i("loadMore", "Loading" + page + "  " + totalItemsCount);
                if (profileAdapter.getItemCount() >= 11) {
                    getProfileData(true);
                }
            }
        });

        // adding dummy header in View
        allPosts = new ArrayList<>();
        allPosts.add(null);

        profileAdapter.setData(allPosts);
        profileAdapter.notifyDataSetChanged();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Constants.USER_ID)) {
            myId = SettingManager.getUserId(ProfileAcitvity.this);
            userId = intent.getStringExtra(Constants.USER_ID);

            getProfileData(false);
        }
    }

    private void getProfileData(boolean isLoadMoreCalled) {
        if (Util.isNetworkAvailable(this)) {
            if (!isLoadMoreCalled) {
                if (userId.equals(myId)) {
                    getMyProfile(userId);
                    getMyPosts(userId);
                } else {
                    getUserProfile(userId, myId);
                    getUserPosts(userId, myId);
                }
            } else {
                if (userId.equals(myId)) {
                    getMyPosts(userId);
                } else {
                    getUserPosts(userId, myId);
                }
            }

        } else {
            Util.internetNotAvailableDialouge(this);
        }
    }

    private void initializeView() {
        closeBtn = findViewById(R.id.close_profile_btn);
        moreOptionBtn = findViewById(R.id.more_option_btn);
        tvProfileTitle = findViewById(R.id.tvProfileTitle);
        rvProfile = findViewById(R.id.rv_profile);
        profileLoadingIndicator = findViewById(R.id.profile_loading_indicator);

        layoutManager = new GridLayoutManager(this, 3);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return (position == 0) ? 3 : 1;
            }
        });
        rvProfile.setLayoutManager(layoutManager);


        closeBtn.setOnClickListener(this);
        moreOptionBtn.setOnClickListener(this);
    }

    private void getMyProfile(String userId) {
        UserProfile userProfile = new UserProfile();
        userProfile.setId(userId);

        Call<String> call = new RestClient(Constants.BASE_URL, ProfileAcitvity.this).get()
                .userProfile(userProfile);
        call.enqueue(new CallbackWithRetry<String>(call) {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response != null && response.body() != null) {

                    JSONObject jsonUser = null;
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());

                        if (jsonObject.getBoolean("success")) {
                            jsonUser = jsonObject.getJSONObject("user");
                            if (jsonUser != null) {
                                Gson gson = new Gson();
                                userInfo = gson.fromJson(jsonUser.toString(), User.class);
                                tvProfileTitle.setText(userInfo.getFullName());
                                profileAdapter.setUserInfo(userInfo);
                                profileAdapter.notifyDataSetChanged();


                            }
                        }
                    } catch (Exception e) {
                        onFinallyFail();
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFinallyFail() {
                Log.i("Profile", "onFinallyFail");
            }
        });
    }

    public void getMyPosts(String userId) {
        profileLoadingIndicator.setVisibility(View.VISIBLE);

        int totalItem = profileAdapter.getItemCount();
        if (totalItem > 1) {
            lastPostId = profileAdapter.getPostId(totalItem - 1);
        } else {
            lastPostId = "0";
        }

        GetMyPosts getMyPosts = new GetMyPosts();
        getMyPosts.setMyId(userId);
        getMyPosts.setLastId(lastPostId);

        Call<String> call = new RestClient(Constants.BASE_URL, ProfileAcitvity.this).get()
                .getMyPosts(getMyPosts);
        call.enqueue(new CallbackWithRetry<String>(call) {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                profileLoadingIndicator.setVisibility(View.GONE);
                if (response.body() != null) {
                    JSONArray jsonArray = null;
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        jsonArray = jsonObject.getJSONArray("posts");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (jsonArray != null) {
                        Gson gson = new Gson();
                        List<Post> posts;
                        posts = Arrays.asList(gson.fromJson(jsonArray.toString(), Post[].class));
                        if (posts.size() > 0) {
                            lastPostId = posts.get(posts.size() - 1).get_id();

                            allPosts.addAll(posts);
                            profileAdapter.setData(allPosts);
                            profileAdapter.notifyItemChanged(0);
                        }

                    } else {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            String message = jsonObject.getString("message");
                            if (message != null && message.equals(getString(R.string.jwt_expired))) {
                                Toast.makeText(ProfileAcitvity.this, "Token expired! Please login again", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ProfileAcitvity.this, SignInActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        } catch (Exception e) {
                            Toast.makeText(ProfileAcitvity.this, "Some thing goes wrong! Please try again", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFinallyFail() {
                profileLoadingIndicator.setVisibility(View.GONE);
            }
        });
    }

    public void getUserPosts(String userId, String myId) {
        profileLoadingIndicator.setVisibility(View.VISIBLE);

        int totalItem = profileAdapter.getItemCount();
        if (totalItem > 1) {
            lastPostId = profileAdapter.getPostId(totalItem - 1);
        } else {
            lastPostId = "0";
        }

        UserPost userPost = new UserPost();
        userPost.setMyId(myId);
        userPost.setUserId(userId);
        userPost.setLastId(lastPostId);

        Call<String> call = new RestClient(Constants.BASE_URL, ProfileAcitvity.this).get()
                .getUserPost(userPost);
        call.enqueue(new CallbackWithRetry<String>(call) {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                profileLoadingIndicator.setVisibility(View.GONE);
                if (response != null && response.body() != null) {
                    JSONArray jsonArray = null;
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        jsonArray = jsonObject.getJSONArray("posts");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (jsonArray != null) {
                        Gson gson = new Gson();
                        List<Post> posts;
                        posts = Arrays.asList(gson.fromJson(jsonArray.toString(), Post[].class));
                        if (posts.size() > 0) {
                            allPosts.addAll(posts);
                            lastPostId = posts.get(posts.size() - 1).get_id();

                            profileAdapter.setData(allPosts);
                            profileAdapter.notifyDataSetChanged();
                        }

                    } else {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            String message = jsonObject.getString("message");
                            if (message != null && message.equals(getString(R.string.jwt_expired))) {
                                Toast.makeText(ProfileAcitvity.this, "Token expired! Please login again", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ProfileAcitvity.this, SignInActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        } catch (Exception e) {
                            Toast.makeText(ProfileAcitvity.this, "Some thing goes wrong! Please try again", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFinallyFail() {
                profileLoadingIndicator.setVisibility(View.GONE);
            }
        });
    }

    private void getUserProfile(String userId, String myId) {
        GetUserProfile getUserProfile = new GetUserProfile();
        getUserProfile.setUserId(userId);
        getUserProfile.setMyId(myId);

        Call<String> call = new RestClient(Constants.BASE_URL, ProfileAcitvity.this).get()
                .getUserProfile(getUserProfile);
        call.enqueue(new CallbackWithRetry<String>(call) {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response != null && response.body() != null) {

                    JSONObject jsonUser = null;
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());

                        if (jsonObject.getBoolean("success")) {
                            jsonUser = jsonObject.getJSONObject("user");
                            if (jsonUser != null) {
                                Gson gson = new Gson();
                                userInfo = gson.fromJson(jsonUser.toString(), User.class);
                                tvProfileTitle.setText(userInfo.getFullName());
                                profileAdapter.setUserInfo(userInfo);
                                profileAdapter.notifyItemChanged(0);
                            }
                        }
                    } catch (Exception e) {
                        onFinallyFail();
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFinallyFail() {
//                profileLoadingIndicator.setVisibility(View.GONE);
                Log.i("Profile", "onFinallyFail");
            }
        });
    }

    public void userFollow(Follow follow, TextView btnFollow) {
        if (Util.isNetworkAvailable(this)) {
            btnFollow.setEnabled(false);
            profileLoadingIndicator.setVisibility(View.VISIBLE);

            Call<String> call = new RestClient(Constants.BASE_URL, ProfileAcitvity.this).get()
                    .follow(follow);
            call.enqueue(new CallbackWithRetry<String>(call) {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    int count = Integer.parseInt(userInfo.getFollowersCount());
                    count++;
                    userInfo.setFollowersCount(count + "");
                    userInfo.setFollowed(true);
                    profileAdapter.setUserInfo(userInfo);
                    profileAdapter.notifyItemChanged(0);

                    profileLoadingIndicator.setVisibility(View.GONE);
                    btnFollow.setEnabled(true);
                    MainActivity.isDataChanged = true;
                }

                @Override
                public void onFinallyFail() {
                    profileLoadingIndicator.setVisibility(View.GONE);
                    btnFollow.setEnabled(true);
                    Toast.makeText(ProfileAcitvity.this, "Some Thing goes wrong! please try again", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Util.internetNotAvailableDialouge(ProfileAcitvity.this);
        }
    }

    public void userUnFollow(UnFollow unFollow, TextView btnUnFollow) {
        if (Util.isNetworkAvailable(this)) {
            btnUnFollow.setEnabled(false);
            profileLoadingIndicator.setVisibility(View.VISIBLE);

            Call<String> call = new RestClient(Constants.BASE_URL, ProfileAcitvity.this).get()
                    .unFollow(unFollow);
            call.enqueue(new CallbackWithRetry<String>(call) {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    int count = Integer.parseInt(userInfo.getFollowersCount());
                    if (count > 0) count--;
                    userInfo.setFollowersCount(count + "");
                    userInfo.setFollowed(false);

                    profileAdapter.setUserInfo(userInfo);
                    profileAdapter.notifyItemChanged(0);

                    profileLoadingIndicator.setVisibility(View.GONE);
                    btnUnFollow.setEnabled(true);
                    MainActivity.isDataChanged = true;
                }

                @Override
                public void onFinallyFail() {
                    profileLoadingIndicator.setVisibility(View.GONE);
                    btnUnFollow.setEnabled(true);
                    Toast.makeText(ProfileAcitvity.this, "Some Thing goes wrong! please try again", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Util.internetNotAvailableDialouge(ProfileAcitvity.this);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    @Override
    public void onClick(View v) {
        if (v == closeBtn) {
            onBackPressed();
        } else if (v == moreOptionBtn) {

        }
    }

    @Override
    public void onVideoClick(int position, ArrayList<Object> allFeeds) {
        if (allFeeds != null) {
            startActivity(new Intent(ProfileAcitvity.this, ProfileVideosActivity.class)
                    .putExtra(Constants.currentVideoPostion, position)
                    .putExtra(Constants.profileVideos, allFeeds)
                    .putExtra(Constants.USER_ID, userId));
            overridePendingTransition(R.anim.enter, R.anim.exit);
        }
    }

    @Override
    public void onEditProfileBtn() {
        if (userInfo != null) {
            startActivityForResult(new Intent(this, ProfileUpdateAcitvity.class)
                    .putExtra(Constants.CURRENT_USER, userInfo), Constants.PROFILE_DETAIL_REQUEST);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        }
    }

    @Override
    public void onFollowBtn(TextView followBtn) {
        if (userInfo != null && userInfo.getFollowersCount() != null
                && !userInfo.getFollowersCount().isEmpty()) {

            Follow follow = new Follow();
            follow.setUserId(SettingManager.getUserId(this));
            follow.setDisplayPicture(SettingManager.getUserPictureURL(this));
            follow.setFollowId(userInfo.get_id());
            follow.setFullName(SettingManager.getUserName(this));

            userFollow(follow, followBtn);
        }
    }

    @Override
    public void onUnFollowBtn(TextView unFollowBtn) {
        if (userInfo != null && userInfo.getFollowersCount() != null
                && !userInfo.getFollowersCount().isEmpty()) {

            UnFollow unFollow = new UnFollow();
            unFollow.setUserId(userInfo.get_id());
            unFollow.setMyId(SettingManager.getUserId(this));

            userUnFollow(unFollow, unFollowBtn);
        }
    }

    @Override
    public void onUserFollowingBtn() {
        if (userInfo != null && userInfo.getFollowingCount() != null
                && !userInfo.getFollowingCount().isEmpty()) {

            startActivityForResult(new Intent(this, FollowingAcitvity.class)
                    .putExtra(Constants.USER_ID, userInfo.get_id()), Constants.PROFILE_DETAIL_REQUEST);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        }
    }

    @Override
    public void onUserFansBtn() {
        if (userInfo != null && userInfo.getFollowersCount() != null
                && !userInfo.getFollowersCount().isEmpty()) {

            startActivityForResult(new Intent(this, FansAcitvity.class)
                    .putExtra(Constants.USER_ID, userInfo.get_id()), Constants.PROFILE_DETAIL_REQUEST);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.PROFILE_DETAIL_REQUEST) {
            if (MainActivity.isDataChanged) {
                if (userId.equals(myId)) {
                    getMyProfile(userId);
                } else {
                    getUserProfile(userId, myId);
                }
            }
        }
    }
}
