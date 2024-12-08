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
import com.photex.tiktok.adapters.FansAdapter;
import com.photex.tiktok.endlessrecycle.EndlessRecyclerViewScrollListener;
import com.photex.tiktok.interfaces.UserInfoClickListner;
import com.photex.tiktok.models.FollowList;
import com.photex.tiktok.models.restmodels.GetMyFollowList;
import com.photex.tiktok.models.restmodels.GetUserFollowers;
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

public class FansAcitvity extends AppCompatActivity implements
        View.OnClickListener,
        UserInfoClickListner {

    private ImageView closeBtn;
    private RecyclerView rvFans;
    private FansAdapter fansAdapter;
    private LinearLayoutManager layoutManager;
    private TextView tvNoData;
    private AVLoadingIndicatorView loadingIndicator;

    private String userId, myId;
    private String lastId;
    private ArrayList<FollowList> fansList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_fans);
        initializeView();

        fansAdapter = new FansAdapter(this)
                .setUserInfoClickListner(this);
        rvFans.setAdapter(fansAdapter);
        rvFans.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Log.i("loadMore", "Loading" + page + "  " + totalItemsCount);
                if (fansAdapter.getItemCount() >= 10) {
                    getFansData();
                }
            }
        });

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Constants.USER_ID)) {
            fansList = new ArrayList<>();
            myId = SettingManager.getUserId(FansAcitvity.this);
            userId = intent.getStringExtra(Constants.USER_ID);

            getFansData();
        }
    }

    private void getFansData() {
        if (Util.isNetworkAvailable(this)) {
            if (userId.equals(myId)) {
                getMyFans();
            } else {
                getUserFans();
            }
        } else {
            Util.internetNotAvailableDialouge(this);
        }
    }

    private void initializeView() {
        closeBtn = findViewById(R.id.close_btn);
        rvFans = findViewById(R.id.rv_following);
        loadingIndicator = findViewById(R.id.loading_indicator);
        tvNoData = findViewById(R.id.tv_no_data);

        layoutManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false);
        rvFans.setLayoutManager(layoutManager);
        rvFans.setHasFixedSize(true);

        closeBtn.setOnClickListener(this);
    }

    public void getMyFans() {
        loadingIndicator.setVisibility(View.VISIBLE);

        int totalItem = fansAdapter.getItemCount();
        if (totalItem > 1) {
            lastId = fansAdapter.getUserId(totalItem - 1);
        } else {
            lastId = "0";
        }

        GetMyFollowList followList = new GetMyFollowList();
        followList.setMyId(userId);
        followList.setLastId(lastId);

        Call<String> call = new RestClient(Constants.BASE_URL, this).get()
                .getMyFollwersList(followList);

        call.enqueue(new CallbackWithRetry<String>(call) {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                loadingIndicator.setVisibility(View.GONE);
                if (response != null && response.body() != null) {
                    JSONArray jsonArray = null;
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        jsonArray = jsonObject.getJSONArray("followers");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (jsonArray != null) {
                        Gson gson = new Gson();
                        List<FollowList> following;
                        following = Arrays.asList(gson.fromJson(jsonArray.toString(), FollowList[].class));

                        if (following.size() > 0) {
                            lastId = following.get(following.size() - 1).get_id();

                            fansList.addAll(following);
                            fansAdapter.setData(fansList);
                            fansAdapter.notifyDataSetChanged();
                        }

                        if (fansList.size() <= 0) {
                            tvNoData.setVisibility(View.VISIBLE);
                        } else {
                            tvNoData.setVisibility(View.GONE);
                        }
                    } else {
                        Toast.makeText(FansAcitvity.this, "Some thing goes wrong please try again!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFinallyFail() {
                loadingIndicator.setVisibility(View.GONE);
            }
        });
    }

    public void getUserFans() {
        loadingIndicator.setVisibility(View.VISIBLE);

        int totalItem = fansAdapter.getItemCount();
        if (totalItem > 1) {
            lastId = fansAdapter.getUserId(totalItem - 1);
        } else {
            lastId = "0";
        }

        GetUserFollowers followList = new GetUserFollowers();
        followList.setMyId(myId);
        followList.setUserId(userId);
        followList.setLastId(lastId);

        Call<String> call = new RestClient(Constants.BASE_URL, this).get()
                .getUserFollowersList(followList);

        call.enqueue(new CallbackWithRetry<String>(call) {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                loadingIndicator.setVisibility(View.GONE);
                if (response != null && response.body() != null) {
                    JSONArray jsonArray = null;
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        jsonArray = jsonObject.getJSONArray("followers");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (jsonArray != null) {
                        Gson gson = new Gson();
                        List<FollowList> following;
                        following = Arrays.asList(gson.fromJson(jsonArray.toString(), FollowList[].class));

                        if (following.size() > 0) {
                            lastId = following.get(following.size() - 1).get_id();

                            fansList.addAll(following);
                            fansAdapter.setData(fansList);
                            fansAdapter.notifyDataSetChanged();


                        }

                        if (fansList.size() <= 0) {
                            tvNoData.setVisibility(View.VISIBLE);
                        } else {
                            tvNoData.setVisibility(View.GONE);
                        }
                    } else {
                        Toast.makeText(FansAcitvity.this, "Some thing goes wrong please try again!", Toast.LENGTH_SHORT).show();
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
                fansList.clear();
                fansAdapter.setData(fansList);
                fansAdapter.notifyDataSetChanged();

                getFansData();
            }
        }
    }
}

