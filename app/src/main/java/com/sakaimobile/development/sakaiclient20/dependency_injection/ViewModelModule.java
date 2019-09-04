package com.sakaimobile.development.sakaiclient20.dependency_injection;

import com.sakaimobile.development.sakaiclient20.ui.viewmodels.AnnouncementViewModel;
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.AssignmentViewModel;
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.CourseViewModel;
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.GradeViewModel;
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.LoadingPageViewModel;
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.ResourceViewModel;
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.ViewModelFactory;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
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
    @ViewModelKey(ResourceViewModel.class)
    abstract ViewModel bindResourceViewModel(ResourceViewModel resourceViewModel);

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
    @IntoMap
    @ViewModelKey(LoadingPageViewModel.class)
    abstract ViewModel bindLoadingPageViewModel(LoadingPageViewModel loadingPageViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);

}
