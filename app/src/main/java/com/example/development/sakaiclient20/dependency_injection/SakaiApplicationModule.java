package com.example.development.sakaiclient20.dependency_injection;

import com.example.development.sakaiclient20.ui.MainActivity;
import com.example.development.sakaiclient20.ui.fragments.AllCoursesFragment;
import com.example.development.sakaiclient20.ui.fragments.AllGradesFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
abstract class SakaiApplicationModule {
    /**
     * Although Android Studio might complain that this abstract method
     * is not implemented or used anywhere, Dagger implements it for us
     * so that we can use the handy `AndroidInjector.inject(this)`
     * syntax inside the Activity.
     */
    @ContributesAndroidInjector
    abstract MainActivity contributeMainActivityInjector();

    @ContributesAndroidInjector
    abstract AllCoursesFragment contributesAllCoursesFragmentInjector();

    @ContributesAndroidInjector
    abstract AllGradesFragment contributesAllGradesFragmentInjector();
}
