package com.example.development.sakaiclient20.dependency_injection;

import com.example.development.sakaiclient20.networking.services.AssignmentsService;
import com.example.development.sakaiclient20.networking.services.CoursesService;
import com.example.development.sakaiclient20.networking.services.GradesService;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module(includes = NetworkingModule.class)
class ServiceModule {

    @Provides
    static CoursesService provideCoursesService(@Named("course_retrofit") Retrofit retrofit) {
        return retrofit.create(CoursesService.class);
    }

    @Provides
    static AssignmentsService assignmentsService(@Named("assignment_retrofit") Retrofit retrofit) {
        return retrofit.create(AssignmentsService.class);
    }

    @Provides
    static GradesService gradesService(@Named("grades_retrofit") Retrofit retrofit) {
        return retrofit.create(GradesService.class);
    }

}
