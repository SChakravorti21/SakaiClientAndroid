package com.sakaimobile.development.sakaiclient20.dependency_injection;

import com.sakaimobile.development.sakaiclient20.ui.MainActivity;
import com.sakaimobile.development.sakaiclient20.ui.SiteResourcesActivity;
import com.sakaimobile.development.sakaiclient20.ui.fragments.AllCoursesFragment;
import com.sakaimobile.development.sakaiclient20.ui.fragments.AllGradesFragment;
import com.sakaimobile.development.sakaiclient20.ui.fragments.AnnouncementsFragment;

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
    abstract SiteResourcesActivity contributeSiteResourcesActivityInjector();

    @ContributesAndroidInjector
    abstract AllCoursesFragment contributesFragmentInjector();

    @ContributesAndroidInjector
    abstract AnnouncementsFragment contributesAnnouncementInjector();

    @ContributesAndroidInjector
    abstract AllGradesFragment contributesAllGradesFragmentInjector();
}
