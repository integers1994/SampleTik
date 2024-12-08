package com.photex.tiktok.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.photex.tiktok.R;
import com.photex.tiktok.adapters.CategoryAdapter;
import com.photex.tiktok.models.CategoryInfo;
import com.photex.tiktok.models.MediaInfo;
import com.photex.tiktok.models.VideoMakerPostInfo;
import com.photex.tiktok.rest.CallbackWithRetry;
import com.photex.tiktok.rest.RestClient;
import com.photex.tiktok.services.UploadVideoService;
import com.photex.tiktok.setting.SettingManager;
import com.photex.tiktok.utils.CommonUtils;
import com.photex.tiktok.utils.Constants;
import com.photex.tiktok.utils.Util;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class VideoUploadActivity extends AppCompatActivity implements
        View.OnClickListener,
        AdapterView.OnItemSelectedListener {

    private ImageView  ivThumbNail;
    private EditText etCaption;
    private Spinner categorySpinner;
    private Button btn_upload_post;

    private MediaInfo mediaInfo;
    private Bitmap thumbnailBitmap;
    private String Tag = "VideoUploadActivity";
    private String videoCaption;

    private CategoryAdapter categoryAdapter;
    private ArrayList<CategoryInfo> categoryInfoList;
    private AVLoadingIndicatorView loadingIndicator;
    private int selectedCategory = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_upload);

        initView();
        initializeData();
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
/*        if (toolbar != null) {
            toolbar.setTitle("Post");
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null && toolbar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Upload");
        }*/

        etCaption = findViewById(R.id.et_caption);
        ivThumbNail = findViewById(R.id.iv_video_thumbnail);
        categorySpinner = findViewById(R.id.category_spinner);
        loadingIndicator = findViewById(R.id.loading_indicator);
        btn_upload_post = findViewById(R.id.btn_upload_post);

      /*  uploadBtn = findViewById(R.id.upload_btn);
        uploadBtn.setOnClickListener(this);*/
    }

    private void initializeData() {
        Intent intent = getIntent();
        if (intent != null &&
                intent.hasExtra(Constants.EXRTA_MEDIA_INFO)) {
            mediaInfo = (MediaInfo) getIntent().getExtras().get(Constants.EXRTA_MEDIA_INFO);

            thumbnailBitmap = ThumbnailUtils.createVideoThumbnail(mediaInfo.getInputVideos().get(0).getPath(),
                    MediaStore.Images.Thumbnails.MINI_KIND);
            ivThumbNail.setImageBitmap(thumbnailBitmap);

            categoryInfoList = new ArrayList<>();
            CategoryInfo categoryInfo = new CategoryInfo();
            categoryInfo.setName("Select video category");
            categoryInfoList.add(categoryInfo);

            CategoryInfo categoryInfoSecond = new CategoryInfo();
            categoryInfoSecond.setName("Gernal");
            //categoryInfoList.add(categoryInfoSecond);

            categoryAdapter = new CategoryAdapter(this, categoryInfoList);
            categorySpinner.setAdapter(categoryAdapter);
            categorySpinner.setOnItemSelectedListener(this);

            getAllCategories();
        }
    }

    private void getAllCategories() {
        if (Util.isNetworkAvailable(this)) {
            loadingIndicator.setVisibility(View.VISIBLE);

            Call<String> call = new RestClient(Constants.BASE_URL,
                    VideoUploadActivity.this).get().getCategories();
            call.enqueue(new CallbackWithRetry<String>(call) {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    loadingIndicator.setVisibility(View.GONE);
                    JSONArray jsonArray = null;
                    boolean success = false;
                    if (response.body() != null && response.isSuccessful()) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            success = jsonObject.getBoolean("success");
                            jsonArray = jsonObject.getJSONArray("categories");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (success && jsonArray != null) {
                            Gson gson = new Gson();
                            List<CategoryInfo> serverCategories;
                            serverCategories = Arrays.asList(gson.fromJson(jsonArray.toString(), CategoryInfo[].class));

                            if (serverCategories.size() > 0) {
                                categoryInfoList.addAll(serverCategories);
                                categoryAdapter.setData(categoryInfoList);
                                categoryAdapter.notifyDataSetChanged();
                            }

                        } else {
                            Toast.makeText(VideoUploadActivity.this, "Some thing goes wrong please try again!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFinallyFail() {
                    Log.e("getComments", "onFinallyFail");
                    Toast.makeText(VideoUploadActivity.this, "Some thing goes wrong please try again!", Toast.LENGTH_SHORT).show();
                    loadingIndicator.setVisibility(View.GONE);
                }
            });

        } else {
            Util.internetNotAvailableDialouge(this);
        }
    }

    private boolean isValidate() {
        videoCaption = etCaption.getText().toString().trim();
        if (videoCaption.isEmpty()) {
            Toast.makeText(VideoUploadActivity.this,
                    "Please Add Title/Description First!",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (videoCaption.length() > 100) {
            Toast.makeText(VideoUploadActivity.this,
                    "Title/Description is more than 100 Characters",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (selectedCategory == 0) {
            Toast.makeText(VideoUploadActivity.this, "Please Select Video Category First!", Toast.LENGTH_SHORT).show();
            return false;
        }

        selectedCategory--;
//        EXRTA_MEDIA_INFO.getAudioInfo().setCatId(1+""/*categoryInfoList.get(selectedCategory).get_id()*/);

     /*   if (EXRTA_MEDIA_INFO.getAudioInfo() != null)
            EXRTA_MEDIA_INFO.getAudioInfo().setCatId(categoryInfoList.get(selectedCategory).get_id());*/


        return true;
    }

    private void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etCaption.getWindowToken(), 0);
    }

    public void upLoadDialog(final Context context) {

        AlertDialog.Builder alertupload = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
        String message = "Do you want to Upload this Video ?" +
                "\n\nIt takes some time to process, processing will done in background you can check progress in Notification!.";

        alertupload.setTitle(R.string.app_name);
        alertupload.setIcon(R.mipmap.ic_launcher);
        alertupload.setCancelable(true);
        alertupload.setMessage(message);

        alertupload.setPositiveButton("Yes", (arg0, arg1) -> {
            if (Util.isNetworkAvailable(VideoUploadActivity.this)) {
                startVideoUploading();
            } else {
                Util.internetNotAvailableDialouge(VideoUploadActivity.this);
            }
        });
        alertupload.setNegativeButton("No", (dialog, which) ->
                dialog.dismiss());
        alertupload.show();

    }

    private void startVideoUploading() {
        VideoMakerPostInfo postInfo = getPostInfo();

        // upload video in background
        if (!CommonUtils.isMyServiceRunning(getActivity(), UploadVideoService.class)) {
            Intent serviceIntent = new Intent(getActivity(),
                    UploadVideoService.class);
            UploadVideoService.IS_SERVICE_RUNNING = true;
            serviceIntent.putExtra(Constants.EXTRA_POST_INFO, postInfo);
            serviceIntent.putExtra(Constants.EXRTA_MEDIA_INFO, mediaInfo);
            serviceIntent.setAction(Constants.START_UPLOAD_SERVICE);

            startService(serviceIntent);
            Log.w(Tag, "service started");
            setResult(RESULT_OK);
            finish();

        } else {
            Log.w(Tag, "service already running");
            Toast.makeText(getApplicationContext(), "Last Video is still uploading...\nPlease try again later!", Toast.LENGTH_LONG).show();
        }
    }


    public Activity getActivity() {
        return VideoUploadActivity.this;
    }

    private VideoMakerPostInfo getPostInfo() {
        String thumbnailHeight = "512";
        String thumbnailWidth = "512";

        if (thumbnailBitmap != null) {
            thumbnailWidth = String.valueOf(thumbnailBitmap.getWidth());
            thumbnailHeight = String.valueOf(thumbnailBitmap.getHeight());
        }

        String fullName = SettingManager.getUserFullName(this);
        String userName = SettingManager.getUserName(this);
        String email = SettingManager.getUserEmail(this);
        String id = SettingManager.getUserId(this);

        String folderName = SettingManager.getUserFolderName(this);
        String userImage = "";
        String[] spliteEmail = null;
        try {
            spliteEmail = email.split("@");
            userImage = folderName + "/" + spliteEmail[0] + ".jpeg";
        } catch (Exception e) {
            userImage = SettingManager.getUserPictureURL(this);
        }


        VideoMakerPostInfo postInfo = new VideoMakerPostInfo();
        postInfo.setUserId(id);
        postInfo.setFullName(fullName);
        postInfo.setUserName(userName);
        postInfo.setLocation("NA");
        postInfo.setTags("NA");
        postInfo.setUserDisplayPicture(userImage);

        postInfo.setPostImageUrl(folderName);
        postInfo.setPostVideoUrl(folderName);
        postInfo.setCaption(videoCaption);
        postInfo.setHeight(thumbnailHeight);
        postInfo.setWidth(thumbnailWidth);
        postInfo.setVideoFilePath(null);
        postInfo.setVideoDuration(String.valueOf(mediaInfo.getDuration()));
        postInfo.setLocalPath("");
        postInfo.setThumbFilePath(createthumb(thumbnailBitmap));

        return postInfo;
    }

    public File createthumb(Bitmap bm) {

        bm = Bitmap.createScaledBitmap(bm, 640, 480, false);

        File thumbFile = null;

        File fileDirectory = new File(Environment.getExternalStorageDirectory(), Constants.TEMP_IMAGE_FOLDER);
        if (!fileDirectory.exists()) {
            fileDirectory.mkdirs();
        }

        thumbFile = new File(fileDirectory, Util.getSortedStringDate() + "thumb.jpeg");
        FileOutputStream ostream1 = null;
        try {
            ostream1 = new FileOutputStream(thumbFile);
            bm.compress(Bitmap.CompressFormat.JPEG, 35, ostream1);
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }
        try {
            ostream1.flush();
            ostream1.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return thumbFile;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_upload_post:
                hideKeyBoard();
                //todo Remove this sick
                selectedCategory = 1;
                mediaInfo.getAudioInfo().setCatId("5c1a339547743113e20d8bdb");
                upLoadDialog(this);
                break;
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedCategory = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.e(Tag, "onNothingSelected");
    }
}
