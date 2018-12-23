package com.sakaimobile.development.sakaiclient20.dependency_injection;

import com.sakaimobile.development.sakaiclient20.networking.services.AnnouncementsService;
import com.sakaimobile.development.sakaiclient20.networking.services.AssignmentsService;
import com.sakaimobile.development.sakaiclient20.networking.services.CoursesService;
import com.sakaimobile.development.sakaiclient20.networking.services.GradeService;
import com.sakaimobile.development.sakaiclient20.networking.services.ResourcesService;
import com.sakaimobile.development.sakaiclient20.networking.services.UserService;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module(includes = NetworkingModule.class)
class ServiceModule {

    @Provides
    UserService provideUserService(@Named("default_retrofit") Retrofit retrofit) {
        return retrofit.create(UserService.class);
    }

    @Provides
    static ResourcesService provideResourcesService(@Named("resource_retrofit") Retrofit retrofit) {
        return retrofit.create(ResourcesService.class);
    }

    @Provides
    static CoursesService provideCoursesService(@Named("course_retrofit") Retrofit retrofit) {
        return retrofit.create(CoursesService.class);
    }

    @Provides
    static AssignmentsService assignmentsService(@Named("assignment_retrofit") Retrofit retrofit) {
        return retrofit.create(AssignmentsService.class);
    }

    @Provides
    static AnnouncementsService announcementsService(@Named("announcement_retrofit") Retrofit retrofit) {
        return retrofit.create(AnnouncementsService.class);
    }

    @Provides
    static GradeService gradesService(@Named("grades_retrofit") Retrofit retrofit) {
        return retrofit.create(GradeService.class);
    }

}
