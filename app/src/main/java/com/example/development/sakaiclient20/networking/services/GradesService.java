package com.example.development.sakaiclient20.networking.services;

import com.example.development.sakaiclient20.models.sakai.gradebook.GradeCollection;
import com.example.development.sakaiclient20.models.sakai.gradebook.GradesResponse;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Shoumyo Chakravorti on 8/5/18.
 */

public interface GradesService {

    @GET("gradebook/my.json")
    Single<GradesResponse> getAllGrades();

    @GET("gradebook/site/{site_id}.json")
    Single<GradeCollection> getGradeForSite(@Path(value = "site_id", encoded = true) String siteId);

}
