package com.photex.tiktok.utils;

import com.photex.tiktok.R;

public class Constants {

    public static final String EXTRA_VIDEO_PATH = "EXTRA_VIDEO_PATH";
    //in app requests
    public static final int CAMERA_PERMITTIONS_REQUEST = 1;
    public static final int SELECT_AUDIO_REQUEST = 2;
    public static final int PREVIEW_VIDEO_REQUEST = 3;
    public static final int COMMENT_ACTIVITY_REQUEST = 4;
    public static final int SHARE_VIDEO_REQUEST = 5;
    public static final int COMMENT_REPLY_ACTIVITY_REQUEST = 6;
    public static final int CREATE_VIDEO_ACTIVITY_REQUEST = 7;
    public static final int UPLOAD_VIDEO_REQUEST = 8;
    public static final int PROFILE_DETAIL_REQUEST = 9;
    public static final int CAMERA_REQUEST = 10;
    public static final int GALLERY_REQUEST = 11;
    public static final int PHOTO_REQUEST = 12;

    public static final int RC_CAMERA_CAPTURE = 13;
    public static final int CROP_PICTURE_REQUEST_CODE = 14;
    public static final int GALLARY_CAPTURE_REQUEST_CODE = 15;
    public static final int UPDATE_PROFILE = 16;
    public static final int STORAGE_PERMITTIONS_REQUEST = 17;
    public static final int RC_STORAGE_PERMISSION_GALLERY_VIDEOS = 18;
    public static final int RC_STORAGE_PERMISSION_GERNEAL = 19;
    public static final int RC_STORAGE_CAMERA_AUDIO_PERMISSION_GERNEAL = 20;
    public static final int RC_VIDEO_TRIM = 21;


    //    public static final String BASE_URL = "http://192.168.88.36/"; testing local ip
/*
    Vm in house
*//*
    public static final String BASE_URL = "http://115.186.156.165/";
    public static final String STREAMING_URL = "http://115.186.156.165";*/
    public static final String BASE_URL = "http://42.200.146.225:8090/";
    public static final String STREAMING_URL = "http://42.200.146.225:8090";

    public static final String TEST_SREAMING_URL = "http://115.186.156.171:8000/hls/";
    public static final String DYNAMIC_LINK = "https://tiktokvideos.page.link/?link=";
    public static final String SERVER_DIRECTORY = "tiktok/";
//    public static final String STREAMING_URL = "rtmp://192.168.88.36:1935/vod2/_definst_";
//    public static final String STREAM_PARAMS = " app=vod2 playpath=";

    public static final int MINIMUM_VIDEO_SIZE = 500; // milli second
    public static final int VIDEO_DURATION_ONE_MINUTE_MILLI_SECONDS = 60000; // milli second
    public static final int VIDEO_DURATION_FIFTEEN_SEC_MILLI_SECONDS = 15000; // milli second

    public static final String message = "message";
    public static final String error = "error";
    public static final String EXRTA_MEDIA_INFO = "extra_media_info";
    public static final String audioInfo = "audioInfo";
    public static final String EXTRA_POST_INFO = "extra_servicePostInfo";
    public static final String profileVideos = "profileVideos";
    public static final String userComment = "userComment";
    public static final String totalComments = "totalComments";
    public static final String totalReplies = "totalReplies";
    public static final String currentVideoPostion = "currentVideoPosition";
    public static final String isBtnClicked = "isBtnClicked";
    public static final String postById = "commentPostById";
    public static final String commentId = "commentId";
    public static final String commentById = "commentById";
    public static final String commentReplies = "commentReplies";
    public static final String isDataChanged = "isDataChanged";


    public static final String AUDIO_FOLDER = "Temp/Audios";
    public static final String TEMP_VIDEO_FOLDER = "Temp/Videos";

    public static final String TEMP_FOLDER= "Temp";
    public static final String TEMP_VIDEO_M3U8_FOLDER = "Temp/Videos/m3u8";
    public static final String TEMP_VIDEO_COMPRESS = "Temp/Compress";
    public static final String TEMP_AUDIO_FOLDER = "Temp/Audio";
    public static final String TEMP_IMAGE_FOLDER = "Temp/Images";
    public static final String TEMP_IMAGE_CROP_FOLDER = "Temp/Images/crop";
    public static final String VIDEO_FOLDER = "Videos";
    public static final String TIKTOKDIR = "MyTikTok";


    public static final String REGISTRATION_COMPLETE = "fcmregistration";
    public static final String START_SAVE_SERVICE = "startSaveService";
    public static final String START_UPLOAD_SERVICE = "startUploadService";
    public static final String DONE_SAVE_SERVICE = "done";
    public static final String CANCEL_SAVE_SERVICE = "stop";
    public static final String START_APPLICATION = "startApp";
    public static final String CURRENT_USER = "userInfo";
    public static final String USER_ID = "userId";

    public static final String EXTRA_URI = "extra_uri";
    public static final String EXTRA_VIDEO_FILE_URI = "video_file_uri";
    public static final String EXTRA_VIDEO_MEDIA_INFO = "media_info";


    public static final String STOP_PROFILE_PICTURE_SERVICE = "stopProfilePictureService";
    public static final String EVENT_PHOTO_CHANGED = "eventPhotoChanged";
    public static final String UPDATED_PICTURE = "updatedPic";
    public static final String UPDATED_PROFILE_PICTURE_FULL = "updatedProfilePictureFull";
    public static final String ACTION_UPLOAD_PICTURE = "uploadProfilePicture";
    public static final String SUCCESS = "success";
    public static final String CONTENT_TITLE = "contentTitle";
    public static final String CONTENT_TEXT = "contentText";
    public static final String NOTIFICATION_ID = "notificationId";


    // cache keys
    public static final String KEY_FEEDS_CACHE = "pkpipewithstoreFeedCache";

    public static final String TOKEN = "token";
    public static final String FULL_PHOTO = "fullPhoto";
    public static final String SHORT_PHOTO = "shortPhoto";
    public static final String UPLOAD_PHOTO = "uploadPhoto";
    public static final String IMAGE_FILE_PATH = "imageFile";


    public static final int[] audiolist = {
            R.raw.sound_1,
            R.raw.sound_2,
            R.raw.sound_3,
            R.raw.sound_4,
            R.raw.sound_5,
            R.raw.sound_6,
            R.raw.sound_7,
            R.raw.sound_8,
            R.raw.sound_9,
            R.raw.sound_10,
            R.raw.sound_11,
            R.raw.sound_12,
            R.raw.sound_13,
            R.raw.sound_14,
            R.raw.sound_15,
            R.raw.sound_16,
            R.raw.sound_17,
            R.raw.sound_18,
            R.raw.sound_19,
            R.raw.sound_20,
            R.raw.sound_21,
            R.raw.sound_22,
            R.raw.sound_23,
            R.raw.sound_24,
            R.raw.sound_25,
            R.raw.sound_26,
            R.raw.sound_27,
            R.raw.sound_28,
            R.raw.sound_29,
            R.raw.sound_30
    };

    public static String UPLOAD_BACK_COVER = "uploadBackCover";
}
