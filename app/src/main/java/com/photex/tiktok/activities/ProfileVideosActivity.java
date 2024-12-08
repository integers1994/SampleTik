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

package com.photex.tiktok.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.gson.Gson;
import com.photex.tiktok.R;
import com.photex.tiktok.adapters.AppsListAdapter;
import com.photex.tiktok.adapters.FeedsAdapter;
import com.photex.tiktok.endlessrecycle.EndlessRecyclerViewScrollListener;
import com.photex.tiktok.interfaces.AppClickListener;
import com.photex.tiktok.interfaces.PostClickListner;
import com.photex.tiktok.interfaces.VideoPlayerListner;
import com.photex.tiktok.models.AppsInfo;
import com.photex.tiktok.models.FirebaseDynamicLinkCreator;
import com.photex.tiktok.models.Post;
import com.photex.tiktok.models.restmodels.GetMyPosts;
import com.photex.tiktok.models.restmodels.SharePost;
import com.photex.tiktok.models.restmodels.UserPost;
import com.photex.tiktok.rest.CallbackWithRetry;
import com.photex.tiktok.rest.RestClient;
import com.photex.tiktok.setting.SettingManager;
import com.photex.tiktok.utils.Constants;
import com.photex.tiktok.utils.Util;
import com.roger.catloadinglibrary.CatLoadingView;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import im.ene.toro.PlayerSelector;
import im.ene.toro.media.PlaybackInfo;
import im.ene.toro.media.VolumeInfo;
import im.ene.toro.widget.Container;
import retrofit2.Call;
import retrofit2.Response;

import static im.ene.toro.media.PlaybackInfo.INDEX_UNSET;
import static im.ene.toro.media.PlaybackInfo.TIME_UNSET;

public class ProfileVideosActivity extends AppCompatActivity implements
        VideoPlayerListner,
        PostClickListner,
        AppClickListener,
        View.OnClickListener {

    public static boolean isDataChanged;
    private String TAG = "mainActivity";
    Container container;
    RecyclerView rvAppsList;
    FeedsAdapter feedsAdapter;
    ProgressBar playerProgressIndicator;
    AVLoadingIndicatorView postLoadingIndicator;
    LinearLayout shareBottomSheet;
    ImageView closeShareBtn;

    ArrayList<Object> allPosts;

    String lastPostId = "0";
    boolean enableCheckPost = true;
    private int feedPosition;
    private LinearLayoutManager layoutManager;
    private CatLoadingView catLoadingView;
    private int totalShares = 0;
    private AppsListAdapter appsListAdapter;
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    private String videoLink;

    private int totalLoadedVideos = 0, currentVideoPosition = 0;
    private String userId, myId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_video);
        transpareentStatusBar();
        initializeView();

        isDataChanged = false;
        allPosts = new ArrayList<>();

        Intent intent = getIntent();
        if (intent != null &&
                intent.hasExtra(Constants.profileVideos) &&
                intent.hasExtra(Constants.currentVideoPostion) &&
                intent.hasExtra(Constants.USER_ID)) {

            myId = SettingManager.getUserId(ProfileVideosActivity.this);
            userId = intent.getStringExtra(Constants.USER_ID);

            allPosts = (ArrayList<Object>) intent.getExtras().get(Constants.profileVideos);
            currentVideoPosition = intent.getExtras().getInt(Constants.currentVideoPostion);
            totalLoadedVideos = allPosts.size();

            appsListAdapter = new AppsListAdapter(this)
                    .setAppClickListener(this);
            rvAppsList.setAdapter(appsListAdapter);

            // See FeedsAdapter for detail usage.
            feedsAdapter = new FeedsAdapter(this, PlayerSelector.DEFAULT)
                    .setVideoPlayerListner(this)
                    .setPostClickListner(this);
            container.setPlayerSelector(PlayerSelector.DEFAULT);
            container.setAdapter(feedsAdapter);
//        container.setPlayerDispatcher(__ -> 50); // The playback will be delayed 500ms.
            container.setPlayerInitializer(order -> {
                VolumeInfo volumeInfo = new VolumeInfo(false, 0.75f);
                return new PlaybackInfo(INDEX_UNSET, TIME_UNSET, volumeInfo);
            });

            container.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount) {
                    Log.i("loadMore", "Loading" + page + "  " + totalItemsCount);
                    if (feedsAdapter.getItemCount() >= 10)
                        getProfileData();
                }
            });

            SnapHelper snapHelper = new PagerSnapHelper();
            snapHelper.attachToRecyclerView(container);

            feedsAdapter.setData(allPosts);
            feedsAdapter.notifyDataSetChanged();
            container.scrollToPosition(currentVideoPosition);
        }
    }

    private void initializeView() {
        container = findViewById(R.id.container);
        layoutManager = new LinearLayoutManager(getBaseContext(), RecyclerView.VERTICAL, false);
        container.setLayoutManager(layoutManager);

        shareBottomSheet = findViewById(R.id.share_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(shareBottomSheet);

        rvAppsList = findViewById(R.id.rv_apps_list);
        rvAppsList.setLayoutManager(new GridLayoutManager(this, 4));
        rvAppsList.setHasFixedSize(true);

        playerProgressIndicator = findViewById(R.id.player_progress_indicator);
        postLoadingIndicator = findViewById(R.id.post_loading_indicator);
        closeShareBtn = findViewById(R.id.btn_close_share_sheet);

        closeShareBtn.setOnClickListener(this);
    }

    private void getProfileData() {
        if (Util.isNetworkAvailable(this)) {
            if (userId.equals(myId)) {
                getMyPosts(userId);
            } else {
                getUserPosts(userId, myId);
            }
        } else {
            Util.internetNotAvailableDialouge(this);
        }
    }

    public void getMyPosts(String userId) {
        postLoadingIndicator.setVisibility(View.VISIBLE);

        int totalItem = feedsAdapter.getItemCount();
        if (totalItem > 1) {
            lastPostId = feedsAdapter.getPostId(totalItem - 1);
        } else {
            lastPostId = "0";
        }

        GetMyPosts getMyPosts = new GetMyPosts();
        getMyPosts.setMyId(userId);
        getMyPosts.setLastId(lastPostId);

        Call<String> call = new RestClient(Constants.BASE_URL, ProfileVideosActivity.this).get()
                .getMyPosts(getMyPosts);
        call.enqueue(new CallbackWithRetry<String>(call) {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                postLoadingIndicator.setVisibility(View.GONE);
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
                            lastPostId = posts.get(posts.size() - 1).get_id();

                            allPosts.addAll(posts);
                            feedsAdapter.setData(allPosts);
                            feedsAdapter.notifyItemChanged(0);
                        }

                    } else {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            String message = jsonObject.getString("message");
                            if (message != null && message.equals(getString(R.string.jwt_expired))) {
                                Toast.makeText(ProfileVideosActivity.this, "Token expired! Please login again", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ProfileVideosActivity.this, SignInActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        } catch (Exception e) {
                            Toast.makeText(ProfileVideosActivity.this, "Some thing goes wrong! Please try again", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFinallyFail() {
                postLoadingIndicator.setVisibility(View.GONE);
            }
        });
    }

    public void getUserPosts(String userId, String myId) {
        postLoadingIndicator.setVisibility(View.VISIBLE);

        int totalItem = feedsAdapter.getItemCount();
        if (totalItem > 1) {
            lastPostId = feedsAdapter.getPostId(totalItem - 1);
        } else {
            lastPostId = "0";
        }

        UserPost userPost = new UserPost();
        userPost.setMyId(myId);
        userPost.setUserId(userId);
        userPost.setLastId(lastPostId);

        Call<String> call = new RestClient(Constants.BASE_URL, ProfileVideosActivity.this).get()
                .getUserPost(userPost);
        call.enqueue(new CallbackWithRetry<String>(call) {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                postLoadingIndicator.setVisibility(View.GONE);
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

                            feedsAdapter.setData(allPosts);
                            feedsAdapter.notifyDataSetChanged();
                        }

                    } else {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            String message = jsonObject.getString("message");
                            if (message != null && message.equals(getString(R.string.jwt_expired))) {
                                Toast.makeText(ProfileVideosActivity.this, "Token expired! Please login again", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ProfileVideosActivity.this, SignInActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        } catch (Exception e) {
                            Toast.makeText(ProfileVideosActivity.this, "Some thing goes wrong! Please try again", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFinallyFail() {
                postLoadingIndicator.setVisibility(View.GONE);
            }
        });
    }


    private void setupFeed() {
        if (allPosts != null && allPosts.size() > 0) {

            feedsAdapter.setData(allPosts);
            feedsAdapter.notifyDataSetChanged();
//            feedsAdapter.setOnFeedItemClickListener(this);
            /*rvFeed.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount) {
                    Log.i("loadMore", "Loading" + page + "  " + totalItemsCount);

                    if (!swipeRefreshLayout.isRefreshing()) {
                        loadMoreProgressBar.setVisibility(View.VISIBLE);
                        getPosts();
                    }

                }
            });
            rvFeed.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0) {
                        if (isNewPostAvailable)
                            btnNewPost.setVisibility(View.GONE);
                        layBottomBarWithIcons.setVisibility(View.GONE);
                    } else {
                        if (isNewPostAvailable)
                            btnNewPost.setVisibility(View.VISIBLE);
                        layBottomBarWithIcons.setVisibility(View.VISIBLE);
                    }
                }
            });
            rvFeed.setItemAnimator(new FeedsItemAnimator());*/

        }

    }

    private void transpareentStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            WindowManager.LayoutParams winParams = window.getAttributes();
            winParams.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            window.setAttributes(winParams);
            window.getDecorView()
                    .setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    private void shareVideoLink(String videoLink, AppsInfo appsInfo) {
        // creating dynamic video link
        String newVideoLink = new FirebaseDynamicLinkCreator(videoLink).getDynamicLink();
        catLoadingView = new CatLoadingView();
        catLoadingView.setCancelable(true);
        catLoadingView.setText("Preparing ...");
        catLoadingView.show(getSupportFragmentManager(), "");
        // setLongLink(Uri.parse("https://example.page.link/?link=https://www.example.com/&apn=com.example.android&ibn=com.example.ios"))
        // [START shorten_long_link]

        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(Uri.parse(newVideoLink))
                .buildShortDynamicLink()
                .addOnCompleteListener(this, task -> {

                    catLoadingView.dismiss();

                    String shortVideoLink = newVideoLink;

                    if (task.isSuccessful()) {
                        // Short link created
                        Uri shortLink = task.getResult().getShortLink();
                        Uri flowchartLink = task.getResult().getPreviewLink();
                        shortVideoLink = shortLink.toString();

                    } else {
                        Log.e(TAG, "ShortDynamicLink: onFailure");
                    }

                    Intent sendIntent = new Intent();
                    String msg = "Hey, check this out: " + shortVideoLink;
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
                    sendIntent.setType("text/plain");
                    sendIntent.setPackage(appsInfo.getPackageName());
                    startActivityForResult(sendIntent, Constants.SHARE_VIDEO_REQUEST);
                });
    }

    private void updateShareCount() {
        if (Util.isNetworkAvailable(ProfileVideosActivity.this)) {

            SharePost sharePost = new SharePost();
            sharePost.setPostId(((Post) allPosts.get(feedPosition)).get_id());

            Call<String> call = new RestClient(Constants.BASE_URL, ProfileVideosActivity.this).get()
                    .sharePost(sharePost);

            call.enqueue(new CallbackWithRetry<String>(call) {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Log.i(TAG, "updateShareCount: onResponse");
                }

                @Override
                public void onFinallyFail() {
                    Log.i(TAG, "updateShareCount: onFinallyFail");
                }
            });
        }
    }

    public void getRelatedApps() {
        List<ResolveInfo> activities = getPackageManager().queryIntentActivities(new Intent(Intent.ACTION_SEND)
                        .setType("text/plain")
                        .putExtra(Intent.EXTRA_TEXT, "text")
                , 0);

        ArrayList<AppsInfo> appsList = new ArrayList<>();
        for (ResolveInfo info : activities) {
            try {
                AppsInfo appsInfo = new AppsInfo();
                appsInfo.setName(info.loadLabel(getPackageManager()).toString());
                appsInfo.setPackageName(info.activityInfo.packageName);
                appsInfo.setIcon(getPackageManager().getApplicationIcon(appsInfo.getPackageName()));

                appsList.add(appsInfo);

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        appsListAdapter.setData(appsList);
        appsListAdapter.notifyDataSetChanged();
        rvAppsList.smoothScrollToPosition(0);
    }

    @Override
    public void onVideoBuffering() {
        Log.i("PlayeState", "onVideoBuffering");
        playerProgressIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    public void onVideoPlaying() {
        Log.i("PlayeState", "onVideoPlaying");
        playerProgressIndicator.setVisibility(View.GONE);
    }

    @Override
    public void onVideoPause() {
        Log.i("PlayeState", "onVideoPause");
    }

    @Override
    public void onVideoComplete() {
        Log.i("PlayeState", "onVideoComplete");
    }

    @Override
    public void onProfileBtn(String userId) {
        onBackPressed();
    }

    @Override
    public void onCommentBtn(int itemPosition, String postId, String postById, int totalComments) {
        this.feedPosition = itemPosition;
        startActivityForResult(new Intent(ProfileVideosActivity.this, CommentsActivity.class)
                        .putExtra(Constants.EXTRA_POST_INFO, postId)
                        .putExtra(Constants.postById, postById)
                        .putExtra(Constants.totalComments, totalComments),
                Constants.COMMENT_ACTIVITY_REQUEST);
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    @Override
    public void onShareBtn(int itemPosition, String videoLink, int totalShares) {
        this.feedPosition = itemPosition;
        this.totalShares = totalShares;
        this.videoLink = videoLink;
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        getRelatedApps();
    }

    @Override
    protected void onStop() {
        if (catLoadingView != null) {
            catLoadingView.dismiss();
        }
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.COMMENT_ACTIVITY_REQUEST) {
            if (resultCode == RESULT_OK &&
                    data != null &&
                    data.hasExtra(Constants.totalComments)) {

                feedsAdapter.updateCommentsInfo(feedPosition, (int) data.getExtras().get(Constants.totalComments));
            }
        } else if (requestCode == Constants.SHARE_VIDEO_REQUEST) {
            if (resultCode == RESULT_OK || resultCode == RESULT_CANCELED) {
                feedsAdapter.updateShareInfo(feedPosition, ++totalShares);
                updateShareCount();
            }
        }
    }

    @Override
    public void onAppClick(AppsInfo appsInfo) {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        shareVideoLink(videoLink, appsInfo);
    }

    @Override
    public void onClick(View view) {
        if (view == closeShareBtn) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }
}
