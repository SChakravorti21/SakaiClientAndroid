package com.sakaimobile.development.sakaiclient20.dependency_injection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sakaimobile.development.sakaiclient20.models.sakai.gradebook.SiteGrades;
import com.sakaimobile.development.sakaiclient20.networking.deserializers.AnnouncementDeserializer;
import com.sakaimobile.development.sakaiclient20.networking.deserializers.AssignmentDeserializer;
import com.sakaimobile.development.sakaiclient20.networking.deserializers.AttachmentDeserializer;
import com.sakaimobile.development.sakaiclient20.networking.deserializers.CourseDeserializer;
import com.sakaimobile.development.sakaiclient20.networking.deserializers.ResourceDeserializer;
import com.sakaimobile.development.sakaiclient20.networking.deserializers.SiteGradesDeserializer;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Announcement;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Assignment;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Attachment;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Course;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Resource;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
class DeserializerModule {

    @Named("course_deserializer")
    @Provides
    static Gson courseDeserializer() {
        return new GsonBuilder()
                .setLenient()
                .registerTypeAdapter(Course.class, new CourseDeserializer())
                .create();
    }


    @Named("resource_deserializer")
    @Provides
    static Gson resourceDeserializer() {
        return new GsonBuilder()
                .setLenient()
                .registerTypeAdapter(Resource.class, new ResourceDeserializer())
                .create();
    }

    @Named("assignment_deserializer")
    @Provides
    static Gson assignmentDeserializer() {
        return new GsonBuilder()
                .setLenient()
                .registerTypeAdapter(Assignment.class, new AssignmentDeserializer())
                .create();
    }

    @Named("attachment_deserializer")
    @Provides
    static Gson attachmentDeserializer() {
        return new GsonBuilder()
                .setLenient()
                .registerTypeAdapter(Attachment.class, new AttachmentDeserializer())
                .create();
    }

    @Named("grades_deserializer")
    @Provides
    static Gson gradesDeserializer() {
        return new GsonBuilder()
                .setLenient()
                .registerTypeAdapter(SiteGrades.class, new SiteGradesDeserializer())
                .create();
    }

    @Named("announcement_deserializer")
    @Provides
    static Gson announcementDeserializer() {
        return new GsonBuilder()
                .setLenient()
                .registerTypeAdapter(Announcement.class, new AnnouncementDeserializer())
                .create();
    }

}
