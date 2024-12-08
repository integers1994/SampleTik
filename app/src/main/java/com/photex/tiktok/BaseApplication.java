package com.photex.tiktok;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;

import java.io.File;

import androidx.multidex.MultiDex;
import im.ene.toro.exoplayer.Config;
import im.ene.toro.exoplayer.ExoCreator;
import im.ene.toro.exoplayer.MediaSourceBuilder;
import im.ene.toro.exoplayer.ToroExo;

public class BaseApplication extends Application {
    public static Config config = null;
    public static ExoCreator exoCreator = null;
    private static long cacheFile = 2 * 1024 * 1024;

    @Override
    public void onCreate() {
        super.onCreate();
        // this configration is for Looping the video
        SimpleCache cache = new SimpleCache(new File(getCacheDir(), "/toro_cache"), new LeastRecentlyUsedCacheEvictor(cacheFile));
        config = new Config.Builder().setMediaSourceBuilder(MediaSourceBuilder.LOOPING)
                .setCache(cache)
                .build();//this is use for lopping

        exoCreator = ToroExo.with(this).getCreator(config);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level >= TRIM_MEMORY_BACKGROUND) ToroExo.with(this).cleanUp();

    }
}