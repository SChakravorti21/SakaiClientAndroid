package com.example.development.sakaiclientandroid.services;

import com.example.development.sakaiclientandroid.api_models.all_sites.AllSitesAPI;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Development on 5/12/18.
 */

public interface SakaiService {

    @GET("site.json")
    Call<AllSitesAPI> getAllSites();
}
