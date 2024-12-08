package com.photex.tiktok.rest;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Ameer Hamza on 1/17/2017.
 */

public abstract class CallbackWithRetry<T> implements Callback<T> {

    private static final int TOTAL_RETRIES = 5;
    private static final String TAG = CallbackWithRetry.class.getSimpleName();
    private final Call<T> call;
    private int retryCount = 0;

    public CallbackWithRetry(Call<T> call) {
        this.call = call;
    }

    @Override
    public void onFailure(Call Call, Throwable t) {
        //Log.e(TAG, t.getLocalizedMessage());
        if (retryCount++ < TOTAL_RETRIES) {
            Log.v("retry", "Retrying... (" + retryCount + " out of " + TOTAL_RETRIES + ")");
            retry();
        } else {
            onFinallyFail();
        }
    }

    abstract public void onFinallyFail();

    private void retry() {
        call.clone().enqueue(this);
    }
}