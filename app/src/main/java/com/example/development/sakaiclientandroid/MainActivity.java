package com.example.development.sakaiclientandroid;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;

import com.example.development.sakaiclientandroid.api_models.all_sites.AllSites;
import com.example.development.sakaiclientandroid.api_models.all_sites.SiteCollection;
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
    private String COOKIE_URL_3;


    OkHttpClient httpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BASE_URL = getString(R.string.BASE_URL);
        COOKIE_URL_1 = getString(R.string.COOKIE_URL_1);
        COOKIE_URL_2 = getString(R.string.COOKIE_URL_2);
        COOKIE_URL_3 = getString(R.string.COOKIE_URL_3);

        // Create the custom OkHttpClient with the interceptor to inject
        // cookies into every request
        HeaderInterceptor interceptor = new HeaderInterceptor(COOKIE_URL_1,
                COOKIE_URL_2, COOKIE_URL_3);
        httpClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        // The Retrofit instance allows us to construct our own services
        // that will make network requests
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();

        SakaiService sakaiService = retrofit.create(SakaiService.class);
        Call<AllSites> fetchSitesCall = sakaiService.getAllSites();
        fetchSitesCall.enqueue(new Callback<AllSites>() {
            @Override
            public void onResponse(Call<AllSites> call, Response<AllSites> response) {
                Log.i("Response", "SUCCESS!");
                Log.i("Status Code", "" + response.code());

                AllSites allSites = response.body();
                Log.i("Sites", allSites.toString());
                if(allSites.getSiteCollection().size() == 0) {
                    Log.i("List size", "no sites");
                } else {
                    for(SiteCollection site : allSites.getSiteCollection()) {
                        Log.i("Site", site.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<AllSites> call, Throwable t) {
                Log.i("Response", "failure");
            }
        });
    }
}
