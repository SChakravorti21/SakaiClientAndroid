package com.example.development.sakaiclientandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.development.sakaiclientandroid.api_models.all_sites.AllSitesAPI;
import com.example.development.sakaiclientandroid.api_models.all_sites.SiteCollectionAPI;
import com.example.development.sakaiclientandroid.models.SiteCollection;
import com.example.development.sakaiclientandroid.services.SakaiService;
import com.example.development.sakaiclientandroid.utils.HeaderInterceptor;

import java.util.ArrayList;

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
        Call<AllSitesAPI> fetchSitesCall = sakaiService.getAllSites();
        fetchSitesCall.enqueue(new Callback<AllSitesAPI>() {
            @Override
            public void onResponse(Call<AllSitesAPI> call, Response<AllSitesAPI> response) {
                Log.i("Response", "SUCCESS!");
                Log.i("Status Code", "" + response.code());

                AllSitesAPI allSitesAPI = response.body();

                if(allSitesAPI.getSiteCollectionAPI().size() == 0) {
                    Log.i("List size", "no sites");
                } else {
                    for(SiteCollectionAPI site : allSitesAPI.getSiteCollectionAPI()) {
                        Log.i("SiteCollectionAPI", site.toString());
                    }
                }


                ArrayList<SiteCollection> siteCollections = SiteCollection.convertApiToSiteCollection(allSitesAPI.getSiteCollectionAPI());


            }

            @Override
            public void onFailure(Call<AllSitesAPI> call, Throwable t) {
                Log.i("Response", "failure");
            }
        });
    }
}
