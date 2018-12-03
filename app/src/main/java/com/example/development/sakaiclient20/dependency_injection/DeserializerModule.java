package com.example.development.sakaiclient20.dependency_injection;

import com.example.development.sakaiclient20.networking.deserializers.AssignmentDeserializer;
import com.example.development.sakaiclient20.networking.deserializers.AttachmentDeserializer;
import com.example.development.sakaiclient20.networking.deserializers.CourseDeserializer;
import com.example.development.sakaiclient20.persistence.entities.Assignment;
import com.example.development.sakaiclient20.persistence.entities.Attachment;
import com.example.development.sakaiclient20.persistence.entities.Course;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
class DeserializerModule {

    @Named("course_deserializer")
    @Provides Gson courseDeserializer() {
        return new GsonBuilder()
                .setLenient()
                .registerTypeAdapter(Course.class, new CourseDeserializer())
                .create();
    }

    @Named("assignment_deserializer")
    @Provides Gson assignmentDeserializer() {
        return new GsonBuilder()
                .setLenient()
                .registerTypeAdapter(Assignment.class, new AssignmentDeserializer())
                .create();
    }

    @Named("attachment_deserializer")
    @Provides Gson attachmentDeserializer() {
        return new GsonBuilder()
                .setLenient()
                .registerTypeAdapter(Attachment.class, new AttachmentDeserializer())
                .create();
    }

}
