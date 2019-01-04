package com.sakaimobile.development.sakaiclient20.networking.services;

import com.sakaimobile.development.sakaiclient20.models.sakai.resources.ResourcesResponse;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ResourcesService {

    @GET("content/site/{site_id}.json")
    Single<ResourcesResponse> getSiteResources(@Path(value = "site_id", encoded = true) String siteId);
}
