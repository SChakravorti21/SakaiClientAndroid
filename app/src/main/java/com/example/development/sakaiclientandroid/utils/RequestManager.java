package com.example.development.sakaiclientandroid.utils;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.example.development.sakaiclientandroid.R;
import com.example.development.sakaiclientandroid.api_models.all_sites.AllSitesAPI;
import com.example.development.sakaiclientandroid.fragments.HomeFragment;
import com.example.development.sakaiclientandroid.services.SakaiService;
import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RequestManager {


    private static String baseUrl;
    private static String cookieUrl;
    private static OkHttpClient httpClient;


    public static boolean fetchAllSites(Context context) {

        // Get the base url for the Sakai API
        baseUrl = context.getString(R.string.BASE_URL);
        // Get the url which has the relevant cookies for Sakai
        cookieUrl = context.getString(R.string.COOKIE_URL_1);

        // Create the custom OkHttpClient with the interceptor to inject
        // cookies into every request
        HeaderInterceptor interceptor = new HeaderInterceptor(context, cookieUrl);
        httpClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        // The Retrofit instance allows us to construct our own services
        // that will make network requests
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();

        // Making a test request using a retrofit client
        // and the getAllSites endpoint (which fetches "site.json")
        SakaiService sakaiService = retrofit.create(SakaiService.class);
        Call<ResponseBody> fetchSitesCall = sakaiService.getResponseBody();


        fetchSitesCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i("Response", "SUCCESS!");
                Log.i("Status Code", "" + response.code());

                try {

                    String responseBody = response.body().string();

                    Gson gson = new Gson();
                    AllSitesAPI api = gson.fromJson(responseBody, AllSitesAPI.class);
                    api.fillSitePages(responseBody);


//                    Bundle bundle = new Bundle();
//                    bundle.putString(getString(R.string.title_activity_nav), responseBody);
//
//                    HomeFragment fragment = new HomeFragment();
//                    fragment.setArguments(bundle);
//                    loadFragment(fragment);

                }

                catch(Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // TODO: Handle errors give proper error message
                Log.i("Response", "failure");
                Log.e("Response error", t.getMessage());

            }
        });

    }
}
