package com.sakaimobile.development.sakaiclient20.networking.services;

import com.sakaimobile.development.sakaiclient20.models.sakai.User.UserResponse;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.GET;

public interface SessionService {

    @GET("session/current.json")
    Single<UserResponse> getLoggedInUser();

    @GET("/portal")
    Single<ResponseBody> refreshSession();
}
