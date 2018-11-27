package com.example.development.sakaiclient20.dependency_injection;

import com.example.development.sakaiclient20.repositories.CourseRepository;
import com.example.development.sakaiclient20.ui.viewmodels.CourseViewModel;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = { RepositoryModule.class })
class ViewModelModule {

    @Provides
    static CourseViewModel provideCourseViewModel(CourseRepository courseRepository) {
        return new CourseViewModel(courseRepository);
    }

}
