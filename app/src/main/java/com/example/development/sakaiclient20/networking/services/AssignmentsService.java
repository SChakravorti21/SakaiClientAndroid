package com.example.development.sakaiclient20.networking.services;

import com.example.development.sakaiclient20.models.sakai.assignments.AssignmentsResponse;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Shoumyo Chakravorti on 8/5/18.
 */

public interface AssignmentsService {

    @GET("assignment/my.json")
    Single<AssignmentsResponse> getAllAssignments();

    @GET("assignment/site/{site_id}.json")
    Single<AssignmentsResponse> getSiteAssignments(@Path(value = "site_id", encoded = true) String siteId);

}
