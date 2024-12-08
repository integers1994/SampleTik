package com.photex.tiktok.rest;

import android.content.Context;
import com.photex.tiktok.setting.SettingManager;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*
import retrofit2.GsonConverterFactory;
*/


public class RestClient {
    private static Restapi REST_CLIENT_SIMPLE;
    private static Context context;
    private static final String TAG = RestClient.class.getSimpleName();

    public RestClient(String base_url) {
        simpleRestClient(base_url);
    }

    public RestClient(String base_url, Context context) {
        this.context = context;
        simpleRestClient(base_url);
    }

    public static Restapi get() {
        return REST_CLIENT_SIMPLE;
    }


    private static void simpleRestClient(String ROOT) {
        //try {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
//        okHttpClient.interceptors().add(logging);
        okHttpClient.readTimeout(50, TimeUnit.SECONDS);
        okHttpClient.connectTimeout(50, TimeUnit.SECONDS);
        okHttpClient.writeTimeout(50, TimeUnit.SECONDS);

        okHttpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request()
                        .newBuilder().addHeader("x-access-token",
                                SettingManager.getServerToken(context)).build();
                return chain.proceed(request);
            }
        });


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ROOT)
                .addConverterFactory(new ToStringConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient.build())
                .build();


        REST_CLIENT_SIMPLE = retrofit.create(Restapi.class);

    }

}
