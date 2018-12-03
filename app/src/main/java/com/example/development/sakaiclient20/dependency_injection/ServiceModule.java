package com.example.development.sakaiclient20.dependency_injection;

import android.content.Context;

import com.example.development.sakaiclient20.networking.services.AssignmentsService;
import com.example.development.sakaiclient20.networking.services.CoursesService;
import com.example.development.sakaiclient20.networking.services.ServiceFactory;

import dagger.Module;
import dagger.Provides;

@Module
class ServiceModule {

    @Provides static CoursesService provideCoursesService(Context context) {
        return ServiceFactory.getService(context, CoursesService.class);
    }

    @Provides static AssignmentsService assignmentsService(Context context) {
        return ServiceFactory.getService(context, AssignmentsService.class);
    }

}
