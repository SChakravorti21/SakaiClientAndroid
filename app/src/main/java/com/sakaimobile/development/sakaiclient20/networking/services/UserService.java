package com.sakaimobile.development.sakaiclient20.networking.services;

import com.sakaimobile.development.sakaiclient20.models.sakai.User.UserResponse;

import io.reactivex.Single;
import retrofit2.http.GET;

public interface UserService {

    @GET("user/current.json")
    Single<UserResponse> getLoggedInUser();
}
