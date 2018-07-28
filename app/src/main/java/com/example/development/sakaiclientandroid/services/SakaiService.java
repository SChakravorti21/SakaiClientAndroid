package com.example.development.sakaiclientandroid.services;

import com.example.development.sakaiclientandroid.api_models.assignments.AllAssignments;
import com.example.development.sakaiclientandroid.api_models.gradebook.AllGradesPost;
import com.example.development.sakaiclientandroid.api_models.gradebook.GradebookCollectionObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Shoumyo Chakravorti on 5/12/18.
 *
 * Defines the interface that is used by
 * {@link com.example.development.sakaiclientandroid.utils.requests.RequestManager} to construct
 * a {@link retrofit2.Retrofit} instance that can make requests to the Sakai API.
 * To see all endpoints that the Sakai API offers, and details of the ones used by this
 * interface, visit the <a href="https://sakai.rutgers.edu/direct/describe" target="_blank">documentation</a>.
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
