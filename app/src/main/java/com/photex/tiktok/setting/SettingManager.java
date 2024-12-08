package com.photex.tiktok.setting;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class SettingManager implements ISettingConst {
    public static final String TAG = SettingManager.class.getSimpleName();
    public static final String SHARED_PREFS = "com.photex.tiktok(v1.0.0)";

    private static void saveSetting(Context mContext, String mKey, String mValue) {

        SharedPreferences mSharedPreferences = mContext.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(mKey, mValue);
        editor.apply();
    }

    private static void saveSettingBoolean(Context mContext, String mKey, boolean mValue) {

        SharedPreferences mSharedPreferences = mContext.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(mKey, mValue);
        editor.apply();
    }

    private static boolean getSettingBoolean(Context mContext, String mKey, boolean mDefValue) {

        SharedPreferences mSharedPreferences = mContext.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        return mSharedPreferences.getBoolean(mKey, mDefValue);
    }

    private static void saveArrayListString(Context mContext, String mKey, HashSet mValue) {
        SharedPreferences mSharedPreferences = mContext.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putStringSet(mKey, mValue);
        editor.apply();
    }

    private static Set getArrayListString(Context mContext, String mKey, HashSet mDefValue) {

        SharedPreferences mSharedPreferences = mContext.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        return mSharedPreferences.getStringSet(mKey, mDefValue);
    }


    private static int getIntSetting(Context mContext, String mKey, int mDefValue) {

        SharedPreferences mSharedPreferences = mContext.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        return mSharedPreferences.getInt(mKey, mDefValue);
    }

    public static void saveSettingPackageSet(Context mContext, Set<String> mValue) {
        SharedPreferences mSharedPreferences = mContext.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putStringSet(ISettingConst.KEY_USERS_INSTALL_APP_PACKAGE, mValue);
        editor.apply();
    }

    public static Set<String> getInstallPackage(Context mContext, Set<String> defValues) {
        SharedPreferences mSharedPreferences = mContext.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        return mSharedPreferences.getStringSet(ISettingConst.KEY_USERS_INSTALL_APP_PACKAGE, defValues);
    }

    public static void saveSendRequest(Context mContext, boolean mValue) {
        SharedPreferences mSharedPreferences = mContext.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(ISettingConst.KEY_SEND_REQEST, mValue);
        editor.apply();
    }

    public static boolean getSendRequest(Context mContext, boolean defValues) {
        SharedPreferences mSharedPreferences = mContext.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        return mSharedPreferences.getBoolean(ISettingConst.KEY_SEND_REQEST, defValues);
    }


    private static String getSetting(Context mContext, String mKey, String mDefValue) {

        SharedPreferences mSharedPreferences = mContext.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        return mSharedPreferences.getString(mKey, mDefValue);
    }

    public static String getUserName(Context mContext) {

        return getSetting(mContext, KEY_USER_NAME, "");
    }

    public static void setUserName(Context mContext, String mValue) {
        saveSetting(mContext, KEY_USER_NAME, mValue);
    }

    public static String getProfilePicTime(Context mContext) {

        return getSetting(mContext, KEY_PROFILE_PIC_TIME, "");
    }

    public static void setProfilePicTime(Context mContext, String mValue) {
        saveSetting(mContext, KEY_PROFILE_PIC_TIME, mValue);
    }


    private static void saveIntSetting(Context mContext, String mKey, int mValue) {
        SharedPreferences mSharedPreferences = mContext.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(mKey, mValue);
        editor.apply();
    }

    private static long getLongSetting(Context mContext, String mKey, long mDefValue) {

        SharedPreferences mSharedPreferences = mContext.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        return mSharedPreferences.getLong(mKey, mDefValue);
    }

    private static void saveLongSetting(Context mContext, String mKey, long mValue) {
        SharedPreferences mSharedPreferences = mContext.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putLong(mKey, mValue);
        editor.apply();
    }

    /*public static String getUserNamePhotex(Context mContext) {

        return getSetting(mContext, KEY_USER_NAME_PHOTEX, "");
    }

    public static void setUserNamePhotex(Context mContext, String mValue) {
        saveSetting(mContext, KEY_USER_NAME_PHOTEX, mValue);
    }
*/

    public static void setGoogleToken(Context mContext, String mValue) {
        saveSetting(mContext, KEY_GOOGLE_TOKEN, mValue);
    }

    public static String getGoogleToken(Context mContext) {

        return getSetting(mContext, KEY_GOOGLE_TOKEN, "");
    }


    public static void setUserId(Context mContext, String mValue) {
        saveSetting(mContext, KEY_USER_ID, mValue);
    }

    public static String getUserId(Context mContext) {

        return getSetting(mContext, KEY_USER_ID, "");
    }


    public static String getUserEmail(Context mContext) {

        return getSetting(mContext, KEY_USER_EMAIL, "");
    }

    public static void setUserEmail(Context mContext, String mValue) {
        saveSetting(mContext, KEY_USER_EMAIL, mValue);
    }

    public static String getProfilePictureURL(Context mContext) {
        return getSetting(mContext, KEY_PROFILE_PICTURE_URL, "");
    }

    public static void setProfilePictureURL(Context mContext, String mValue) {
        saveSetting(mContext, KEY_PROFILE_PICTURE_URL, mValue);
    }

    public static String getUserFolderName(Context context) {
        return getSetting(context, KEY_USER_FOLDER_NAME, "");
    }

    public static void setUserFolderName(Context mContext, String mValue) {
        saveSetting(mContext, KEY_USER_FOLDER_NAME, mValue);
    }


    public static String getUserFullName(Context context) {
        return getSetting(context, KEY_USER_FULL_NAME, "");
    }

    public static void setUserFullName(Context mContext, String mValue) {
        saveSetting(mContext, KEY_USER_FULL_NAME, mValue);
    }

    public static String getUserPictureURL(Context mContext) {

        return getSetting(mContext, KEY_USER_PICTURE_URL, "");
    }

    public static void setUserPictureURL(Context mContext, String mValue) {
        saveSetting(mContext, KEY_USER_PICTURE_URL, mValue);
    }

    public static String getUserPictureVersion(Context mContext) {

        return getSetting(mContext, KEY_USER_PICTURE_VERSION, "");
    }

    public static void setUserPictureVersion(Context mContext, String mValue) {
        saveSetting(mContext, KEY_USER_PICTURE_VERSION, mValue);
    }

    public static String getFeed(Context mContext) {

        return getSetting(mContext, KEY_SAVE_FEED, "");
    }

    public static void setFeed(Context mContext, String mValue) {
        saveSetting(mContext, KEY_SAVE_FEED, mValue);
    }

    public static String getUrl(Context mContext) {

        return getSetting(mContext, KEY_SAVE_URL, "");
    }

    public static void setUrl(Context mContext, String mValue) {
        saveSetting(mContext, KEY_SAVE_URL, mValue);
    }

    public static boolean getFromPhotex(Context mContext) {

        return getSettingBoolean(mContext, KEY_IS_FROM_UPLOAD, false);
    }

    public static void setFromPhotex(Context mContext, boolean mValue) {
        saveSettingBoolean(mContext, KEY_IS_FROM_UPLOAD, mValue);
    }

    public static boolean getPhotexInstalled(Context mContext) {

        return getSettingBoolean(mContext, KEY_IS_PHOTEX_INSTALLED_FIRST, false);
    }

    public static void setPhotexInstalled(Context mContext, boolean mValue) {
        saveSettingBoolean(mContext, KEY_IS_PHOTEX_INSTALLED_FIRST, mValue);
    }

    public static boolean getChangeInProfileFragment(Context mContext) {

        return getSettingBoolean(mContext, KEY_CHANGE_PROFILE_FRAGMENT, false);
    }

    public static void setChangeInProfileFragment(Context mContext, boolean mValue) {
        saveSettingBoolean(mContext, KEY_CHANGE_PROFILE_FRAGMENT, mValue);
    }


    public static void setFcmToken(Context mContext, String mValue) {
        saveSetting(mContext, KEY_FCM_TOKEN, mValue);
    }

    public static String getFcmToken(Context mContext) {
        return getSetting(mContext, KEY_FCM_TOKEN, "");
    }

    public static void setServerToken(Context mContext, String mValue) {
        saveSetting(mContext, KEY_SERVER_TOKEN, mValue);
    }

    public static String getServerToken(Context mContext) {
        return getSetting(mContext, KEY_SERVER_TOKEN, "");
    }

    public static void setServerTokenTime(Context mContext, long mValue) {
        saveLongSetting(mContext, KEY_TOKEN_REFRESHED_TIME, mValue);
    }

    public static long getServerTokenTime(Context mContext) {
        return getLongSetting(mContext, KEY_TOKEN_REFRESHED_TIME, 0);
    }

    public static void setNotiCount(Context mContext, String mValue) {
        saveSetting(mContext, KEY_GET_NOTI_COUNT, mValue);
    }

    public static String getNotiCount(Context mContext) {
        return getSetting(mContext, KEY_GET_NOTI_COUNT, "0");
    }

    public static int getInFcmMessagingNotificationLauncherCounter(Context mContext) {

        return getIntSetting(mContext, KEY_NOTIFI_LAUNCHER_COUNTER, 0);
    }

    public static void setInFcmMessagingNotificationLauncherCounter(Context mContext, int mValue) {
        saveIntSetting(mContext, KEY_NOTIFI_LAUNCHER_COUNTER, mValue);
    }


    public static void setFcmMessageArrayListTitleMessageForLike(Context mContext, HashSet mValue) {
        saveArrayListString(mContext, KEY_NOTIFI_TITLE_Message_LIKE, mValue);
    }

    public static Set getFcmMessageArrayListTitleMessageForLike(Context mContext) {

        return getArrayListString(mContext, KEY_NOTIFI_TITLE_Message_LIKE, null);
    }

    public static void setFcmMessageArrayListTitleMessageForComment(Context mContext, HashSet mValue) {
        saveArrayListString(mContext, KEY_NOTIFI_TITLE_Message_COMMENT, mValue);
    }

    public static Set getFcmMessageArrayListTitleMessageForComment(Context mContext) {

        return getArrayListString(mContext, KEY_NOTIFI_TITLE_Message_COMMENT, null);
    }

    public static void setFcmMessageArrayListPostIdLike(Context mContext, HashSet mValue) {
        saveArrayListString(mContext, KEY_NOTIFI_POST_ID_ARRAY_LIKE, mValue);
    }

    public static Set getFcmMessageArrayListPostIdLike(Context mContext) {

        return getArrayListString(mContext, KEY_NOTIFI_POST_ID_ARRAY_LIKE, null);
    }

    public static void setFcmMessageArrayListPostIdComment(Context mContext, HashSet mValue) {
        saveArrayListString(mContext, KEY_NOTIFI_POST_ID_ARRAY_COMMENT, mValue);
    }

    public static Set getFcmMessageArrayListPostIdComment(Context mContext) {

        return getArrayListString(mContext, KEY_NOTIFI_POST_ID_ARRAY_COMMENT, null);
    }

    public static int getInFcmMessagingNotificationLikeCounter(Context mContext) {

        return getIntSetting(mContext, KEY_NOTIFI_LIKE_COUNTER, 0);
    }

    public static void setInFcmMessagingNotificationLikeCounter(Context mContext, int mValue) {
        saveIntSetting(mContext, KEY_NOTIFI_LIKE_COUNTER, mValue);
    }

    public static int getInFcmMessagingNotificationCommentCounter(Context mContext) {

        return getIntSetting(mContext, KEY_NOTIFI_COMMENT_COUNTER, 1000);
    }

    public static void setInFcmMessagingNotificationCommentCounter(Context mContext, int mValue) {
        saveIntSetting(mContext, KEY_NOTIFI_COMMENT_COUNTER, mValue);
    }

    public static boolean getProfileServiceRunning(Context mContext) {

        return getSettingBoolean(mContext, KEY_NOTIFICATION_PROFILE_PICTURE_ID, false);
    }

    public static void setProfileServiceRunning(Context mContext, boolean mValue) {
        saveSettingBoolean(mContext, KEY_NOTIFICATION_PROFILE_PICTURE_ID, mValue);
    }


    public static int getBackCoverNotificationId(Context mContext) {

        return getIntSetting(mContext, KEY_NOTIFICATION_BACK_COVER_PICTURE_ID, 0);
    }

    public static void setBackCoverNotificationId(Context mContext, int mValue) {
        saveIntSetting(mContext, KEY_NOTIFICATION_BACK_COVER_PICTURE_ID, mValue);
    }


    public static boolean getIsProfilePictureChanged(Context mContext) {

        return getSettingBoolean(mContext, KEY_IS_PROFILE_PICTURE_CHANGED, true);
    }

    public static void setIsProfilePictureChanged(Context mContext, boolean mValue) {
        saveSettingBoolean(mContext, KEY_IS_PROFILE_PICTURE_CHANGED, mValue);
    }

    public static boolean getIsProfilePictureUploaded(Context mContext) {

        return getSettingBoolean(mContext, KEY_IS_PROFILE_PICTURE_UPLOADED, false);
    }

    public static void setIsProfilePictureUploaded(Context mContext, boolean mValue) {
        saveSettingBoolean(mContext, KEY_IS_PROFILE_PICTURE_UPLOADED, mValue);
    }

    public static void setFcmMessageArrayListPostIdCommentReply(Context mContext, HashSet mValue) {
        saveArrayListString(mContext, KEY_NOTIFI_POST_ID_ARRAY_COMMENT_REPLY, mValue);
    }

    public static Set getFcmMessageArrayListPostIdCommentReply(Context mContext) {

        return getArrayListString(mContext, KEY_NOTIFI_POST_ID_ARRAY_COMMENT_REPLY, null);
    }

    public static void setFcmMessageArrayListTitleMessageForCommentReply(Context mContext, HashSet mValue) {
        saveArrayListString(mContext, KEY_NOTIFI_TITLE_Message_COMMENT_REPLY, mValue);
    }

    public static Set getFcmMessageArrayListTitleMessageForCommentReply(Context mContext) {

        return getArrayListString(mContext, KEY_NOTIFI_TITLE_Message_COMMENT_REPLY, null);
    }

    public static int getInFcmMessagingNotificationCommentReplyCounter(Context mContext) {

        return getIntSetting(mContext, KEY_NOTIFI_COMMENT_REPLY_COUNTER, 2000);
    }

    public static void setInFcmMessagingNotificationCommentReplyCounter(Context mContext, int mValue) {
        saveIntSetting(mContext, KEY_NOTIFI_COMMENT_REPLY_COUNTER, mValue);
    }


    public static boolean getPostUploadingStatus(Context mContext) {

        return getSettingBoolean(mContext, KEY_POST_UPLOADING_STATUS, false);
    }

    public static void setPostUploadingStatus(Context mContext, boolean mValue) {
        saveSettingBoolean(mContext, KEY_POST_UPLOADING_STATUS, mValue);
    }


    public static boolean getPostUploadedStatus(Context mContext) {

        return getSettingBoolean(mContext, KEY_POST_UPLOADED_POST_STATUS, false);
    }

    public static void setPostUploadedStatus(Context mContext, boolean mValue) {
        saveSettingBoolean(mContext, KEY_POST_UPLOADED_POST_STATUS, mValue);
    }


    public static String getPostImageURL(Context mContext) {

        return getSetting(mContext, KEY_POST_IMAGE_URL, "");
    }

    public static void setPostImageURL(Context mContext, String mValue) {
        saveSetting(mContext, KEY_POST_IMAGE_URL, mValue);
    }

    public static String getPostId(Context mContext) {

        return getSetting(mContext, KEY_POST_ID, "");
    }

    public static void setPostId(Context mContext, String mValue) {
        saveSetting(mContext, KEY_POST_ID, mValue);
    }


    public static int getPostNotificationId(Context mContext) {

        return getIntSetting(mContext, KEY_POST_NOTIFICATION_ID, 0);
    }

    public static void setPostNotificationId(Context mContext, int mValue) {
        saveIntSetting(mContext, KEY_POST_NOTIFICATION_ID, mValue);
    }

    public static boolean getPostPictureSuccessFailureStatus(Context mContext) {

        return getSettingBoolean(mContext, KEY_POST_PICTURE_SUCCESS, false);
    }

    public static void setPostPictureSuccessFailureStatus(Context mContext, boolean mValue) {
        saveSettingBoolean(mContext, KEY_POST_PICTURE_SUCCESS, mValue);
    }

    public static String getVideoResolutionLimit(Context mContext) {

        return getSetting(mContext, KEY_VIDEO_RESOLUTION_LIMIT, "1024");
    }

    public static void setVideoResolutionLimit(Context mContext, String mValue) {
        saveSetting(mContext, KEY_VIDEO_RESOLUTION_LIMIT, mValue);
    }

    public static String getVideoSizeLimit(Context mContext) {

        return getSetting(mContext, KEY_VIDEO_SIZE_LIMIT, "200");
    }

    public static void setVideoSizeLimit(Context mContext, String mValue) {
        saveSetting(mContext, KEY_VIDEO_SIZE_LIMIT, mValue);
    }

    public static String getVideoTimeLimit(Context mContext) {

        return getSetting(mContext, KEY_VIDEO_TIME_LIMIT, "10");
    }

    public static void setVideoTimeLimit(Context mContext, String mValue) {
        saveSetting(mContext, KEY_VIDEO_TIME_LIMIT, mValue);
    }


}
