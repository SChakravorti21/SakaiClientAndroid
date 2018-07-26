package com.example.development.sakaiclientandroid.services;

import com.example.development.sakaiclientandroid.api_models.assignments.AllAssignments;
import com.example.development.sakaiclientandroid.api_models.gradebook.AllGradesPost;
import com.example.development.sakaiclientandroid.api_models.gradebook.GradebookCollectionObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Development on 5/12/18.
 */

public interface SakaiService {

    @GET("site.json")
    Call<ResponseBody> getAllSites();

    @GET("gradebook/my.json")
    Call<AllGradesPost> getAllGrades();

    @GET("gradebook/site/{site_id}.json")
    Call<GradebookCollectionObject> getGradeForSite(@Path(value = "site_id", encoded = true) String siteId);

    @GET("assignment/my.json")
    Call<AllAssignments> getAllAssignments();

    @GET("assignment/site/{site_id}.json")
    Call<AllAssignments> getSiteAssignments(@Path(value = "site_id", encoded = true) String siteId);
}
