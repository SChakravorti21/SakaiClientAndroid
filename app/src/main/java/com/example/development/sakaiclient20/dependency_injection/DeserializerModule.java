package com.example.development.sakaiclient20.dependency_injection;

import com.example.development.sakaiclient20.networking.deserializers.CourseDeserializer;
import com.example.development.sakaiclient20.persistence.entities.Course;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
class DeserializerModule {

    @Named("course_deserializer")
    @Provides
    Gson courseDeserializer() {
        return new GsonBuilder()
                .setLenient()
                .registerTypeAdapter(Course.class, new CourseDeserializer())
                .create();
    }

}
