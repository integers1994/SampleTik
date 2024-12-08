package com.photex.tiktok.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.photex.tiktok.R;
import com.photex.tiktok.cgcrop.CropActivity;
import com.photex.tiktok.models.User;
import com.photex.tiktok.services.UploadProfilePictureService;
import com.photex.tiktok.setting.SettingManager;
import com.photex.tiktok.utils.Constants;

import java.io.File;

public class ProfilePictureUploadActivity extends AppCompatActivity {


    Toolbar toolbar;
    ActionBar actionBar;
    ImageView ivUserProfilePhoto;
    ProgressBar progressBar;
    Intent intent;
    String imageFilePath = "";
    File actualFile = null;
    User currentUser;
    MenuItem menuItemUse;
    boolean isChangeBackGround = false;
    boolean isChangeProfilePicture = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_picture_upload);
        initData();
        initViews();

    }

    private void initData() {
        intent = getIntent();
        if (intent != null && intent.hasExtra(Constants.UPLOAD_BACK_COVER)) {
            currentUser = (User) intent.getExtras().get(Constants.CURRENT_USER);
            imageFilePath = intent.getStringExtra(Constants.IMAGE_FILE_PATH);
            actualFile = new File(imageFilePath);
            isChangeBackGround = true;
        } else if (intent != null &&
                intent.hasExtra(Constants.IMAGE_FILE_PATH) &&
                !intent.hasExtra(Constants.UPLOAD_BACK_COVER)) {

            isChangeProfilePicture = true;
            imageFilePath = intent.getStringExtra(Constants.IMAGE_FILE_PATH);
            currentUser = (User) intent.getExtras().get(Constants.CURRENT_USER);
            if (imageFilePath != null && !imageFilePath.isEmpty() && currentUser != null) {
                actualFile = new File(imageFilePath);
            }
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.progressBar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.profile_photo);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ivUserProfilePhoto = findViewById(R.id.ivUserProfilePhoto);
        if (actualFile != null && !actualFile.getAbsolutePath().isEmpty()) {
            ivUserProfilePhoto.setImageURI(Uri.fromFile(actualFile));
        }
        Log.d("profileVersion", SettingManager.getUserPictureVersion(this));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_profile_photo, menu);
        menuItemUse = menu.findItem(R.id.action_upload);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                deleteUnnecessaryFiles();
                finish();
                break;
            case R.id.action_upload:
                progressBar.setVisibility(View.VISIBLE);
                Intent intentService = new Intent(ProfilePictureUploadActivity.this, UploadProfilePictureService.class);
                intentService.putExtra(Constants.CURRENT_USER, currentUser);
                intentService.putExtra(Constants.IMAGE_FILE_PATH, imageFilePath);
                startService(intentService);
                MainActivity.isDataChanged = true;
                setResult(RESULT_OK);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        deleteUnnecessaryFiles();
        super.onBackPressed();
    }

    private void deleteUnnecessaryFiles() {

        try {
            if (actualFile.exists())
                actualFile.delete();

            if (CropActivity.bitmapp != null && !CropActivity.bitmapp.isRecycled()) {
                CropActivity.bitmapp.recycle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
