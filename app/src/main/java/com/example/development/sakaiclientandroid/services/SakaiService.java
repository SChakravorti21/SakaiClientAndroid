package com.example.development.sakaiclientandroid.services;

import com.example.development.sakaiclientandroid.api_models.gradebook.AllGradesObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Development on 5/12/18.
 */

public interface SakaiService {

    @GET("site.json")
    Call<AllSitesAPI> getAllSites();

    @GET("site.json")
    Call<ResponseBody> getResponseBody();

    @GET("gradebook/my.json")
    Call<AllGradesObject> getAllGrades();
}
