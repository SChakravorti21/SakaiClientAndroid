package com.example.development.sakaiclientandroid.utils;

import android.content.Context;

import com.example.development.sakaiclientandroid.R;
import com.example.development.sakaiclientandroid.services.SakaiService;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Development on 5/21/18.
 */

public class RequestManager {

    private static SakaiService sakaiService;

    public static void createRetrofitInstance(Context context) {
        // Get the base url for the Sakai API
        String baseUrl = context.getString(R.string.BASE_URL);
        // Get the url which has the relevant cookies for Sakai
        String cookieUrl = context.getString(R.string.COOKIE_URL_1);

        // Create the custom OkHttpClient with the interceptor to inject
        // cookies into every request
        HeaderInterceptor interceptor = new HeaderInterceptor(context, cookieUrl);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        // The Retrofit instance allows us to construct our own services
        // that will make network requests
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();

        // Create the Retrofit service with all endpoints
        sakaiService = retrofit.create(SakaiService.class);
    }

    public static void fetchAllSites(Callback<ResponseBody> responseCallback) {
        Call<ResponseBody> fetchSitesCall = sakaiService.getResponseBody();
        fetchSitesCall.enqueue(responseCallback);
    }

}
