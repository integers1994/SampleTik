package com.photex.tiktok.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.photex.tiktok.R;
import com.photex.tiktok.cgcrop.CropActivity;
import com.photex.tiktok.models.User;
import com.photex.tiktok.models.restmodels.UserBasicInfo;
import com.photex.tiktok.rest.CallbackWithRetry;
import com.photex.tiktok.rest.RestClient;
import com.photex.tiktok.setting.SettingManager;
import com.photex.tiktok.utils.CommonUtils;
import com.photex.tiktok.utils.Constants;
import com.photex.tiktok.utils.FileUtils;
import com.photex.tiktok.utils.RealPathUtil;
import com.photex.tiktok.utils.Util;
import com.wang.avi.AVLoadingIndicatorView;
import com.yalantis.ucrop.UCrop;

import net.alhazmy13.gota.Gota;
import net.alhazmy13.gota.GotaResponse;

import org.json.JSONObject;

import java.io.File;

import retrofit2.Call;
import retrofit2.Response;

public class ProfileUpdateAcitvity extends AppCompatActivity implements
        View.OnClickListener, TextWatcher, Gota.OnRequestPermissionsBack {

    private ImageView ivUserProfilePic, ivEditProfilePic, closeBtn, updateProfileBtn;
    private TextView tvProfileTitle, tvUserName;
    private EditText etUserFullName, etUserBio;
    private AVLoadingIndicatorView loadingIndicator;

    private LinearLayout editProfileBottomSheet;
    private RelativeLayout bottomLayout;
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    private boolean isBottomSheetVisible;
    private TextView cameraBtn, galleryBtn;

    private User userInfo;
    private String userFullName;
    private String userBio;
    private Uri fileUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_update);
        initializeView();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Constants.CURRENT_USER)) {
            userInfo = (User) intent.getExtras().get(Constants.CURRENT_USER);

            Glide.with(this)
                    .load(Constants.BASE_URL + Constants.SERVER_DIRECTORY + userInfo.getDisplayPicture())
                    .apply(new RequestOptions()
                            .signature(new ObjectKey(SettingManager.getProfilePicTime(this)))
                            .placeholder(R.drawable.user_image_place_holder)
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                    .into(ivUserProfilePic);

            tvProfileTitle.setText(userInfo.getFullName());
            tvUserName.setText("@" + userInfo.getUserName());
            etUserFullName.setText(userInfo.getFullName());
            etUserBio.setText(userInfo.getBio());
            updateSaveButton(false);
        }

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int state) {
                if (state == BottomSheetBehavior.STATE_COLLAPSED) {
                    hideBottomSheet();
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

    }

    private void updateSaveButton(boolean isEnabled) {
        if (isEnabled) {
            updateProfileBtn.setImageResource(R.drawable.save_profile_icon);
            updateProfileBtn.setEnabled(true);
        } else {
            updateProfileBtn.setImageResource(R.drawable.disabled_save_profile_icon);
            updateProfileBtn.setEnabled(false);
        }

    }

    private void initializeView() {
        cameraBtn = findViewById(R.id.cameraBtn);
        galleryBtn = findViewById(R.id.galleryBtn);

        editProfileBottomSheet = findViewById(R.id.edit_profile_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(editProfileBottomSheet);
        bottomLayout = findViewById(R.id.bottom_layout);

        ivUserProfilePic = findViewById(R.id.iv_user_profile_pic);
        ivEditProfilePic = findViewById(R.id.iv_edit_profile_pic);
        tvProfileTitle = findViewById(R.id.tvProfileTitle);
        tvUserName = findViewById(R.id.tv_user_name);
        etUserFullName = findViewById(R.id.et_user_full_name);
        etUserBio = findViewById(R.id.et_user_bio);
        loadingIndicator = findViewById(R.id.profile_update_loading_indicator);

        closeBtn = findViewById(R.id.close_profile_btn);
        updateProfileBtn = findViewById(R.id.update_profile_btn);

        closeBtn.setOnClickListener(this);
        updateProfileBtn.setOnClickListener(this);
        ivEditProfilePic.setOnClickListener(this);
        cameraBtn.setOnClickListener(this);
        galleryBtn.setOnClickListener(this);

        etUserFullName.addTextChangedListener(this);
        etUserBio.addTextChangedListener(this);
    }

    private void editProfilePicture(ImageView imageView) {
      /*  final boolean isAlreadyUploading =
                SettingManager.getIsProfilePictureChanged(this)
                        && Util.isMyServiceRunning(this,
                        UploadProfilePictureService.class);

        *//*Create dialoge for profile picture options*//*
        final Dialog dialog = new Dialog(this);
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.START;
            wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);
        }
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_profile_photo);
        TextView txtPhotoCamera = dialog.findViewById(R.id.txtPhotoCamera);
        TextView txtPhotoGallery = dialog.findViewById(R.id.txtPhotoGallery);
        TextView txtViewPhoto = dialog.findViewById(R.id.txtViewPhoto);*/

//        /*Show options if it have uploading *//*
//        if (currentUser.getFullDisplayPicture() != null
//                && !currentUser.getFullDisplayPicture().isEmpty()) {
//            txtViewPhoto.setVisibility(View.VISIBLE);
//        } else {
//            txtViewPhoto.setVisibility(View.GONE);
//        }*/
       /* txtPhotoCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Util.checkCameraHardware(ProfileUpdateAcitvity.this)) {

                    if (EasyPermissions.hasPermissions(ProfileUpdateAcitvity.this,
                            Constants.PARM_WRITE_EXTERNAL_STORAGE)) {

                        if (!isAlreadyUploading) {
                            captureImage();
                        } else {
                            Toast.makeText(ProfileUpdateAcitvity.this,
                                    "Please wait or cancel from notification," +
                                            "uploading is already running",
                                    Toast.LENGTH_SHORT).show();

                        }
                    } else {

                        EasyPermissions.requestPermissions(UserProfileActivity.this,
                                getString(R.string.msg_write_permission),
                                Constants.RC_SETTINGS_WRITE_STORAGE_DOWNLOAD,
                                Constants.PARM_WRITE_EXTERNAL_STORAGE);

                    }
                } else {
                    Toast.makeText(ProfileUpdateAcitvity.this, "Not have Camera",
                            Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
        txtPhotoGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (EasyPermissions.hasPermissions(ProfileUpdateAcitvity.this,
                        Constants.PARM_WRITE_EXTERNAL_STORAGE)) {
                    if (!isAlreadyUploading) {
                        Intent intent = new Intent(
                                Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, GALLARY_CAPTURE_REQUEST_CODE);
                    } else {
                        Toast.makeText(ProfileUpdateAcitvity.this,
                                "Please wait or cancel uploading from notification," +
                                        "uploading is already running",
                                Toast.LENGTH_SHORT).show();

                    }
                } else {
                    EasyPermissions.requestPermissions(ProfileUpdateAcitvity.this,
                            getString(R.string.msg_write_permission),
                            Constants.RC_SETTINGS_WRITE_STORAGE_DOWNLOAD,
                            Constants.PARM_WRITE_EXTERNAL_STORAGE);
                }
                dialog.dismiss();
            }
        });
        txtViewPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                intentFullPhoto(imageView);
                dialog.dismiss();
            }
        });*/
//        dialog.show();
    }

    private void updateProfile() {
        if (Util.isNetworkAvailable(this)) {
            loadingIndicator.setVisibility(View.VISIBLE);
            updateSaveButton(false);


            UserBasicInfo basicInfo = new UserBasicInfo();
            basicInfo.setUserId(userInfo.get_id());
            basicInfo.setFullName(userFullName);
            basicInfo.setBio(userBio);

            basicInfo.setGenderPrivate(userInfo.isGenderPrivate());
            basicInfo.setDobPrivate(userInfo.isDobPrivate());
            basicInfo.setGender("");
            basicInfo.setDob("");
            basicInfo.setReligiousViews("");
            basicInfo.setPoliticalViews("");
            basicInfo.setLanguages("");

/*                        basicInfo.setGender(rdMale.isChecked() ? "Male" : "Female");

                        basicInfo.setDob(txtdateOfBirth.getText() != null
                    && !txtdateOfBirth.getText().toString().isEmpty() ?
                    txtdateOfBirth.getText().toString().trim() : "");

            basicInfo.setReligiousViews(txtReligiousViews.getText() != null
                    && !txtReligiousViews.getText().toString().isEmpty() ?
                    txtReligiousViews.getText().toString().trim() : "");

            basicInfo.setPoliticalViews(txtPoliticalViews.getText() != null
                    && !txtPoliticalViews.getText().toString().isEmpty() ?
                    txtPoliticalViews.getText().toString().trim() : "");

            basicInfo.setLanguages(txtLanguages.getText() != null
                    && !txtLanguages.getText().toString().isEmpty() ?
                    txtLanguages.getText().toString().trim() : "");*/


            Call<String> call = new RestClient(Constants.BASE_URL, ProfileUpdateAcitvity.this).get()
                    .updateBasicInfo(basicInfo);

            call.enqueue(new CallbackWithRetry<String>(call) {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    loadingIndicator.setVisibility(View.GONE);
                    updateSaveButton(true);

                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());

                            if (jsonObject.getBoolean("success")) {
                                SettingManager.setUserFullName(ProfileUpdateAcitvity.this, basicInfo.getFullName());
                                MainActivity.isDataChanged = true;
                                finish();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(ProfileUpdateAcitvity.this, "Some thing goes wrong please try again!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFinallyFail() {
                    loadingIndicator.setVisibility(View.GONE);
                    updateSaveButton(true);
                }
            });

        } else {
            Util.internetNotAvailableDialouge(this);
        }
    }

    private boolean isDataValid() {
        userFullName = etUserFullName.getText().toString().trim();
        userBio = etUserBio.getText().toString().trim();

        if (userFullName.isEmpty()) {
            Toast.makeText(this, "User Name is empty!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (userFullName.length() < 3) {
            Toast.makeText(this, "User Name length can not be less than 3", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void showBottomSheet() {
        isBottomSheetVisible = true;
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomLayout.setBackgroundColor(getResources().getColor(R.color.lightGray1));
        etUserFullName.setEnabled(false);
        etUserBio.setEnabled(false);

    }

    private void hideBottomSheet() {
        isBottomSheetVisible = false;
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        bottomLayout.setBackgroundColor(Color.TRANSPARENT);
        etUserFullName.setEnabled(true);
        etUserBio.setEnabled(true);
    }

    private void captureImage() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            fileUri = Util.getOutputMediaFileUri(1);

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                File file = new File(fileUri.getPath());
                Uri photoUri = FileProvider.getUriForFile(ProfileUpdateAcitvity.this,
                        "com.photex.tiktok.fileprovider", file);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            } else {
                File file = new File(fileUri.getPath());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));

            }

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, Constants.RC_CAMERA_CAPTURE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestPermittions(int requestId) {
        new Gota.Builder(this)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .requestId(requestId)
                .setListener(this)
                .check();
    }

    @Override
    public void onBackPressed() {
        if (isBottomSheetVisible) {
            hideBottomSheet();
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == closeBtn) {
            onBackPressed();

        } else if (v == updateProfileBtn) {
            Util.hideKeyboard(this);
            if (isDataValid()) {
                updateProfile();
            }

        } else if (v == ivEditProfilePic) {
            showBottomSheet();

        } else if (v == cameraBtn) {
            hideBottomSheet();
            requestPermittions(Constants.CAMERA_REQUEST);

        } else if (v == galleryBtn) {
            hideBottomSheet();
            requestPermittions(Constants.GALLERY_REQUEST);

        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        updateSaveButton(true);
    }

    @Override
    public void onRequestBack(int requestId, @NonNull GotaResponse gotaResponse) {
        if (requestId == Constants.CAMERA_REQUEST) {
            if (gotaResponse.isAllGranted()) {
                captureImage();
            } else {
                hideBottomSheet();
            }
        } else if (requestId == Constants.GALLERY_REQUEST) {
            if (gotaResponse.isAllGranted()) {
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, Constants.GALLARY_CAPTURE_REQUEST_CODE);
            } else {
                hideBottomSheet();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.RC_CAMERA_CAPTURE) {
            if (resultCode == RESULT_OK) {
               /* if (fileUri != null) {
                    Intent intent = new Intent(ProfileUpdateAcitvity.this, CropActivity.class);
                    intent.putExtra("image_path", fileUri.getPath());
                    intent.putExtra("fromProfile", "fromProfileProfilePic");
                    startActivityForResult(intent, Constants.CROP_PICTURE_REQUEST_CODE);
                }*/

                UCrop.of(fileUri, Uri.fromFile(FileUtils.getCropImagePath(getActivity())))
                        .withAspectRatio(1, 1)
                        .start(getActivity(), Constants.CROP_PICTURE_REQUEST_CODE);
            } else if (resultCode == RESULT_CANCELED) {

            } else {
                Toast.makeText(this, "failed to Capture Image", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == Constants.GALLARY_CAPTURE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    Uri uri = data.getData();
                    String path = "";
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        path = Util.getRealPathFromURI_API19(ProfileUpdateAcitvity.this, uri);
                    } else {
                        path = Util.getPathFromURI(uri, ProfileUpdateAcitvity.this);
                    }
                    if (path != null && uri != null) {
                        Intent intent = new Intent(ProfileUpdateAcitvity.this, CropActivity.class);
                        intent.putExtra("image_path", path);
                        intent.putExtra("fromProfile", "fromProfileProfilePic");
                        startActivityForResult(intent, Constants.CROP_PICTURE_REQUEST_CODE);
                    } else {
                        Toast.makeText(this, "image selection fail", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "image selection fail", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (/*CropActivity.bitmapp != null &&*/ requestCode == Constants.CROP_PICTURE_REQUEST_CODE
        ) {

            if (resultCode == RESULT_OK) {
                try {
                    if (userInfo != null) {
                        Uri uri = UCrop.getOutput(data);

                        File file = new File(RealPathUtil.getPathFromUri(getActivity(), uri));
                        if (file.exists()) {
                            Intent intent = new Intent(ProfileUpdateAcitvity.this, ProfilePictureUploadActivity.class);
                            intent.putExtra(Constants.IMAGE_FILE_PATH, file.getAbsolutePath());
                            intent.putExtra(Constants.CURRENT_USER, userInfo);
                            startActivityForResult(intent, Constants.UPDATE_PROFILE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if(resultCode == UCrop.RESULT_ERROR)
            {
                CommonUtils.showToast(getActivity(),"Some thing goes wrong");
            }

        } else if (requestCode == Constants.UPDATE_PROFILE) {
            if (resultCode == RESULT_OK && MainActivity.isDataChanged) {
                Handler handler = new Handler();
                handler.postDelayed(() -> Glide.with(ProfileUpdateAcitvity.this)
                        .load(Constants.BASE_URL +
                                Constants.SERVER_DIRECTORY +
                                userInfo.getDisplayPicture())
                        .apply(new RequestOptions()
                                .signature(new ObjectKey(SettingManager.getProfilePicTime(this)))
                                .placeholder(R.drawable.user_image_place_holder)
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                        .into(ivUserProfilePic), 2000);

            }
        }
    }

    private Activity getActivity() {
        return ProfileUpdateAcitvity.this;
    }
}
