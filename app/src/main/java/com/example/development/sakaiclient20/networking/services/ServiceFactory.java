package com.example.development.sakaiclient20.networking.services;

import android.content.Context;

import com.example.development.sakaiclient20.R;
import com.example.development.sakaiclient20.models.sakai.gradebook.SiteGrades;
import com.example.development.sakaiclient20.networking.deserializers.AssignmentDeserializer;
import com.example.development.sakaiclient20.networking.deserializers.AttachmentDeserializer;
import com.example.development.sakaiclient20.networking.deserializers.CourseDeserializer;
import com.example.development.sakaiclient20.networking.deserializers.SiteGradesDeserializer;
import com.example.development.sakaiclient20.networking.utilities.HeaderInterceptor;
import com.example.development.sakaiclient20.persistence.entities.Assignment;
import com.example.development.sakaiclient20.persistence.entities.Attachment;
import com.example.development.sakaiclient20.persistence.entities.Course;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Development on 8/5/18.
 */

public class ServiceFactory {

    public static <Service> Service getService(Context context, Class<Service> serviceClass) {
        // Get the base url for the Sakai API
        String baseUrl = context.getString(R.string.BASE_URL);
        // Get the url which has the relevant cookies for Sakai
        String cookieUrl = context.getString(R.string.COOKIE_URL_1);

        // Create the custom OkHttpClient with the interceptor to inject
        // cookies into every request
        HeaderInterceptor interceptor = new HeaderInterceptor(cookieUrl);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        // The Retrofit instance allows us to construct our own services
        // that will make network requests
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpClient);
        addConverterFactories(retrofitBuilder, serviceClass);

        return retrofitBuilder.build().create(serviceClass);
    }

    private static <Service> void addConverterFactories(Retrofit.Builder builder, Class<Service> serviceClass) {
        // The order in which the converter factories are added matters because
        // Gson will check all the converters IN THE ORDER THEY WERE ADDED when
        // trying to deserialize a JSON object
        if(serviceClass == AssignmentsService.class) {
            builder.addConverterFactory(GsonConverterFactory.create(getAssignmentDeserializer()));
            builder.addConverterFactory(GsonConverterFactory.create(getAttachmentDeserializer()));
        } else if(serviceClass == CoursesService.class) {
            builder.addConverterFactory(GsonConverterFactory.create(getCourseDeserializer()));
        } else if(serviceClass == GradesService.class) {
            builder.addConverterFactory(GsonConverterFactory.create(getGradeDeserializer()));
        }
    }

    private static Gson getCourseDeserializer() {
        return new GsonBuilder()
                .setLenient()
                .registerTypeAdapter(Course.class, new CourseDeserializer())
                .create();
    }

    private static Gson getAssignmentDeserializer() {
        return new GsonBuilder()
                .setLenient()
                .registerTypeAdapter(Assignment.class, new AssignmentDeserializer())
                .create();
    }

    private static Gson getAttachmentDeserializer() {
        return new GsonBuilder()
                .setLenient()
                .registerTypeAdapter(Attachment.class, new AttachmentDeserializer())
                .create();
    }

    private static Gson getGradeDeserializer() {
        return new GsonBuilder()
                .setLenient()
                .registerTypeAdapter(SiteGrades.class, new SiteGradesDeserializer())
                .create();
    }
}
