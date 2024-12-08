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

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
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
import com.photex.tiktok.models.User;
import com.photex.tiktok.models.restmodels.GetAllPost;
import com.photex.tiktok.models.restmodels.SharePost;
import com.photex.tiktok.rest.CallbackWithRetry;
import com.photex.tiktok.rest.RestClient;
import com.photex.tiktok.services.UploadProfilePictureService;
import com.photex.tiktok.setting.SettingManager;
import com.photex.tiktok.utils.Constants;
import com.photex.tiktok.utils.Util;
import com.roger.catloadinglibrary.CatLoadingView;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import im.ene.toro.PlayerSelector;
import im.ene.toro.media.PlaybackInfo;
import im.ene.toro.media.VolumeInfo;
import im.ene.toro.widget.Container;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Response;

import static im.ene.toro.media.PlaybackInfo.INDEX_UNSET;
import static im.ene.toro.media.PlaybackInfo.TIME_UNSET;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
        VideoPlayerListner,
        EasyPermissions.PermissionCallbacks,
        PostClickListner, AppClickListener {

    public static boolean isDataChanged;
    private String TAG = MainActivity.class.getSimpleName();
    Container container;
    RecyclerView rvAppsList;
    FeedsAdapter feedsAdapter;
    ImageView btn_create_video, btn_profile, btn_close_share_sheet;
    ProgressBar playerProgressIndicator;
    SwipeRefreshLayout swipeRefreshLayout;
    AVLoadingIndicatorView postLoadingIndicator;
    LinearLayout shareBottomSheet;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        transparentStatusBar();
        initializeView();
        handleDynamicLink();
        Log.d(TAG, SettingManager.getServerToken(this));

        isDataChanged = false;
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Constants.UPLOAD_PHOTO)) {
            // upload profile picture
            if (!SettingManager.getIsProfilePictureUploaded(this)) {
                new Thread(() -> userDisplayPicture(
                        SettingManager.getUserEmail(MainActivity.this),
                        SettingManager.getProfilePictureURL(MainActivity.this))
                ).run();
            }
        }

        allPosts = new ArrayList<>();

        appsListAdapter = new AppsListAdapter(this)
                .setAppClickListener(this);
        rvAppsList.setAdapter(appsListAdapter);

        // See FeedsAdapter for detail usage.
        feedsAdapter = new FeedsAdapter(this, PlayerSelector.DEFAULT)
                .setVideoPlayerListner(this)
                .setPostClickListner(this);
        container.setPlayerSelector(PlayerSelector.DEFAULT);
        container.setAdapter(feedsAdapter);
        container.setPlayerDispatcher(__ -> 50); // The playback will be delayed 500ms.
        container.setPlayerInitializer(order -> {
            VolumeInfo volumeInfo = new VolumeInfo(false, 0.75f);
            return new PlaybackInfo(INDEX_UNSET, TIME_UNSET, volumeInfo);
        });

        container.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {

                Log.i("loadMore", "Loading" +
                        page + "  " + totalItemsCount);
                if (feedsAdapter.getItemCount() >= 10)
                    getPosts();
            }
        });

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(container);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            lastPostId = "0";

            getPosts();

            if (!enableCheckPost) {
                enableCheckPost = true;
            }
        });

        getPosts();
    }

    private void handleDynamicLink() {
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                        }

                        Log.w(TAG, "deepLink:" + deepLink);
                        // Handle the deep link. For example, open the linked
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "getDynamicLink:onFailure", e);
                    }
                });

    /*    FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnCompleteListener(new OnCompleteListener<PendingDynamicLinkData>() {
                    @Override
                    public void onComplete(@NonNull Task<PendingDynamicLinkData> task) {
                        if (!task.isSuccessful()) {
                            // Handle error
                            // ...
                        }

                        FirebaseAppInvite invite = FirebaseAppInvite.getInvitation(task.getResult());
                        if (invite != null) {
                            // Handle invite
                            // ...
                        }
                    }
                });*/
    }

    private void initializeView() {
        container = findViewById(R.id.container);
        layoutManager = new LinearLayoutManager(getActivity(),
                RecyclerView.VERTICAL, false);
        container.setLayoutManager(layoutManager);

        shareBottomSheet = findViewById(R.id.share_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(shareBottomSheet);

        rvAppsList = findViewById(R.id.rv_apps_list);
        rvAppsList.setLayoutManager(new GridLayoutManager(this, 4));
        rvAppsList.setHasFixedSize(true);

        playerProgressIndicator = findViewById(R.id.player_progress_indicator);
        swipeRefreshLayout = findViewById(R.id.swipe_container);
        postLoadingIndicator = findViewById(R.id.post_loading_indicator);


        btn_create_video = findViewById(R.id.img_create_video);
        btn_profile = findViewById(R.id.img_profile);
        btn_close_share_sheet = findViewById(R.id.btn_close_share_sheet);

        btn_create_video.setOnClickListener(this);
        btn_profile.setOnClickListener(this);
        btn_close_share_sheet.setOnClickListener(this);
    }

    public void getPosts() {
        if (Util.isNetworkAvailable(MainActivity.this)) {
            postLoadingIndicator.setVisibility(View.VISIBLE);
            int totalItem = feedsAdapter.getItemCount();
            if (swipeRefreshLayout.isRefreshing()) {
                lastPostId = "0";
            } else {
                if (totalItem > 0) {
                    lastPostId = feedsAdapter.getPostId(totalItem - 1);
                } else {
                    lastPostId = "0";
                }
            }
            GetAllPost allPost = new GetAllPost();
            allPost.setLastId(lastPostId);
            allPost.setUserId(SettingManager.getUserId(this));

            Call<String> call = new RestClient(Constants.BASE_URL,
                    MainActivity.this).get()
                    .getAllPosts(allPost);

            call.enqueue(new CallbackWithRetry<String>(call) {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    postLoadingIndicator.setVisibility(View.GONE);
                    Log.d(TAG, "raw " + response.raw().toString());
                    //Null pointer exception
                    if (response.isSuccessful() &&
                            response.body() != null &&
                            !response.body().isEmpty()) {

                        JSONArray jsonArray = null;
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            jsonArray = jsonObject.getJSONArray("posts");

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Gson gson = new Gson();

                        if (jsonArray != null) {
                            ArrayList<Post> allParesedPosts = new ArrayList<>();
                            List<Post> posts = Arrays.asList(gson.fromJson(jsonArray.toString(),
                                    Post[].class));

                            if (posts.size() > 0) {
                                allParesedPosts.addAll(posts);
                                lastPostId = posts.get(posts.size() - 1).get_id();
                                //lastPostId = lstPostId;
                                if (swipeRefreshLayout.isRefreshing()) {
                                    allPosts.clear();
                                    feedsAdapter.clearData();
                                    feedsAdapter.notifyDataSetChanged();
                                }

                                for (int i = 0; i < allParesedPosts.size(); i++) {
                                    if (!allPosts.contains(allParesedPosts.get(i))) {
                                        Post post= allParesedPosts.get(i);
                                        /*post.setPostVideoUrl("360p.m3u8");*/
                                        allPosts.add(post);
                                    }
                                }

                                if (lastPostId.equals("0")) {
                                    setupFeed();
                                } else {
                                    feedsAdapter.setData(allPosts);
                                    feedsAdapter.notifyDataSetChanged();
                                }
                            }

                        } else {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body());
                                String message = jsonObject.getString("message");
                                if (message != null && message.equals(getString(R.string.jwt_expired))) {
                                    Toast.makeText(MainActivity.this, "Token expired Please login again.", Toast.LENGTH_LONG).show();
                                    SettingManager.setUserEmail(MainActivity.this, "");

                                    Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }


//                loadMoreProgressBar.setVisibility(View.GONE);

                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }

                @Override
                public void onFailure(Call Call, Throwable t) {
                    super.onFailure(Call, t);
                    Log.d(TAG, "Reason Failure = " + t.getMessage());
                }

                @Override
                public void onFinallyFail() {
                    postLoadingIndicator.setVisibility(View.GONE);
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    Toast.makeText(MainActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Util.internetNotAvailableDialouge(MainActivity.this);
        }

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

    private void transparentStatusBar() {
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
        if (Util.isNetworkAvailable(MainActivity.this)) {

            SharePost sharePost = new SharePost();
            sharePost.setPostId(((Post) allPosts.get(feedPosition)).get_id());

            Call<String> call = new RestClient(Constants.BASE_URL, MainActivity.this).get()
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

    private void userDisplayPicture(String userEmail, String imageUrl) {

        final String spliteEmail[] = userEmail.split("@");
        SettingManager.setUserPictureURL(
                this,
                SettingManager.getUserFolderName(this) + "/" + spliteEmail[0] + ".jpeg");

        if (imageUrl != null
                && !imageUrl.isEmpty()
                && !imageUrl.equals("null")) {

            Glide.with(this)
                    .asBitmap()
                    .load(imageUrl)
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            e.printStackTrace();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap bitmap, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            File file = Util.storeBitmap(MainActivity.this, bitmap, Constants.FULL_PHOTO);
                            if (file != null && file.exists()) {
                                User currentUser = new User();
                                currentUser.set_id(SettingManager.getUserId(MainActivity.this));
                                currentUser.setEmailId(SettingManager.getUserEmail(MainActivity.this));
                                String imageFilePath = file.getAbsolutePath();

                                if (imageFilePath != null && !imageFilePath.isEmpty()) {
                                    Intent intentService = new Intent(MainActivity.this, UploadProfilePictureService.class);
                                    intentService.putExtra(Constants.CURRENT_USER, currentUser);
                                    intentService.putExtra(Constants.IMAGE_FILE_PATH, imageFilePath);
                                    startService(intentService);
                                }
                            }
                            return false;
                        }
                    }).into(250, 250);
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.img_create_video:

                actionCreateVideo();
                break;
            case R.id.img_profile:
                startActivity(new Intent(MainActivity.this, ProfileAcitvity.class)
                        .putExtra(Constants.USER_ID, SettingManager.getUserId(this)));
                overridePendingTransition(R.anim.enter, R.anim.exit);
                break;
            case R.id.btn_close_share_sheet:
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;
        }
    }

    private void actionCreateVideo() {
        if (!hasCameraWriteAudioRecordPermissions()) {
            requestWriteReadCameraAudioPermission(Constants
                    .RC_STORAGE_CAMERA_AUDIO_PERMISSION_GERNEAL);
        } else {
            startActivityForResult(new Intent(MainActivity.this,
                            VideoCreateActivity.class),
                    Constants.CREATE_VIDEO_ACTIVITY_REQUEST);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        }
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

        startActivity(new Intent(getActivity(),
                ProfileAcitvity.class)
                .putExtra(Constants.USER_ID, userId));
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    @Override
    public void onCommentBtn(int itemPosition, String postId, String postById, int totalComments) {
        this.feedPosition = itemPosition;
        startActivityForResult(new Intent(MainActivity.this, CommentsActivity.class)
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
        } else if (requestCode == Constants.CREATE_VIDEO_ACTIVITY_REQUEST) {
            if (resultCode == RESULT_OK) {
                // TODO: 11/10/2018 update posts after uploading video
                container.scrollToPosition(0);
            }
        }
    }

    @Override
    public void onAppClick(AppsInfo appsInfo) {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        shareVideoLink(videoLink, appsInfo);
    }


    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

        requestOrPerformAction(requestCode);

    }

    private void requestOrPerformAction(int requestCode) {
        switch (requestCode) {
            case Constants.RC_STORAGE_CAMERA_AUDIO_PERMISSION_GERNEAL:
                actionCreateVideo();
                break;
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        } else {
            requestOrPerformAction(requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    private boolean hasCameraWriteAudioRecordPermissions() {

        return EasyPermissions.hasPermissions(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
        );
    }

    private void requestWriteReadCameraAudioPermission(int requestCode) {

        EasyPermissions.requestPermissions(
                getActivity(),
                getString(R.string.msg_permission_please_give_us_storage_audio_camera_permission),
                requestCode, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO

        );
    }


    private boolean hasCameraPermission() {

        return EasyPermissions.hasPermissions(getActivity(),
                Manifest.permission.CAMERA);
    }

    private boolean hasWritePermission() {

        return EasyPermissions.hasPermissions(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private boolean hasAudioRecordPermission() {

        return EasyPermissions.hasPermissions(getActivity(),
                Manifest.permission.RECORD_AUDIO);
    }


    public Activity getActivity() {
        return MainActivity.this;
    }
}
