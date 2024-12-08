package com.photex.tiktok.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.photex.tiktok.R;
import com.photex.tiktok.adapters.FollowingAdapter;
import com.photex.tiktok.endlessrecycle.EndlessRecyclerViewScrollListener;
import com.photex.tiktok.interfaces.UserInfoClickListner;
import com.photex.tiktok.models.FollowingList;
import com.photex.tiktok.models.restmodels.GetMyFollowList;
import com.photex.tiktok.models.restmodels.UserFollowingList;
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

public class FollowingAcitvity extends AppCompatActivity implements
        View.OnClickListener, UserInfoClickListner {

    private ImageView closeBtn;
    private RecyclerView rvFollowing;
    private FollowingAdapter followingAdapter;
    private LinearLayoutManager layoutManager;
    private TextView tvNoFollwing;
    private AVLoadingIndicatorView loadingIndicator;

    private String userId, myId;
    private String lastFollowingId;
    private ArrayList<FollowingList> followingList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_following);
        initializeView();

        followingAdapter = new FollowingAdapter(this)
                .setUserInfoClickListner(this);
        rvFollowing.setAdapter(followingAdapter);
        rvFollowing.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Log.i("loadMore", "Loading" + page + "  " + totalItemsCount);
                if (followingAdapter.getItemCount() >= 10) {
                    getFollowingData();
                }
            }
        });

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Constants.USER_ID)) {
            followingList = new ArrayList<>();
            myId = SettingManager.getUserId(FollowingAcitvity.this);
            userId = intent.getStringExtra(Constants.USER_ID);

            getFollowingData();
        }
    }

    private void getFollowingData() {
        if (Util.isNetworkAvailable(this)) {
            if (userId.equals(myId)) {
                getMyFollowing();
            } else {
                getUserFollowing();
            }
        } else {
            Util.internetNotAvailableDialouge(this);
        }
    }

    private void initializeView() {
        closeBtn = findViewById(R.id.close_btn);
        rvFollowing = findViewById(R.id.rv_following);
        loadingIndicator = findViewById(R.id.loading_indicator);
        tvNoFollwing = findViewById(R.id.tv_no_data);

        layoutManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false);
        rvFollowing.setLayoutManager(layoutManager);
        rvFollowing.setHasFixedSize(true);

        closeBtn.setOnClickListener(this);
    }

    public void getMyFollowing() {
        loadingIndicator.setVisibility(View.VISIBLE);

        int totalItem = followingAdapter.getItemCount();
        if (totalItem > 1) {
            lastFollowingId = followingAdapter.getUserId(totalItem - 1);
        } else {
            lastFollowingId = "0";
        }

        GetMyFollowList followList = new GetMyFollowList();
        followList.setMyId(userId);
        followList.setLastId(lastFollowingId);

        Call<String> call = new RestClient(Constants.BASE_URL, this).get()
                .getMyFollowingList(followList);

        call.enqueue(new CallbackWithRetry<String>(call) {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                loadingIndicator.setVisibility(View.GONE);
                if (response != null && response.body() != null) {
                    JSONArray jsonArray = null;
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        jsonArray = jsonObject.getJSONArray("followings");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (jsonArray != null) {
                        Gson gson = new Gson();
                        List<FollowingList> following;
                        following = Arrays.asList(gson.fromJson(jsonArray.toString(), FollowingList[].class));

                        if (following.size() > 0) {
                            lastFollowingId = following.get(following.size() - 1).get_id();

                            followingList.addAll(following);
                            followingAdapter.setData(followingList);
                            followingAdapter.notifyDataSetChanged();


                        }

                        if (followingList.size() <= 0) {
                            tvNoFollwing.setVisibility(View.VISIBLE);
                        } else {
                            tvNoFollwing.setVisibility(View.GONE);
                        }
                    } else {
                        Toast.makeText(FollowingAcitvity.this, "Some thing goes wrong please try again!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFinallyFail() {
                loadingIndicator.setVisibility(View.GONE);
            }
        });
    }

    public void getUserFollowing() {
        loadingIndicator.setVisibility(View.VISIBLE);

        int totalItem = followingAdapter.getItemCount();
        if (totalItem > 1) {
            lastFollowingId = followingAdapter.getUserId(totalItem - 1);
        } else {
            lastFollowingId = "0";
        }

        UserFollowingList followList = new UserFollowingList();
        followList.setMyId(myId);
        followList.setLastId(lastFollowingId);
        followList.setUserId(userId);

        Call<String> call = new RestClient(Constants.BASE_URL, this).get()
                .getUserFollowingList(followList);

        call.enqueue(new CallbackWithRetry<String>(call) {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                loadingIndicator.setVisibility(View.GONE);
                if (response != null && response.body() != null) {
                    JSONArray jsonArray = null;
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        jsonArray = jsonObject.getJSONArray("followings");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (jsonArray != null) {
                        Gson gson = new Gson();
                        List<FollowingList> following;
                        following = Arrays.asList(gson.fromJson(jsonArray.toString(), FollowingList[].class));

                        if (following.size() > 0) {
                            lastFollowingId = following.get(following.size() - 1).get_id();

                            followingList.addAll(following);
                            followingAdapter.setData(followingList);
                            followingAdapter.notifyDataSetChanged();


                        }

                        if (followingList.size() <= 0) {
                            tvNoFollwing.setVisibility(View.VISIBLE);
                        } else {
                            tvNoFollwing.setVisibility(View.GONE);
                        }
                    } else {
                        Toast.makeText(FollowingAcitvity.this, "Some thing goes wrong please try again!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFinallyFail() {
                loadingIndicator.setVisibility(View.GONE);
            }
        });
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
        }
    }

    @Override
    public void onUserImageClick(String userId) {
        startActivityForResult(new Intent(this, ProfileAcitvity.class)
                .putExtra(Constants.USER_ID, userId), Constants.PROFILE_DETAIL_REQUEST);
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.PROFILE_DETAIL_REQUEST) {
            if (MainActivity.isDataChanged) {
                followingList.clear();
                followingAdapter.setData(followingList);
                followingAdapter.notifyDataSetChanged();

                getFollowingData();
            }
        }
    }
}

