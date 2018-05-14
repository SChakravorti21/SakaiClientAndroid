package com.example.development.sakaiclientandroid.services;

import com.example.development.sakaiclientandroid.api_models.all_sites.AllSites;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * Created by Development on 5/12/18.
 */

public interface SakaiService {

    @GET("site.json")
    Call<AllSites> getAllSites(@Header("Cookie") String cookies);
}
