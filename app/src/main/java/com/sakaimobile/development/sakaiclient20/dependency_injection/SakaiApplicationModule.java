package com.sakaimobile.development.sakaiclient20.dependency_injection;

import com.sakaimobile.development.sakaiclient20.ui.activities.MainActivity;
import com.sakaimobile.development.sakaiclient20.ui.activities.SitePageActivity;
import com.sakaimobile.development.sakaiclient20.ui.fragments.AllCoursesFragment;
import com.sakaimobile.development.sakaiclient20.ui.fragments.AllGradesFragment;
import com.sakaimobile.development.sakaiclient20.ui.fragments.AnnouncementsFragment;
import com.sakaimobile.development.sakaiclient20.ui.activities.LoadingActivity;
import com.sakaimobile.development.sakaiclient20.ui.fragments.SiteChatFragment;
import com.sakaimobile.development.sakaiclient20.ui.fragments.assignments.AssignmentsFragment;
import com.sakaimobile.development.sakaiclient20.ui.fragments.SiteGradesFragment;
import com.sakaimobile.development.sakaiclient20.ui.fragments.SiteResourcesFragment;
import com.sakaimobile.development.sakaiclient20.ui.fragments.assignments.SiteAssignmentsFragment;

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
    abstract SitePageActivity contributeSitePageActivityInjector();

    @ContributesAndroidInjector
    abstract LoadingActivity contributeLoadingActivityInjector();

    @ContributesAndroidInjector
    abstract AllCoursesFragment contributesFragmentInjector();

    @ContributesAndroidInjector
    abstract AnnouncementsFragment contributesAnnouncementInjector();

    @ContributesAndroidInjector
    abstract AllGradesFragment contributesAllGradesFragmentInjector();

    @ContributesAndroidInjector
    abstract AssignmentsFragment contributesAssignmentsFragmentInjector();

    @ContributesAndroidInjector
    abstract SiteResourcesFragment contributeSiteResourcesFragmentInjector();

    @ContributesAndroidInjector
    abstract SiteGradesFragment contributesSiteGradesFragmentInjector();

    @ContributesAndroidInjector
    abstract SiteAssignmentsFragment contributesSiteAssignmentsFragmentInjector();

    @ContributesAndroidInjector
    abstract SiteChatFragment contributesSiteChatFragmentInjector();
}
