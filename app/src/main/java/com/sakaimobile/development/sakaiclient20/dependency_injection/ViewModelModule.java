package com.sakaimobile.development.sakaiclient20.dependency_injection;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.sakaimobile.development.sakaiclient20.ui.viewmodels.AnnouncementViewModel;
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.AssignmentViewModel;
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.CourseViewModel;
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.GradeViewModel;
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.ViewModelFactory;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module(includes = { RepositoryModule.class })
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(CourseViewModel.class)
    abstract ViewModel bindCourseViewModel(CourseViewModel userViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(AssignmentViewModel.class)
    abstract ViewModel bindAssignmentViewModel(AssignmentViewModel assignmentViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(AnnouncementViewModel.class)
    abstract ViewModel bindAnnouncementViewModel(AnnouncementViewModel announcementViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(GradeViewModel.class)
    abstract ViewModel bindGradeViewModel(GradeViewModel gradeViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);

}
