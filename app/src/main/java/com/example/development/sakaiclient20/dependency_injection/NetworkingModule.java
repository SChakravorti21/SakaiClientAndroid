package com.example.development.sakaiclient20.dependency_injection;

import android.content.Context;

import com.example.development.sakaiclient20.R;
import com.example.development.sakaiclient20.networking.deserializers.CourseDeserializer;
import com.example.development.sakaiclient20.networking.utilities.HeaderInterceptor;
import com.example.development.sakaiclient20.persistence.entities.Course;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(includes = DeserializerModule.class)
class NetworkingModule {

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
