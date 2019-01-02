package com.sakaimobile.development.sakaiclient20.ui.activities;

import android.app.DownloadManager;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.sakaimobile.development.sakaiclient20.R;
import com.sakaimobile.development.sakaiclient20.networking.utilities.SharedPrefsUtil;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Announcement;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Course;
import com.sakaimobile.development.sakaiclient20.ui.custom_components.CustomLinkMovementMethod;
import com.sakaimobile.development.sakaiclient20.ui.custom_components.DownloadCompleteReceiver;
import com.sakaimobile.development.sakaiclient20.ui.fragments.AllCoursesFragment;
import com.sakaimobile.development.sakaiclient20.ui.fragments.AllGradesFragment;
import com.sakaimobile.development.sakaiclient20.ui.fragments.AnnouncementsFragment;
import com.sakaimobile.development.sakaiclient20.ui.fragments.CourseSitesFragment;
import com.sakaimobile.development.sakaiclient20.ui.fragments.SingleAnnouncementFragment;
import com.sakaimobile.development.sakaiclient20.ui.fragments.assignments.AssignmentsFragment;
import com.sakaimobile.development.sakaiclient20.ui.helpers.BottomNavigationViewHelper;
import com.sakaimobile.development.sakaiclient20.ui.listeners.OnActionPerformedListener;
import com.sakaimobile.development.sakaiclient20.ui.listeners.OnAnnouncementSelected;
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.AnnouncementViewModel;
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.AssignmentViewModel;
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.CourseViewModel;
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.GradeViewModel;
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.ViewModelFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

import static com.sakaimobile.development.sakaiclient20.ui.fragments.AnnouncementsFragment.NUM_ANNOUNCEMENTS_DEFAULT;

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener,
        OnActionPerformedListener, OnAnnouncementSelected {


    @Inject
    ViewModelFactory viewModelFactory;


    protected Set<LiveData> beingObserved;

    public static final String ASSIGNMENTS_TAG = "ASSIGNMENTS";

    private static final short FRAGMENT_REPLACE = 0;
    private static final short FRAGMENT_ADD = 1;


    private FrameLayout container;
    private ProgressBar spinner;
    private boolean isLoadingAllCourses;

    private Fragment displayingFragment;

    /******************************\
     LIFECYCLE/INTERFACE METHODS
     \******************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerDownloadReceiver();

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
        beingObserved = new HashSet<>();
        loadCoursesFragment(true);
    }

//    private void logUserInfo() {
//        userService
//                .getLoggedInUser()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(userResponse -> {
//                    Log.d("LOG", "logging user in crashlytics...");
//                    Crashlytics.setUserEmail(userResponse.email);
//                    Crashlytics.setUserName(userResponse.displayName);
//                });
//    }

    @Override
    protected void onResume() {
        super.onResume();
        CustomLinkMovementMethod.setFragmentManager(getSupportFragmentManager());
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
        setActionBarTitle(getString(R.string.app_name));
        switch (item.getItemId()) {
            case R.id.navigation_home:
                loadCoursesFragment(false);
                return true;
            case R.id.navigation_assignments:
                loadAssignmentsFragment(true, true);
                return true;
            case R.id.navigation_announcements:
                loadAnnouncementsFragment();
                return true;
            case R.id.navigation_gradebook:
                loadGradesFragment();
                return true;
            default:
                return false;
        }
    }


    /******************************\
     INTERFACE IMPLEMENTATIONS
     \******************************/


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
        b.putSerializable(getString(R.string.siteid_to_course_map), (HashMap) siteIdToCourse);

        SingleAnnouncementFragment fragment = new SingleAnnouncementFragment();
        fragment.setArguments(b);

        loadFragment(fragment, FRAGMENT_ADD, true, R.anim.grow_enter, R.anim.pop_exit);
    }


    /*******************************\
     LIFECYCLE CONVENIENCE METHODS
     \*******************************/

    public void registerDownloadReceiver() {
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        DownloadCompleteReceiver receiver = new DownloadCompleteReceiver();
        registerReceiver(receiver, filter);
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
                transaction.addToBackStack(fragment.getClass().getCanonicalName());

            if (replace == FRAGMENT_REPLACE)
                transaction.replace(R.id.fragment_container, fragment).commit();
            else if (replace == FRAGMENT_ADD)
                transaction.add(R.id.fragment_container, fragment).commit();
            else
                return false;

            displayingFragment = fragment;
            return true;
        }

        return false;
    }


    /**
     * pops the fragment backstacak until a given fragment
     *
     * @param name name of fragment to pop until
     */
    private void popBackStackUntil(String name) {
        getSupportFragmentManager().popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    /**
     * Loads the all courses fragment (home page)
     */
    public void loadCoursesFragment(boolean refresh) {
        this.container.setVisibility(View.GONE);
        startProgressBar();
        isLoadingAllCourses = true;

        LiveData<List<List<Course>>> courseLiveData = ViewModelProviders.of(this, viewModelFactory)
                .get(CourseViewModel.class)
                .getCoursesByTerm(refresh);


        courseLiveData.observe(this, courses -> {
            stopProgressBar();

            AllCoursesFragment coursesFragment = AllCoursesFragment.newInstance(courses, this);
            loadFragment(coursesFragment, FRAGMENT_REPLACE, false, false);
            container.setVisibility(View.VISIBLE);

            isLoadingAllCourses = false;
            courseLiveData.removeObservers(this);

            if (refresh)
                makeToast("Successfully refreshed courses", Toast.LENGTH_SHORT);
        });
    }

    /**
     * Loads all assignments tab
     */
    public void loadAssignmentsFragment(boolean sortedByCourses, boolean refresh) {

        this.container.setVisibility(View.GONE);
        this.spinner.setVisibility(View.VISIBLE);

        LiveData<List<List<Course>>> courseLiveData = ViewModelProviders.of(this, viewModelFactory)
                .get(AssignmentViewModel.class)
                .getCoursesByTerm(refresh);

        courseLiveData.observe(this, courses -> {
            spinner.setVisibility(View.GONE);

            Bundle bundle = new Bundle();
            bundle.putSerializable(ASSIGNMENTS_TAG, (Serializable) courses);
            bundle.putBoolean(AssignmentsFragment.ASSIGNMENTS_SORTED_BY_COURSES, sortedByCourses);

            AssignmentsFragment fragment = new AssignmentsFragment();
            fragment.setArguments(bundle);
            loadFragment(fragment, FRAGMENT_REPLACE, false, false);

            container.setVisibility(View.VISIBLE);
            courseLiveData.removeObservers(this);

            if (refresh)
                makeToast("Successfully refreshed assignments", Toast.LENGTH_SHORT);
        });
    }

    /**
     * Loads the all announcements fragment by obser
     * ving on the live data from the
     * announcements view model
     * <p>
     * whenever an update is detected in the live data, recreate the fragment
     */
    public void loadAnnouncementsFragment() {
        this.container.setVisibility(View.GONE);
        startProgressBar();

        AnnouncementViewModel announcementViewModel = ViewModelProviders.of(this, viewModelFactory).get(AnnouncementViewModel.class);
        CourseViewModel courseViewModel = ViewModelProviders.of(this, viewModelFactory).get(CourseViewModel.class);


        LiveData<List<Announcement>> announcementsLiveData = announcementViewModel
                        .getAllAnnouncements(NUM_ANNOUNCEMENTS_DEFAULT);

        LiveData<List<List<Course>>> coursesLiveData = courseViewModel
                        .getCoursesByTerm(false);


        // announcements fragment will be observing on announcement live data
        // here we are observing on courselivedata
        beingObserved.add(announcementsLiveData);
        beingObserved.add(coursesLiveData);

        coursesLiveData.observe(this, courses -> {

            HashMap<String, Course> map = createSiteIdToCourseMap(courses);

            // create fragment arguments
            Bundle b = new Bundle();
            b.putString(getString(R.string.siteid_tag), null);
            b.putSerializable(getString(R.string.siteid_to_course_map), map);

            // create and load the fragment
            AnnouncementsFragment frag = new AnnouncementsFragment();
            frag.setArguments(b);

            loadFragment(frag, FRAGMENT_REPLACE, false, false);

            this.container.setVisibility(View.VISIBLE);
            stopProgressBar();
        });


    }


    /**
     * Loads the all grades fragment
     */
    public void loadGradesFragment() {
        this.container.setVisibility(View.GONE);
        startProgressBar();


        LiveData<List<List<Course>>> courseLiveData =
                ViewModelProviders.of(this, viewModelFactory)
                        .get(GradeViewModel.class)
                        .getCoursesByTerm(true);

        beingObserved.add(courseLiveData);

        courseLiveData.observe(this, courses -> {
            stopProgressBar();

            AllGradesFragment gradesFragment = AllGradesFragment.newInstance(courses);
            loadFragment(gradesFragment, FRAGMENT_REPLACE, false, false);
            container.setVisibility(View.VISIBLE);

        });
    }


    /******************************\
     CONVENIENCE METHODS
     \******************************/

    private HashMap<String, Course> createSiteIdToCourseMap(List<List<Course>> courses) {

        HashMap<String, Course> siteIdToCourse = new HashMap<>();

        for (List<Course> term : courses) {
            for (Course course : term) {
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

    public void makeToast(String message, int duration) {
        Toast.makeText(this, message, duration).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        removeObservations();
    }

    protected void removeObservations() {
        for (LiveData liveData : beingObserved) {
            liveData.removeObservers(this);
        }
        beingObserved.clear();
    }
}
