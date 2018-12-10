package com.sakaimobile.development.sakaiclient20.networking.services;

import com.sakaimobile.development.sakaiclient20.models.sakai.courses.CoursesResponse;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Course;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Shoumyo Chakravorti on 8/5/18.
 */

public interface CoursesService {
    @GET("site.json")
    Single<CoursesResponse> getAllSites();

    @GET("site/{site_id}.json")
    Single<Course> getSite(@Path(value = "site_id", encoded = true) String siteId);
}
