package com.photex.tiktok.rest;

import com.photex.tiktok.models.Comment;
import com.photex.tiktok.models.CommentReply;
import com.photex.tiktok.models.ResponseAudioInfo;
import com.photex.tiktok.models.ResponseAudioInfoOld;
import com.photex.tiktok.models.restmodels.DeleteComment;
import com.photex.tiktok.models.restmodels.DeleteCommentReply;
import com.photex.tiktok.models.restmodels.FcmToken;
import com.photex.tiktok.models.restmodels.Follow;
import com.photex.tiktok.models.restmodels.GetAllPost;
import com.photex.tiktok.models.restmodels.GetAllReplies;
import com.photex.tiktok.models.restmodels.GetAudios;
import com.photex.tiktok.models.restmodels.GetComments;
import com.photex.tiktok.models.restmodels.GetMyFollowList;
import com.photex.tiktok.models.restmodels.GetMyPosts;
import com.photex.tiktok.models.restmodels.GetUserFollowers;
import com.photex.tiktok.models.restmodels.GetUserProfile;
import com.photex.tiktok.models.restmodels.SharePost;
import com.photex.tiktok.models.restmodels.UnFollow;
import com.photex.tiktok.models.restmodels.UserBasicInfo;
import com.photex.tiktok.models.restmodels.UserFollowingList;
import com.photex.tiktok.models.restmodels.UserPost;
import com.photex.tiktok.models.restmodels.UserProfile;
import com.photex.tiktok.models.restmodels.UserSignIn;
import com.photex.tiktok.models.restmodels.UserSignUpInfo;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface Restapi {
    // Base Url http:115.186.156.165
    @POST("api/v2/user/signup_user")
    Call<String> userSignUp(@Body UserSignUpInfo userSignUpInfo);

    /*User SignIn*/
    @POST("api/v2/user/get_id")
    Call<String> userSignIn(@Body UserSignIn userSignIn);

    //update fcm token
    @POST("api/v2/user/update_fcm_key")
    Call<String> updateFcmToken(@Body FcmToken fcmToken);

    // Upload Video, thumbnail and add post
    @Multipart
    @POST("api/v2/post/ad_post")
    Call<String> addPost(
            @PartMap Map<String, RequestBody> partMap,
            @Part MultipartBody.Part video,
            @Part MultipartBody.Part thumbnail);

    @POST("api/v2/post/get_all_posts")
    Call<String> getAllPosts(@Body GetAllPost allPosts);

    @POST("api/v2/post/comment")
    Call<String> addComment(@Body Comment postComment);

    @POST("api/v2/post/get_comments")
    Call<String> getComments(@Body GetComments getComments);

    @POST("api/v2/post/delete_comment")
    Call<String> deleteComment(@Body DeleteComment deleteComment);

    @POST("api/v2/post/commentReply")
    Call<String> addCommentReply(@Body CommentReply commentReply);

    @POST("api/v2/post/getCommentReplies")
    Call<String> getCommentReplies(@Body GetAllReplies getAllReplies);

    @POST("api/v2/post/deleteCommentReply")
    Call<String> deleteCommentReply(@Body DeleteCommentReply deleteCommentReply);

    @POST("/api/v2/post/shares")
    Call<String> sharePost(@Body SharePost sharePost);

    @Multipart
    @POST("api/v2/user/update_display_picture")
    Call<String> updateDisplayPictureMultiPart(@PartMap Map<String, RequestBody> partMap,
                                               @Part MultipartBody.Part fullDisplayPicture,
                                               @Part MultipartBody.Part displayPicture);

    @POST("api/v2/user/get_profile_info")
    Call<String> userProfile(@Body UserProfile userProfile);

    // Get  myPosts when you visit your own profile
    @POST("api/v2/post/get_my_posts")
    Call<String> getMyPosts(@Body GetMyPosts getMyPosts);

    @POST("api/v2/user/get_user_profile_info")
    Call<String> getUserProfile(@Body GetUserProfile getUserProfile);

    @POST("api/v2/post/get_user_posts")
    Call<String> getUserPost(@Body UserPost userPost);

    @POST("api/v2/user/follow")
    Call<String> follow(@Body Follow follow);

    @POST("api/v2/user/unfollow")
    Call<String> unFollow(@Body UnFollow unFollow);

    @POST("api/v2/user/get_my_following")
    Call<String> getMyFollowingList(@Body GetMyFollowList getMyFollowList);

    @POST("api/v2/user/get_user_following")
    Call<String> getUserFollowingList(@Body UserFollowingList userFollowingList);

    @POST("api/v2/user/get_my_followers")
    Call<String> getMyFollwersList(@Body GetMyFollowList getMyFollowList);

    @POST("api/v2/user/get_user_followers")
    Call<String> getUserFollowersList(@Body GetUserFollowers getUserFollowers);

    @POST("api/v2/user/updateUserInfo")
    Call<String> updateBasicInfo(@Body UserBasicInfo info);

    @POST("category/get_categories")
    Call<String> getCategories();

    // Upload SingleAudioInfo, thumbnail and add post
    @Multipart
    @POST("audio/upload_audio")
    Call<String> uploadAudio(
            @PartMap Map<String, RequestBody> partMap,
            @Part MultipartBody.Part audio,
            @Part MultipartBody.Part thumbnail);

    @POST("audio/get_all_audios")
    Call<String> getAllAudios(@Body GetAudios getAudios);

    @POST("audio/get_all_audios")
    Call<ResponseAudioInfo> getAllAudioWithResponse(@Body GetAudios getAudios);


    /*@POST("/api/v2/post/incrementViews")
    Call<String> postVideosView(@Body VideoViewsRequest videoViewsRequest);

    *//*User update Profile Data*//*
    @POST("api/v2/user/update_profile_info")
    Call<String> updateProfile(@Body UpdateUserProfile user);

    *//*Get Users Post when you visit someone else profile*//*
    @POST("api/v2/post/get_user_posts")
    Call<String> getUserPost(@Body UserPost userPost);*/

    /*Delete Post
    @POST("api/v2/post/delete_post")
    Call<String> deletePost(@Body DeletePost deletePost);//TODO remaining jwt expire

    //Get Posts
    // @POST("api/v2/post/getFollowingPosts")
    @POST("api/v2/post/getFollowingPostsWithAds")*/

    /*@POST("api/v2/post/getNotFollowingPosts")
    Call<String> getNotFollowingPosts(@Body AllPosts allPosts);

    @POST("api/v2/user/refreshToken")
    Call<String> refreshServerToken();

    *//*Get user Profile*//*
    @POST("api/v2/user/get_user_profile_info")
    Call<String> getUserProfile(@Body GetUserProfile getUserProfile);


    *//*Update Back Cover Picture*//*
    @POST("api/v2/user/update_backcover_picture")
    Call<String> updateBackCover(@Body UpdateBackCover backCover);//TODO remaining jwt expire
    *//*GetUsers Followers*//*

     *//*Update Display Picture*//*
    @POST("api/v2/user/update_display_picture")
    Call<String> updateDisplayPicture(@Body UpdateDisplayPicture picture);//TODO remaining jwt expire


    @Multipart
    @POST("api/v2/user/update_backcover_picture")
    Call<String> updateBackCoverMultiPart(@PartMap Map<String, RequestBody> partMap,
                                          @Part MultipartBody.Part backCover);//TODO remaining jwt expire
    // Upload Video, thumbnail and add post


    *//*Like Post*//*
    @POST("api/v2/post/like")
    Call<String> postLike(@Body PostLike like);//TODO remaining jwt expire

    *//*UnLike Post*//*
    @POST("api/v2/post/unlike")
    Call<String> postUnlike(@Body PostUnlike unlike);//TODO remaining jwt expire

    //get Likes
    @POST("api/v2/post/get_likes")
    Call<String> getLikes(@Body GetLikes getLikes);

   *//* @POST("api/v2/user/get_latest_users")
    Call<String> getSearches(@Body GetSearch getSearch);*//*

    @POST("api/v2/user/get_active_users")
    Call<String> getSearches(@Body GetSearch getSearch);

    @POST("api/v2/user/userSuggestions")
    Call<String> getSuggestions(@Body GetSearch getSuggestions);

    @POST("api/v2/user/search_user")
    Call<String> getSearchByName(@Body GetSearchByName byName);

    @POST("api/v2/user/searchUserWithScore")
    Call<String> getSearchByScore(@Body GetSearchByScore searchByScore);


    @POST("api/v2/user/notification")
    Call<String> getNotifications(@Body AllPosts noti);///////////////

    @POST("api/v2/post/get_post_info")
    Call<String> getSinglePost(@Body SinglePostRequest singlePostRequest);

    @POST("api/v2/read")
    Call<String> sentNotiReadStatus(@Body NotiReadStatus notiReadStatus);//TODO remaining jwt expire

    @POST("api/v2/user/getNotificationsCount")
    Call<String> getNotificationCount(@Body GetNotificationCount getNotificationCount);

    @POST("api/v2/user/resetNotificationsCount")
    Call<String> resetNotificationCount(@Body GetNotificationCount getNotificationCount);

    // Education information
    @POST("api/v2/user/addEducationInfo")
    Call<String> addEducationInfo(@Body UserEducationRest info);

    @POST("api/v2/user/updateEducationInfo")
    Call<String> updateEducationInfo(@Body UserEducationEdit info);

    @POST("api/v2/user/addPlacesLived")
    Call<String> addUserPlaceInfo(@Body UserPlace info);

    @POST("api/v2/user/updatePlacesLived")
    Call<String> updateUserPlaceInfo(@Body UserPlaceEdit userPlaceEdit);

    @POST("api/v2/user/deletePlacesInfo")
    Call<String> deletePlaceInfo(@Body PlaceDeleteInfo info);

    @POST("api/v2/user/deleteEducationInfo")
    Call<String> deleteEducationInfo(@Body EducationDeleteInfo info);

    @POST("api/v2/user/updateContactInfo")
    Call<String> updateUserContactInfo(@Body UserContactInfo info);

    @POST("api/v2/user/updateProfessionalSkills")
    Call<String> updateUserProfessionalSkills(@Body UserProfessionalSkills info);

    @POST("api/v2/user/updateRelationshipInfo")
    Call<String> updateUserRelationshipInfo(@Body UserRelationshipInfo info);

    @POST("api/v2/user/updateBioInfo")
    Call<String> updateUserBio(@Body UserBio info);

    @POST("api/v2/user/addWorkInfo")
    Call<String> addWorkInfo(@Body UserWorkRest info);

    @POST("api/v2/user/deleteWorkInfo")
    Call<String> deleteWorkInfo(@Body WorkDeleteInfo info);

    @POST("api/v2/user/updateWorkInfo")
    Call<String> updateWorkInfo(@Body UserWorkEdit info);

    @POST("api/v2/post/reportPost")
    Call<String> reportPost(@Body ReqReportPost reqReportPost);

    @POST("api/v2/user/logout")
    Call<String> logOut(@Body LogOut logOut);

    @POST("getPhotexUpdate")
    Call<String> getAppVersion();


    // everytime user comes in
    @GET("/stats/daily-users")
    Call<String> sendStats();

    //Insert install
    @GET("/stats/insert-photex-installs")
    Call<String> insert_install();


    //User Registered
    @GET("graph/user_registration.php")
    Call<String> user_registered();

    //User Visited
    @GET("graph/photex_visitor.php")
    Call<String> user_visited();

    //User Active
    @GET("/graph/active_visitors.php")
    Call<String> user_active();

    //User InActive
    @GET("/graph/inactive_visitors.php")
    Call<String> user_in_active();

    //Post uploaded
    @GET("graph/post_uploaded.php")
    Call<String> post_uploaded();

    //FeedAds
    @POST("api/v2/post/get_all_ads")
    Call<String> getPostAds();

    //http://34.202.78.78/
    @GET("classified/index.php/?id=1")
    Call<String> getAdsTesting();

    @POST("api/v2/ad/activeAdNetwork")
    Call<String> getActiveAdId();

    //////////////////Groups Endpoints////////////////
    @POST("api/v2/group/create-group")
    Call<String> creatGroup(@Body CreateGroup createGroup);

    @POST("api/v2/group")
    Call<String> getAllGroup(@Body AllGroup allGroup);

    @POST("api/v2/group/single-group")
    Call<String> getGroupDetail(@Body SingleGroup singleGroup);

    @POST("api/v2/group/paginated-posts")
    Call<String> groupPosts();

    //////////////////Groups Endpoints////////////////
    *//*Short URL ANDRIOD*//*
    @Headers("Content-Type:application/json")
    @POST("urlshortener/v1/url?key=AIzaSyBobysHVLzXC1ISbWUFd8PI05cXnI72gRw")
    Call<String> getShortURl(@Body GetShortURL getShortURL);

    @POST("api/v2/user/videoSuggestions")
    Call<String> getSuggestedVideos(@Body GetSuggestVideo getSuggestVideo);*/


}