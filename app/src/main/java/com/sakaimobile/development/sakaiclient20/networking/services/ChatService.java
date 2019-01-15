package com.sakaimobile.development.sakaiclient20.networking.services;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ChatService {

    @FormUrlEncoded
    @POST("chat-message/new")
    Single<ResponseBody> postChatMessage(
            @Field("chatChannelId") String chatChannelId,
            @Field("body") String body,
            @Field("csrftoken") String csrftoken
    );

}
