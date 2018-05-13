package com.example.development.sakaiclientandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;

import com.example.development.sakaiclientandroid.api_models.all_sites.AllSites;
import com.example.development.sakaiclientandroid.services.SakaiService;
import com.example.development.sakaiclientandroid.utils.HeaderInterceptor;
import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {

    private String BASE_URL;
    private String COOKIE_URL_1;
    private String COOKIE_URL_2;

    OkHttpClient httpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BASE_URL = getString(R.string.BASE_URL);
        COOKIE_URL_1 = getString(R.string.COOKIE_URL_1);
        COOKIE_URL_2 = getString(R.string.COOKIE_URL_2);

        CookieManager cookieManager = CookieManager.getInstance();
        Log.i("Cookie 1", cookieManager.getCookie(COOKIE_URL_1));
        Log.i("Cookie 2", cookieManager.getCookie(COOKIE_URL_2));

        // Create the custom OkHttpClient with the inceptor to inject
        // cookies intro every request
        httpClient = new OkHttpClient.Builder()
                .addInterceptor(new HeaderInterceptor(COOKIE_URL_1, COOKIE_URL_2))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();

        Log.i("test", "test1");

        SakaiService sakaiService = retrofit.create(SakaiService.class);
        Call<AllSites> fetchSitesCall = sakaiService.getAllSites();
        fetchSitesCall.enqueue(new Callback<AllSites>() {
            @Override
            public void onResponse(Call<AllSites> call, Response<AllSites> response) {
                Log.i("Response", "SUCCESS!");
                AllSites allSites = response.body();
                Log.i("Sites", allSites.toString());
            }

            @Override
            public void onFailure(Call<AllSites> call, Throwable t) {
                Log.i("Response", "failure");
            }
        });
    }
}
