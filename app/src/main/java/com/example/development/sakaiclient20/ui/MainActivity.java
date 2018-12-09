package com.example.development.sakaiclient20.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.example.development.sakaiclient20.R;
import com.example.development.sakaiclient20.networking.utilities.SharedPrefsUtil;
import com.example.development.sakaiclient20.persistence.entities.Announcement;
import com.example.development.sakaiclient20.persistence.entities.Course;
import com.example.development.sakaiclient20.ui.fragments.AllCoursesFragment;
import com.example.development.sakaiclient20.ui.fragments.AnnouncementsFragment;
import com.example.development.sakaiclient20.ui.fragments.CourseSitesFragment;
import com.example.development.sakaiclient20.ui.fragments.SingleAnnouncementFragment;
import com.example.development.sakaiclient20.ui.helpers.BottomNavigationViewHelper;
import com.example.development.sakaiclient20.ui.listeners.OnActionPerformedListener;
import com.example.development.sakaiclient20.ui.viewmodels.AnnouncementViewModel;
import com.example.development.sakaiclient20.ui.viewmodels.CourseViewModel;
import com.example.development.sakaiclient20.ui.viewmodels.ViewModelFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener,
        HasSupportFragmentInjector, OnActionPerformedListener {

    @Inject
    DispatchingAndroidInjector<Fragment> supportFragmentInjector;

    public static final String ALL_COURSES_TAG = "ALL_COURSES";
    public static final String COURSE_TAG = "COURSE";
    public static final String ALL_GRADES_TAG = "GRADES";
    public static final String ASSIGNMENTS_TAG = "ASSIGNMENTS";
    public static final String SITE_GRADES_TAG = "SITE_GRADES";

    public static final int NUM_ANNOUNCEMENTS_DEFAULT = 5;

    private static final short FRAGMENT_REPLACE = 0;
    private static final short FRAGMENT_ADD = 1;


    private FrameLayout container;
    private ProgressBar spinner;
    public boolean isLoadingAllCourses;

    @Inject
    CourseViewModel courseViewModel;

    @Inject
    ViewModelFactory viewModelFactory;
    private List<LiveData> beingObserved;

    /******************************\
     LIFECYCLE/INTERFACE METHODS
     \******************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get reference to the container
        this.container = findViewById(R.id.fragment_container);

        //starts spinner
        this.spinner = findViewById(R.id.nav_activity_progressbar);
        this.spinner.setVisibility(View.VISIBLE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
        BottomNavigationViewHelper.removeShiftMode(navigation);

        //clear the saved tree states in saved preferences so some nodes aren't opened by default
        SharedPrefsUtil.clearTreeStates(this);

        // Request all site pages for the Home Fragment and then loads the fragment
        //refresh since we are loading for the same time
        beingObserved = new ArrayList<>();
        loadHomeFragment();
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return supportFragmentInjector;
    }

    @Override
    protected void onPause() {
        super.onPause();
        removeObservations();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() == 0) {
            setActionBarTitle(getString(R.string.app_name));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_nav_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * When an item on the navigation bar is selected, creates the respective fragment
     * and then loads the fragment into the Frame Layout. For the AllCoursesFragment, we have to
     * put the responseBody of the request into the bundle and give it to the fragment, so that
     * the fragment has data to display all the site collections.
     *
     * @param item = selected item on nav bar
     * @return whether the fragment was successfully loaded.
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        //if we are loading all courses, don't allow user to click any navigation item
        if (isLoadingAllCourses)
            return false;

        // To be safe, remove any observations that might be active for the previous tab
        // since that might trigger an unwanted fragment transaction
        removeObservations();
        switch (item.getItemId()) {
            case R.id.navigation_home:
                loadHomeFragment();
                return true;
            case R.id.navigation_announcements:
                loadAnnouncementsFragment();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onCourseSelected(String siteId) {
        LiveData<Course> courseLiveData = ViewModelProviders.of(this, viewModelFactory)
                .get(CourseViewModel.class)
                .getCourse(siteId);
        beingObserved.add(courseLiveData);
        courseLiveData.observe(this, course -> {
            CourseSitesFragment fragment = CourseSitesFragment.newInstance(course, this);
            loadFragment(fragment, FRAGMENT_REPLACE, true, true);
            setActionBarTitle(course.title);
        });
    }


    @Override
    public void onAnnouncementSelected(Announcement announcement, Map<String, Course> siteIdToCourse) {
        Bundle b = new Bundle();
        b.putSerializable(getString(R.string.single_announcement_tag), announcement);
        // for some reason map isn't serializable, so i had to cast to hashmap
        //TODO check before casting
        b.putSerializable(getString(R.string.siteid_to_course_map), (HashMap)siteIdToCourse);
        b.putSerializable(getString(R.string.siteid_to_course_map), (HashMap)siteIdToCourse);

        SingleAnnouncementFragment fragment = new SingleAnnouncementFragment();
        fragment.setArguments(b);

        loadFragment(fragment, FRAGMENT_ADD, true, R.anim.grow_enter, R.anim.pop_exit);
    }

    @Override
    public void onSiteAnnouncementsSelected(Course course) {

        LiveData<List<Announcement>> siteAnnouncementsLiveData =
                ViewModelProviders.of(this, viewModelFactory)
                .get(AnnouncementViewModel.class)
                .getSiteAnnouncements(course.siteId, NUM_ANNOUNCEMENTS_DEFAULT);

        beingObserved.add(siteAnnouncementsLiveData);
        // observe the site announcements list,
        // make new fragment if it is updated.
        siteAnnouncementsLiveData.observe(this, siteAnnouncements -> {

            HashMap<String, Course> siteIdToCourse = new HashMap<>();
            siteIdToCourse.put(course.siteId, course);

            AnnouncementsFragment frag =
                    prepareAnnouncementsFragment(siteAnnouncements, siteIdToCourse, AnnouncementsFragment.SITE_ANNOUNCEMENTS);

            loadFragment(frag, FRAGMENT_REPLACE, true, true);
            container.setVisibility(View.VISIBLE);

            // TODO use proper string resource
            String actionBarTitle = String.format("%s: %s", getString(R.string.announcements_site), course.title);
            setActionBarTitle(actionBarTitle);
        });

    }

    /*******************************\
     LIFECYCLE CONVENIENCE METHODS
     \*******************************/

    private void removeObservations() {
        for (LiveData liveData : beingObserved) {
            liveData.removeObservers(this);
        }
    }

    /******************************\
     FRAGMENT MANAGEMENT
     \******************************/

    /**
     * Loads a given fragment into the fragment container in the NavActivity layout
     *
     * @param fragment
     * @return boolean whether the fragment was successfully loaded
     */
    private boolean loadFragment(Fragment fragment, int replace, boolean addToBackStack, boolean showAnimations) {
        if (showAnimations)
            return loadFragment(fragment, replace, addToBackStack, R.anim.enter, R.anim.exit);
        else
            return loadFragment(fragment, replace, addToBackStack, -1, -1);
    }

    /**
     * Loads a given fragment into the fragment container in the NavActivity layout
     *
     * @param fragment
     * @return boolean whether the fragment was successfully loaded
     */
    private boolean loadFragment(Fragment fragment, int replace, boolean addToBackStack, int animEnter, int animExit) {
        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            if (animEnter > 0 && animExit > 0)
                transaction.setCustomAnimations(animEnter, animExit, R.anim.pop_enter, R.anim.pop_exit);
            if (addToBackStack)
                transaction.addToBackStack(null);

            if(replace == FRAGMENT_REPLACE)
                transaction.replace(R.id.fragment_container, fragment).commit();
            else if(replace == FRAGMENT_ADD)
                transaction.add(R.id.fragment_container, fragment).commit();
            else
                return false;

            return true;
        }

        return false;
    }

    /**
     * Loads the all courses fragment (home page)
     */
    public void loadHomeFragment() {
        this.container.setVisibility(View.GONE);
        startProgressBar();
        isLoadingAllCourses = true;

        LiveData<List<List<Course>>> courseLiveData =
                ViewModelProviders.of(this, viewModelFactory)
                        .get(CourseViewModel.class)
                        .getCoursesByTerm();
        beingObserved.add(courseLiveData);
        courseLiveData.observe(this, courses -> {
            stopProgressBar();

            AllCoursesFragment coursesFragment = AllCoursesFragment.newInstance(courses, this);
            loadFragment(coursesFragment, FRAGMENT_REPLACE, false, false);
            container.setVisibility(View.VISIBLE);

            setActionBarTitle(getString(R.string.app_name));
            isLoadingAllCourses = false;
        });
    }

    /**
     * Loads the all announcements fragment by obser
     * ving on the live data from the
     * announcements view model
     *
     * whenever an update is detected in the live data, recreate the fragment
     * TODO: possibly dont recreate the fragment, just recreate the view
     */
    public void loadAnnouncementsFragment() {
        this.container.setVisibility(View.GONE);
        startProgressBar();

        // get the announcements to show
        LiveData<List<Announcement>> announcementsLiveData =
                ViewModelProviders.of(this, viewModelFactory)
                .get(AnnouncementViewModel.class)
                .getAllAnnouncements(NUM_ANNOUNCEMENTS_DEFAULT);

        // get our courses so we can create a map from siteId to course
        // which the announcement adapter needs
        LiveData<List<List<Course>>> coursesLiveData =
                ViewModelProviders.of(this, viewModelFactory)
                .get(CourseViewModel.class)
                .getCoursesByTerm();

        beingObserved.add(announcementsLiveData);
        beingObserved.add(coursesLiveData);

        // TODO : make it so that both are concurrent
        announcementsLiveData.observe(this, announcements -> {
            coursesLiveData.observe(this, courses -> {

                HashMap<String, Course> siteIdToCourse = createSiteIdToCourseMap(courses);

                stopProgressBar();

               AnnouncementsFragment frag =
                       prepareAnnouncementsFragment(announcements, siteIdToCourse, AnnouncementsFragment.ALL_ANNOUNCEMENTS);

                // load the fragment onto the screen with a replace
                loadFragment(frag, FRAGMENT_REPLACE, false, false);
                container.setVisibility(View.VISIBLE);
                setActionBarTitle(getString(R.string.announcements));
            });
        });



    }


    /******************************\
     CONVENIENCE METHODS
     \******************************/

    /**
     * Prepares an announcements fragment for all announcements or site announcements
     * @param announcements list of announcements to show in frag
     * @param siteIdToCourse hashmap mapping from siteIdToCourses, needed in the adapter
     * @param announcementType type of announcement to show, all or site
     * @return created announcements fragment
     */
    private AnnouncementsFragment prepareAnnouncementsFragment(
            List<Announcement> announcements,
            HashMap<String, Course> siteIdToCourse,
            int announcementType) {

        // create the fragment and set its arguments
        AnnouncementsFragment announcementsFragment = new AnnouncementsFragment();

        Bundle b = new Bundle();
        b.putInt(getString(R.string.announcement_type), announcementType);

        //TODO check before casting to arraylist
        b.putSerializable(getString(R.string.all_announcements_tag), (ArrayList)announcements);
        b.putSerializable(getString(R.string.siteid_to_course_map), siteIdToCourse);
        b.putSerializable(getString(R.string.action_listener_tag), this);
        announcementsFragment.setArguments(b);

        return announcementsFragment;
    }

    private HashMap<String, Course> createSiteIdToCourseMap(List<List<Course>> courses) {

        HashMap<String, Course> siteIdToCourse = new HashMap<>();

        for(List<Course> term : courses) {
            for(Course course : term) {
                siteIdToCourse.put(course.siteId, course);
            }
        }
        return siteIdToCourse;
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    public void startProgressBar() {
        spinner.setVisibility(View.VISIBLE);
    }

    public void stopProgressBar() {
        spinner.setVisibility(View.GONE);
    }
}
