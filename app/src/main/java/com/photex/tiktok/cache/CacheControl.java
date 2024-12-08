package com.photex.tiktok.cache;

import android.util.Log;

import com.anupcowkur.reservoir.Reservoir;
import com.anupcowkur.reservoir.ReservoirDeleteCallback;
import com.anupcowkur.reservoir.ReservoirPutCallback;
import com.google.gson.reflect.TypeToken;
import com.photex.tiktok.models.Post;
import com.photex.tiktok.models.Search;
import com.photex.tiktok.models.User;
import com.photex.tiktok.utils.Constants;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;


public class CacheControl {

    public static void putFeedsInCache(ArrayList<Post> feeds) {

        Reservoir.putAsync(Constants.KEY_FEEDS_CACHE, feeds, new ReservoirPutCallback() {
            @Override
            public void onSuccess() {
                Log.d("FeedsCache", "Put Feeds success");
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("FeedsCache", "Put Feeds error " + e.toString());
            }
        });

    }
/*    public static void putSearchUserInCache(ArrayList<Search> searchUser) {

        Reservoir.putAsync(Constants.KEY_LEATEST_USER_CACHE, searchUser, new ReservoirPutCallback() {
            @Override
            public void onSuccess() {
                Log.d("FeedsCache", "Put search user success");
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("FeedsCache", "Put search user error " + e.toString());
            }
        });

    }

    public static ArrayList<Search> getSearchUserFromCache() {

        ArrayList<Search> searchUsers = new ArrayList<>();
        //Get collection
        Type resultType = new TypeToken<ArrayList<Search>>() {
        }.getType();
        try {
            searchUsers = Reservoir.get(Constants.KEY_LEATEST_USER_CACHE, resultType);
            Log.d("FeedsCache", "Feeds"+searchUsers.size());
        } catch (Exception e) {
            Log.d("FeedsCache", "Feeds get from cache error " + e.toString());
        }
        return searchUsers;
    }





    public static void putMyPostsGalleryInCache(ArrayList<Post> posts) {
        Reservoir.putAsync(Constants.KEY_MYPOST_GALLERY_CACHE, posts, new ReservoirPutCallback() {
            @Override
            public void onSuccess() {
                Log.d("FeedsCache", "Put MyPosts success");
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("FeedsCache", "Put MyPosts error " + e.toString());
            }
        });
    }

    public static void putCurrentUserInCache(User user) {

        Reservoir.putAsync(Constants.KEY_USER_IN_CACHE, user, new ReservoirPutCallback() {
            @Override
            public void onSuccess() {
                Log.d("FeedsCache", "Put MyPosts success");
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("FeedsCache", "Put MyPosts error " + e.toString());
            }
        });
    }


    public static void deleteCurrentUserInCache() {

        Reservoir.deleteAsync(Constants.KEY_USER_IN_CACHE, new ReservoirDeleteCallback() {
            @Override
            public void onSuccess() {
                Log.d("logout", "User deleted in cache");
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    public static void deleteMyPostsCache() {

        Reservoir.deleteAsync(Constants.KEY_MYPOST_CACHE, new ReservoirDeleteCallback() {
            @Override
            public void onSuccess() {
                Log.d("FeedsCache", "MyPosts Deleted From Cache successfully");
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("FeedsCache", "MyPosts Deleted From Cache error " + e.toString());
            }
        });

    }

    public static void deleteMyPostsGalleryCache() {

        Reservoir.deleteAsync(Constants.KEY_MYPOST_GALLERY_CACHE, new ReservoirDeleteCallback() {
            @Override
            public void onSuccess() {
                Log.d("FeedsCache", "MyPosts Deleted From Cache successfully");
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("FeedsCache", "MyPosts Deleted From Cache error " + e.toString());
            }
        });

    }*/

    public static void deleteFeedsCache() {

        try {
            Reservoir.delete(Constants.KEY_FEEDS_CACHE);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("FeedsCache", ""+e.getMessage());
        }

    }

/*    public static void deleteUserInCache() {

        Reservoir.deleteAsync(Constants.KEY_USER_IN_CACHE, new ReservoirDeleteCallback() {
            @Override
            public void onSuccess() {
                Log.d("FeedsCache", "Feeds Deleted From Cache successfully");
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("FeedsCache", "Feeds Deleted From Cache error " + e.toString());
            }
        });

    }

    public static ArrayList<Post> getFeedsFromCache() {

        ArrayList<Post> feeds = new ArrayList<>();
        //Get collection
        Type resultType = new TypeToken<ArrayList<Post>>() {
        }.getType();
        try {
            feeds = Reservoir.get(Constants.KEY_FEEDS_CACHE, resultType);
            Log.d("FeedsCache", "Feeds"+feeds.size());
        } catch (Exception e) {
            Log.d("FeedsCache", "Feeds get from cache error " + e.toString());
        }
        return feeds;
    }


    public static User getCurrentUserFromCache() {

        User user = new User();
        //Get collection
        Type resultType = new TypeToken<User>() {
        }.getType();
        try {
            user = Reservoir.get(Constants.KEY_USER_IN_CACHE, resultType);
            Log.d("FeedsCache", "Feeds get from cache success");
        } catch (Exception e) {
            Log.d("FeedsCache", "Feeds get from cache error " + e.toString());
        }
        return user;
    }

    public static ArrayList<Post> getMyPostsFromCache() {

        ArrayList<Post> feeds = new ArrayList<>();
        //Get collection
        Type resultType = new TypeToken<ArrayList<Post>>() {
        }.getType();
        try {
            feeds = Reservoir.get(Constants.KEY_MYPOST_CACHE, resultType);
            Log.d("FeedsCache", "MyPosts get from cache success");
        } catch (Exception e) {
            Log.d("FeedsCache", "MyPosts get from cache error " + e.toString());
        }
        return feeds;
    }


    public static ArrayList<Post> getMyPostsGalleryCache() {

        ArrayList<Post> feeds = new ArrayList<>();
        //Get collection
        Type resultType = new TypeToken<ArrayList<Post>>() {
        }.getType();
        try {
            feeds = Reservoir.get(Constants.KEY_MYPOST_GALLERY_CACHE, resultType);
            Log.d("FeedsCache", "MyPosts get from cache success");
        } catch (Exception e) {
            Log.d("FeedsCache", "MyPosts get from cache error " + e.toString());
        }
        return feeds;
    }

    public static void clearAllCache() {
        try {
            Reservoir.clear();
            Log.d("AllCache", "All cache Clear success");
        } catch (Exception e) {
            Log.d("AllCache", "All cache Clear error " + e.toString());
        }
    }

    public static boolean hasKey(String key) {
        try {
            boolean objectExists = Reservoir.contains(key);
            return objectExists;
        } catch (Exception e) {
            return false;
        }
    }*/


}
