package com.sakaimobile.development.sakaiclient20.dependency_injection;

import android.content.Context;

import com.google.gson.Gson;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.sakaimobile.development.sakaiclient20.R;
import com.sakaimobile.development.sakaiclient20.networking.utilities.HeaderInterceptor;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(includes = DeserializerModule.class)
class NetworkingModule {

    @Named("default_retrofit")
    @Provides
    Retrofit defaultRetrofit(Retrofit.Builder builder) {
        return builder
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Named("course_retrofit")
    @Provides
    Retrofit courseRetrofit(Retrofit.Builder builder,
                            @Named("course_deserializer") Gson courseDeserializer) {
        return builder
                .addConverterFactory(GsonConverterFactory.create(courseDeserializer))
                .build();
    }


    @Named("assignment_retrofit")
    @Provides
    Retrofit assignmentRetrofit(Retrofit.Builder builder,
                                @Named("assignment_deserializer") Gson assignmentDeserializer,
                                @Named("attachment_deserializer") Gson attachmentDeserializer) {
        return builder
                .addConverterFactory(GsonConverterFactory.create(assignmentDeserializer))
                .addConverterFactory(GsonConverterFactory.create(attachmentDeserializer))
                .build();
    }


    @Named("grades_retrofit")
    @Provides
    Retrofit gradesRetrofit(Retrofit.Builder builder,
                            @Named("grades_deserializer") Gson gradesDeserializer) {
        return builder
                .addConverterFactory(GsonConverterFactory.create(gradesDeserializer))
                .build();
    }

    @Named("announcement_retrofit")
    @Provides
    Retrofit announcementRetrofit(Retrofit.Builder builder,
                                  @Named("announcement_deserializer") Gson announcementDeserializer,
                                  @Named("attachment_deserializer") Gson attachmentDeserializer) {
        return builder
                .addConverterFactory(GsonConverterFactory.create(announcementDeserializer))
                .addConverterFactory(GsonConverterFactory.create(attachmentDeserializer))
                .build();
    }


    @Provides
    Retrofit.Builder builder(Context context, OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(context.getString(R.string.BASE_URL))
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
    }

    @Provides
    OkHttpClient okHttpClient(HeaderInterceptor interceptor) {
        return new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();
    }

    @Provides
    HeaderInterceptor headerInterceptor(Context context) {
        return new HeaderInterceptor(context.getString(R.string.COOKIE_URL_1));
    }

}
