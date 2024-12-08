package com.photex.tiktok.services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.photex.tiktok.R;
import com.photex.tiktok.compressor.Compressor;
import com.photex.tiktok.models.User;
import com.photex.tiktok.models.restmodels.UpdateBackCover;
import com.photex.tiktok.models.restmodels.UpdateDisplayPicture;
import com.photex.tiktok.rest.CallbackWithRetry;
import com.photex.tiktok.rest.RestClient;
import com.photex.tiktok.setting.SettingManager;
import com.photex.tiktok.utils.CleanFoldersTask;
import com.photex.tiktok.utils.Constants;
import com.photex.tiktok.utils.NotificationHelper;
import com.photex.tiktok.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

public class UploadProfilePictureService extends Service {

    private static final String TAG = UploadProfilePictureService.class.getSimpleName();


    String imageFilePath = "";
    String profilePiVer = "";

    File actualFile = null;
    File shortPhotoFile = null;
    File fullPhotoFile = null;

    int qualityS = 100, quality = 90, widthHeightS = 180;

    boolean errorUploadingPicture = false;
    boolean isChangeBackGround = false;
    boolean isChangeProfilePicture = false;


    User currentUser;
    NotificationManager mNotificationManager;
    NotificationCompat.Builder mBuilder;
    int notificationTimeId = 0;
    String contextTitle;


    int mainObserverId = -1, shortObserverId = -1;

    public UploadProfilePictureService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initData(intent);
        return START_NOT_STICKY;

    }

    private void initData(Intent intent) {


        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (intent != null) {
            if (intent.hasExtra(Constants.IMAGE_FILE_PATH) &&
                    intent.hasExtra(Constants.CURRENT_USER)) {

                isChangeProfilePicture = true;
                imageFilePath = intent.getStringExtra(Constants.IMAGE_FILE_PATH);
                currentUser = (User) intent.getExtras().get(Constants.CURRENT_USER);
                if (imageFilePath != null && !imageFilePath.isEmpty() && currentUser != null) {
                    actualFile = new File(imageFilePath);
                    shortPhotoFile = new File(imageFilePath);
                    fullPhotoFile = new File(imageFilePath);
                }
                uploadProflePicture();
            }
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void uploadProflePicture() {
        if (isChangeProfilePicture) {
            contextTitle = "Profile Image";
        } else {
            contextTitle = "Back cover Image";
        }


        if (isChangeProfilePicture) {

            // TODO: 11/8/2018 compress profile photo is in todo list
            compressShortProfilePicture();
//            compresBigProfilePicture();
            uploadPictureToServer();

        }
        /*else if (isChangeBackGround) {

            compresBigProfilePicture();
            uploadBackCoverToServer();
        }*/
    }

    private void uploadPictureToServer() {

        final long currentTime = Calendar.getInstance(Locale.ENGLISH)
                .getTimeInMillis();
        notificationTimeId = (int) currentTime;

        /*Notification */
        NotificationHelper helper = new NotificationHelper(this);
        mBuilder = helper.getNotificationForPictureUpload(
                getString(R.string.profile_image),
                getString(R.string.uploading_profile_image),
                R.drawable.upload_btn_icon, notificationTimeId);
        /*Notification */

        final String spliteEmail[] = currentUser.getEmailId().split("@");


        if (Util.isNetworkAvailable(UploadProfilePictureService.this)) {

/*            if (spliteEmail[0] != null && !spliteEmail[0].isEmpty()
                    && shortPhotoFile != null && shortPhotoFile.length() <= 25000
                    && fullPhotoFile != null && fullPhotoFile.length() <= 150000) {*/
            if (spliteEmail[0] != null && !spliteEmail[0].isEmpty()
                    && fullPhotoFile != null && fullPhotoFile.length() <= 150000) {


                // make service in background
                startForeground(notificationTimeId, mBuilder.build());


                SettingManager.setProfileServiceRunning(
                        UploadProfilePictureService.this,
                        true);


                // TODO: 11/8/2018 change code for uploading profile full photo
                fullPhotoFile = shortPhotoFile = Util.renameFile(
                        Util.makeAndGetFolderName(this, Constants.FULL_PHOTO),
                        shortPhotoFile.getName(), spliteEmail[0] + ".jpeg");

                /*fullPhotoFile = Util.renameFile(
                        Util.makeAndGetFolderName(this, Constants.FULL_PHOTO),
                        fullPhotoFile.getName(), spliteEmail[0] + currentTime + "_full" + ".jpeg");*/

                if (shortPhotoFile != null && shortPhotoFile.exists() && fullPhotoFile != null && fullPhotoFile.exists()) {
                    RequestBody shortImageRequest = RequestBody.create(MediaType.parse("image"),
                            shortPhotoFile);
                    RequestBody fullImageRequest = RequestBody.create(MediaType.parse("image"),
                            fullPhotoFile);


                    MultipartBody.Part shortImagePart;
                    MultipartBody.Part fullImagePart;

                    try {
                        shortImagePart = MultipartBody.Part.createFormData("displayPicture",
                                shortPhotoFile.getName().replace(" ", "_"), shortImageRequest);
                        fullImagePart = MultipartBody.Part.createFormData("displayPicture",
                                fullPhotoFile.getName().replace(" ", "_"), fullImageRequest);
                    } catch (Exception e) {
                        e.printStackTrace();
                        shortImagePart = MultipartBody.Part.createFormData("displayPicture",
                                shortPhotoFile.getName(), shortImageRequest);
                        fullImagePart = MultipartBody.Part.createFormData("displayPicture",
                                fullPhotoFile.getName(), fullImageRequest);
                    }


                    Call<String> call = new RestClient(Constants.BASE_URL,
                            UploadProfilePictureService.this).get()
                            .updateDisplayPictureMultiPart(
                                    createMapForRequestDisplayPicture(spliteEmail[0], String.valueOf(currentTime)),
                                    fullImagePart,
                                    shortImagePart

                            );

                    call.enqueue(new CallbackWithRetry<String>(call) {

                        @Override
                        public void onResponse(Call<String> call,
                                               Response<String> response) {

                            JSONObject jsonObject = null;
                            boolean success = false;
                            try {
                                jsonObject = new JSONObject(response.body());
                                if (jsonObject.getBoolean("success")) {
                                    success = jsonObject.getBoolean("success");
                                    Log.i(TAG, "edit picture updated success");

                                } else {
                                    Log.i(TAG, " edit picture updated failed");
                                }
                                if (jsonObject.getString("displayPictureLastModified") != null)
                                    profilePiVer = jsonObject.getString("displayPictureLastModified");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (success) {

                                SettingManager.setIsProfilePictureUploaded(
                                        UploadProfilePictureService.this,
                                        true);

                                SettingManager.
                                        setIsProfilePictureChanged(
                                                UploadProfilePictureService.this,
                                                true);

                                SettingManager.setProfilePicTime(UploadProfilePictureService.this,
                                        String.valueOf(System.currentTimeMillis()));

                                if (!profilePiVer.isEmpty()) {
                                    SettingManager.setUserPictureVersion(
                                            UploadProfilePictureService.this
                                            , profilePiVer);

                                    Log.d(TAG, "Version " + profilePiVer);
                                } else {
                                    SettingManager.setUserPictureVersion(
                                            UploadProfilePictureService.this, System.currentTimeMillis() + "");

                                }
                                Intent intent = new Intent();
                                intent.setAction(Constants.EVENT_PHOTO_CHANGED);
                                intent.putExtra(Constants.UPDATED_PICTURE, SettingManager.
                                        getUserFolderName(UploadProfilePictureService.this)
                                        + "/" + spliteEmail[0] + ".jpeg");
                                intent.putExtra(Constants.UPDATED_PROFILE_PICTURE_FULL, SettingManager.
                                        getUserFolderName(UploadProfilePictureService.this)
                                        + "/" + spliteEmail[0] + currentTime + "_full" + ".jpeg");
                                sendLocalBroadCast(intent);

                                sendBroadCastNotificationStatus(success,
                                        contextTitle, contextTitle + getString(R.string.changed_successfully),
                                        notificationTimeId, isChangeProfilePicture);


                            } else {
                                //failed broadcast
                                sendBroadCastNotificationStatus(success,
                                        contextTitle, contextTitle + getString(R.string.upload_failed_please_retry),
                                        notificationTimeId, isChangeProfilePicture);
                            }
                            deleteUnnecessaryFiles();
                            SettingManager.setProfileServiceRunning(UploadProfilePictureService.this,
                                    false);
                            ReleaseResources(true);
                        }

                        @Override
                        public void onFinallyFail() {
                            sendBroadCastNotificationStatus(false,
                                    contextTitle, contextTitle + " " + getString(R.string.upload_failed_please_retry),
                                    notificationTimeId, isChangeProfilePicture);
                            deleteUnnecessaryFiles();
                            ReleaseResources(true);
                        }

                    });
                } else {
                    //failed broadcast
                    sendBroadCastNotificationStatus(false,
                            contextTitle, contextTitle + " " + getString(R.string.upload_failed_pleaes_retry),
                            notificationTimeId, isChangeProfilePicture);
                    deleteUnnecessaryFiles();
                    stopService();
                }


            } else {
                //failed broadcast
                sendBroadCastNotificationStatus(false,
                        contextTitle, contextTitle + " " + getString(R.string.upload_failed_pleaes_retry),
                        notificationTimeId, isChangeProfilePicture);
                deleteUnnecessaryFiles();
                stopService();
            }
        } else {
            //failed broadcast
            sendBroadCastNotificationStatus(false,
                    contextTitle, contextTitle + " " + getString(R.string.upload_failed_pleaes_retry),
                    notificationTimeId, isChangeProfilePicture);
            deleteUnnecessaryFiles();
            Toast.makeText(this, R.string.please_check_your_internet_connection,
                    Toast.LENGTH_SHORT).show();
            stopService();

        }

    }

    private void uploadBackCoverToServer() {

/*        final long currentTime = Calendar.getInstance(Locale.ENGLISH).getTimeInMillis();
        notificationTimeId = (int) currentTime;

        *//*Notification *//*
        NotificationHelper helper = new NotificationHelper(this);
        mBuilder = helper.getNotificationForPictureUpload(
                getString(R.string.back_cover_image),
                getString(R.string.uploading_back_cover_image),
                R.drawable.upload, notificationTimeId);
        *//*Notification *//*

        final String spliteEmail[] = currentUser.getEmailId().split("@");
        Log.d("fileSize", "full " + fullPhotoFile.length() + "");

        if (Utils.isNetworkAvailable(UploadProfilePictureService.this)) {
            if (spliteEmail[0] != null && !spliteEmail[0].isEmpty()
                    && fullPhotoFile.length() <= 150000) {
                SettingManager.setProfileServiceRunning(UploadProfilePictureService.this,
                        true);

                startForeground(notificationTimeId, mBuilder.build());


                fullPhotoFile = Utils.renameFile(
                        Utils.makeAndGetFolderName(this, Constants.FULL_PHOTO),
                        fullPhotoFile.getName(), spliteEmail[0] + currentTime + "_backCover" + ".jpeg");

                if (fullPhotoFile != null && fullPhotoFile.exists()) {
                    RequestBody fullImageRequest = RequestBody.create(MediaType.parse("image"),
                            fullPhotoFile);

                    MultipartBody.Part fullImagePart;

                    try {
                        fullImagePart = MultipartBody.Part.createFormData("backCoverDisplayPicture",
                                fullPhotoFile.getName().replace(" ", "_"), fullImageRequest);
                    } catch (Exception e) {
                        e.printStackTrace();
                        fullImagePart = MultipartBody.Part.createFormData("backCoverDisplayPicture",
                                fullPhotoFile.getName(), fullImageRequest);
                    }
                    Call<String> call = new RestClient(Constants.BASE_URL, UploadProfilePictureService.this).get()
                            .updateBackCoverMultiPart(
                                    createMapForRequestBackCover(spliteEmail[0],
                                            String.valueOf(currentTime)), fullImagePart);

                    call.enqueue(new CallbackWithRetry<String>(call) {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {

                            if (response != null && response.body() != null) {
                                JSONObject jsonObject = null;
                                boolean success = false;
                                try {
                                    jsonObject = new JSONObject(response.body());
                                    if (jsonObject.getBoolean("success")) {
                                        success = jsonObject.getBoolean("success");
                                        Log.i("edit", "backcover updated success");

                                    } else {
                                        Log.i("edit", "backcover updated failed");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (success) {
                                    deleteUnnecessaryFiles();

                                    Intent intent = new Intent();
                                    *//*intent.putExtra("updatedInfo", userProfile);*//*
                                    intent.setAction(Constants.EVENT_PHOTO_CHANGED);

                                    intent.putExtra(Constants.UPDATED_BACK_COVER,
                                            SettingManager.getUserFolderName(UploadProfilePictureService.this)
                                                    + "/" + spliteEmail[0] + currentTime + "_backCover" + ".jpeg");
                                    sendLocalBroadCast(intent);

                                    sendBroadCastNotificationStatus(true,
                                            contextTitle, getString(R.string.back_cover_image_changed),
                                            notificationTimeId, isChangeProfilePicture);

                                    ReleaseResources(true);
                                           *//* setResult(RESULT_OK, intent);
                                            progressBar.setVisibility(View.GONE);
                                            finish();*//*
                                }
                            } else {

                                sendBroadCastNotificationStatus(false,
                                        contextTitle, getString(R.string.back_cover_failed_please_try_again),
                                        notificationTimeId, isChangeProfilePicture);


                                ReleaseResources(true);
                                      *//*  progressBar.setVisibility(View.GONE);
                                        menuItemUse.setEnabled(true);*//*
                            }

                        }

                        @Override
                        public void onFailure(Call call, Throwable t) {

                            if (t.toString().contains("java.net.ConnectException: Failed to connect to")) {
                                sendBroadCastNotificationStatus(false,
                                        contextTitle, getString(R.string.back_cover_failed_please_try_again),
                                        notificationTimeId, isChangeProfilePicture);

                                ReleaseResources(true);

                            }

                                 *//*   progressBar.setVisibility(View.GONE);
                                    menuItemUse.setEnabled(true);
*//*
                        }

                        @Override
                        public void onFinallyFail() {
                            deleteUnnecessaryFiles();
                            sendBroadCastNotificationStatus(false,
                                    contextTitle, getString(R.string.back_cover_failed_please_try_again),
                                    notificationTimeId, isChangeProfilePicture);

                            Toast.makeText(UploadProfilePictureService.this, getString(R.string.please_check_your_internet_connection), Toast.LENGTH_SHORT).show();
                            ReleaseResources(true);
                        }
                    });

                }


            }
        } else {
            sendBroadCastNotificationStatus(false,
                    contextTitle, getString(R.string.back_cover_please_check_your_internet_connection),
                    notificationTimeId, isChangeProfilePicture);

            deleteUnnecessaryFiles();
            Toast.makeText(this, getString(R.string.please_check_your_internet_connection), Toast.LENGTH_SHORT).show();
            stopService();
        }*/
    }

    private void sendBroadCastNotificationStatus(
            boolean success, String contentTitle, String contentText,
            int notificationId, boolean isChangeProfilePicture) {

        Intent intent = new Intent();
        intent.setAction(Constants.ACTION_UPLOAD_PICTURE);
        intent.putExtra(Constants.SUCCESS, success);
        intent.putExtra(Constants.CONTENT_TITLE, contentTitle);
        intent.putExtra(Constants.CONTENT_TEXT, contentText);
        intent.putExtra(Constants.NOTIFICATION_ID, notificationId);
        sendBroadcast(intent);
    }

    private void sendLocalBroadCast(Intent intent) {
        LocalBroadcastManager
                .getInstance(UploadProfilePictureService.this)
                .sendBroadcast(intent);

    }

    private void ReleaseResources(boolean removeNotification) {
        stopForeground(removeNotification);
        stopService();
    }

    private void stopService() {
        SettingManager.setProfileServiceRunning(UploadProfilePictureService.this,
                false);

/*
        try {

            if (mainObserverId != -1)
                transferUtility.cancel(mainObserverId);
            if (isChangeProfilePicture && shortObserverId != -1)
                transferUtility.cancel(shortObserverId);
        } catch (Exception e) {
            e.printStackTrace();
        }
*/


        shortPhotoFile = null;
        fullPhotoFile = null;
        notificationTimeId = 0;
        stopSelf();
    }

    private void compresBigProfilePicture() {

/*        try {
            if (fullPhotoFile != null && fullPhotoFile.exists()) {
                if (fullPhotoFile.length() > 1000) {

                    long length = fullPhotoFile.length();
                    Log.d("size_image", "Post length" + length);
                    fullPhotoFile = getCompressedImageFileFull(qualityS);
                    Log.i("size_image", "Post " + fullPhotoFile.length());
                    if (fullPhotoFile.exists()) {

                        if (fullPhotoFile.length() > 150000) {
                            for (int i = 0; i < 5; i++) {
                                if (quality == 90) {
                                    quality = 80;
                                    if (compressAgainFull(quality)) {
                                        Log.d("fileSize", "full " + fullPhotoFile.length() + "");
                                        break;
                                    }

                                } else if (quality == 80) {
                                    quality = 70;
                                    if (compressAgainFull(quality)) {
                                        Log.d("fileSize", "full " + fullPhotoFile.length() + "");

                                        break;
                                    }
                                } else if (quality == 70) {
                                    quality = 60;
                                    if (compressAgainFull(quality)) {
                                        Log.d("fileSize", "full " + fullPhotoFile.length() + "");
                                        break;
                                    }
                                } else if (quality == 60) {
                                    quality = 50;
                                    if (compressAgainFull(quality)) {
                                        Log.d("fileSize", "full " + fullPhotoFile.length() + "");
                                        break;
                                    } else {

                                        sendBroadCastNotificationStatus(false,
                                                contextTitle, getString(R.string.please_select_another_pic),
                                                notificationTimeId, isChangeProfilePicture);

                                        Toast.makeText(UploadProfilePictureService.this,
                                                getString(R.string.please_select_another_pic),
                                                Toast.LENGTH_SHORT).show();
                                        stopService();
                                    }
                                }
                            }

                        }*//* else {
                            stopService();
                        }*//*
                    } else {
                        Toast.makeText(UploadProfilePictureService.this,
                                getString(R.string.please_select_another_pic_it_to_small),
                                Toast.LENGTH_SHORT).show();
                        stopService();
                    }
                } else {
                    sendBroadCastNotificationStatus(false,
                            contextTitle, getString(R.string.please_select_another_pic),
                            notificationTimeId, isChangeProfilePicture);
                    Toast.makeText(UploadProfilePictureService.this,
                            getString(R.string.please_select_another_pic_it_to_small),
                            Toast.LENGTH_SHORT).show();
                    stopService();

                }
            } else {
                sendBroadCastNotificationStatus(false,
                        contextTitle, getString(R.string.please_select_another_pic),
                        notificationTimeId, isChangeProfilePicture);

                Toast.makeText(UploadProfilePictureService.this,
                        getString(R.string.please_select_an_image),
                        Toast.LENGTH_SHORT).show();
                stopService();

            }
        } catch (Exception e) {
            sendBroadCastNotificationStatus(false,
                    contextTitle, getString(R.string.please_select_another_pic),
                    notificationTimeId, isChangeProfilePicture);

            Toast.makeText(UploadProfilePictureService.this,
                    getString(R.string.please_select_another_pic),
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            stopService();

        }*/

    }

    private void compressShortProfilePicture() {
/*        //File size is compressed
        if (shortPhotoFile.length() > 100) {

            ImageInfo info = ImageUtil.getHeightWidth(UploadProfilePictureService.this, shortPhotoFile);

            if (info.getSize() <= 25000) {

                if (info.getHeight() < 110 || info.getWidth() < 110) {

                    //failed broadcast
                    sendBroadCastNotificationStatus(false,
                            contextTitle, getString(R.string.please_select_another_pic_it_to_small),
                            notificationTimeId, isChangeProfilePicture);

                    Toast.makeText(UploadProfilePictureService.this,
                            getString(R.string.please_select_another_pic_it_to_small),
                            Toast.LENGTH_SHORT).show();
                    shortPhotoFile = null;
                    stopService();
                }

            } else {
                shortPhotoFile = getCompressedImageFile(qualityS, widthHeightS, widthHeightS, actualFile);
                Log.d("size_image", "Compressor " + shortPhotoFile.length() + "");

                if (shortPhotoFile.length() > 25000) {
                    for (int i = 0; i < 5; i++) {
                        if (widthHeightS >= 180) {
                            widthHeightS = 150;
                            if (compressAgainShort()) break;
                        } else if (widthHeightS >= 150 && widthHeightS < 180) {
                            widthHeightS = 140;
                            if (compressAgainShort()) break;
                        } else if (widthHeightS >= 140 && widthHeightS < 150) {
                            widthHeightS = 130;
                            if (compressAgainShort()) break;
                        } else if (widthHeightS >= 130 && widthHeightS < 140) {
                            widthHeightS = 120;
                            if (compressAgainShort()) break;
                        } else if (widthHeightS >= 120 && widthHeightS < 130) {
                            widthHeightS = 110;
                            if (compressAgainShort()) {
                                break;
                            } else {
                                shortPhotoFile = null;
                                sendBroadCastNotificationStatus(false,
                                        contextTitle, getString(R.string.please_select_another_pic),
                                        notificationTimeId, isChangeProfilePicture);

                                Toast.makeText(UploadProfilePictureService.this,
                                        getString(R.string.please_select_another_pic),
                                        Toast.LENGTH_SHORT).show();
                                stopService();
                            }
                        }
                    }

                }
            }

        } else {
            sendBroadCastNotificationStatus(false,
                    contextTitle, getString(R.string.please_select_another_pic),
                    notificationTimeId, isChangeProfilePicture);

            stopService();
        }*/
    }

    private File getCompressedImageFileFull(int quality) {
        return new Compressor.Builder(UploadProfilePictureService.this)
                .setQuality(quality)
                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                .setDestinationDirectoryPath(Util.makeAndGetFolderName(this, Constants.FULL_PHOTO))
                .build()
                .compressToFile(actualFile);
    }

    private File getCompressedImageFile(int quality, int width, int height, File file) {

        return new Compressor.Builder(UploadProfilePictureService.this)
                .setMaxWidth(width)
                .setMaxHeight(height)
                .setQuality(quality)
                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                .setDestinationDirectoryPath(Util.makeAndGetFolderName(this, Constants.SHORT_PHOTO))
                .build()
                .compressToFile(file);
    }

    private boolean compressAgainFull(int quality) {
       /* fullPhotoFile = getCompressedImageFileFull(quality);
        Log.d(TAG, "size_image Comressor again  " + fullPhotoFile.length() + "");

        if (!(fullPhotoFile.length() > 150000)) {
            return true;
        }*/
        return false;
    }

    private boolean compressAgainShort() {
        /*shortPhotoFile = getCompressedImageFile(qualityS, widthHeightS, widthHeightS, actualFile);
        if (!(shortPhotoFile.length() > 25000)) {
            Log.d(TAG, "size_image Comressor again " + shortPhotoFile.length() + "");
            return true;
        }*/
        return false;
    }

    private void deleteUnnecessaryFiles() {

        ArrayList<String> folders = new ArrayList<>();
//        folders.add(Utils.makeAndGetFolderName(this, Constants.SHORT_PHOTO));
        folders.add(Util.makeAndGetFolderName(this, Constants.FULL_PHOTO));
//        folders.add(Utils.makeAndGetFolderName(this, Constants.CROPPED_IMAGE));
//        folders.add(Utils.makeAndGetFolderName(this, Constants.CAMERA_PHOTO));

        CleanFoldersTask foldersTask = new CleanFoldersTask(folders);
        foldersTask.execute();
        try {
            if (fullPhotoFile.exists())
                fullPhotoFile.delete();

            /*if (CropActivity.bitmapp != null && !CropActivity.bitmapp.isRecycled()) {
                CropActivity.bitmapp.recycle();
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        ReleaseResources(true);
        SettingManager.setProfileServiceRunning(UploadProfilePictureService.this,
                false);
        super.onDestroy();
    }

    private HashMap<String, RequestBody> createMapForRequestDisplayPicture
            (String splitEmail,
             String currentTime) {

        /*Display picture model*/
        UpdateDisplayPicture displayPicture = new UpdateDisplayPicture();
        displayPicture.setUserId(currentUser.get_id());
        displayPicture.setDisplayPicture(SettingManager.
                getUserFolderName(UploadProfilePictureService.this)
                + "/" + splitEmail + ".jpeg");

        displayPicture.setFullDisplayPicture(
                SettingManager.getUserFolderName
                        (UploadProfilePictureService.this) + "/"
                        + splitEmail + currentTime + "_full" + ".jpeg");
        /*Display picture model*/

        HashMap<String, RequestBody> map = new HashMap<>();
        map.put("userId",
                createPartFromString(displayPicture.getUserId()));
        map.put("displayPicture",
                createPartFromString(displayPicture.getDisplayPicture()));
        map.put("fullDisplayPicture",
                createPartFromString(displayPicture.getFullDisplayPicture()));

        map.put("folderName", createPartFromString(SettingManager.getUserFolderName
                (UploadProfilePictureService.this)));

        return map;
    }


    private HashMap<String, RequestBody> createMapForRequestBackCover
            (String spliteEmail, String currentTime) {

        UpdateBackCover backCover = new UpdateBackCover();
        backCover.setUserId(currentUser.get_id());
        backCover.setBackCoverDisplayPicture(SettingManager.
                getUserFolderName(UploadProfilePictureService.this)
                + "/" + spliteEmail + currentTime + "_backCover" + ".jpeg");

        HashMap<String, RequestBody> map = new HashMap<>();
        map.put("userId",
                createPartFromString(backCover.getUserId()));
        map.put("backCoverDisplayPicture",
                createPartFromString(backCover.getBackCoverDisplayPicture()));
        map.put("folderName", createPartFromString(SettingManager.getUserFolderName
                (UploadProfilePictureService.this)));

        return map;
    }

    private RequestBody createPartFromString(String string) {
        return RequestBody.create(
                MultipartBody.FORM, string);
    }
}
