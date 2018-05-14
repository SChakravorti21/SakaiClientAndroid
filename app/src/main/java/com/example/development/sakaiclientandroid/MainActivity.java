package com.example.development.sakaiclientandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.test.suitebuilder.annotation.Suppress;
import android.util.Log;

import com.example.development.sakaiclientandroid.api_models.all_sites.AllSites;
import com.example.development.sakaiclientandroid.api_models.all_sites.SiteCollection;
import com.example.development.sakaiclientandroid.services.SakaiService;
import com.example.development.sakaiclientandroid.utils.HeaderInterceptor;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private String baseUrl;
    private String cookieUrl;

    OkHttpClient httpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        baseUrl = getString(R.string.BASE_URL);
        cookieUrl = getString(R.string.COOKIE_URL_1);

        // Create the custom OkHttpClient with the interceptor to inject
        // cookies into every request
        HeaderInterceptor interceptor = new HeaderInterceptor(this, cookieUrl);
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
