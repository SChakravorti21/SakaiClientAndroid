package com.sakaimobile.development.sakaiclient20.ui.activities;

import android.app.DownloadManager;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.sakaimobile.development.sakaiclient20.R;
import com.sakaimobile.development.sakaiclient20.networking.services.SessionService;
import com.sakaimobile.development.sakaiclient20.networking.utilities.SharedPrefsUtil;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Announcement;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Assignment;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Course;
import com.sakaimobile.development.sakaiclient20.ui.custom_components.CustomLinkMovementMethod;
import com.sakaimobile.development.sakaiclient20.ui.custom_components.DownloadCompleteReceiver;
import com.sakaimobile.development.sakaiclient20.ui.fragments.AllCoursesFragment;
import com.sakaimobile.development.sakaiclient20.ui.fragments.AllGradesFragment;
import com.sakaimobile.development.sakaiclient20.ui.fragments.AnnouncementsFragment;
import com.sakaimobile.development.sakaiclient20.ui.fragments.CourseSitesFragment;
import com.sakaimobile.development.sakaiclient20.ui.fragments.SettingsFragment;
import com.sakaimobile.development.sakaiclient20.ui.fragments.SingleAnnouncementFragment;
import com.sakaimobile.development.sakaiclient20.ui.fragments.assignments.AssignmentsFragment;
import com.sakaimobile.development.sakaiclient20.ui.helpers.AssignmentSortingUtils;
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

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener,
        OnAnnouncementSelected {

    private static final short FRAGMENT_REPLACE = 0;
    private static final short FRAGMENT_ADD = 1;

    protected Set<LiveData> beingObserved;
    @Inject ViewModelFactory viewModelFactory;

    private FrameLayout container;
    private ProgressBar spinner;
    private Set<Class> refreshedFragments;

    @Inject CourseViewModel courseViewModel;
    DownloadCompleteReceiver downloadReceiver;

    //==============================
    // LIFECYCLE/INTERFACE METHODS
    //==============================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get reference to the container
        this.container = findViewById(R.id.fragment_container);

        //starts spinner
        this.spinner = findViewById(R.id.nav_activity_progressbar);
        this.spinner.setVisibility(View.GONE);

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
        refreshedFragments = new HashSet<>();
        loadCoursesFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerDownloadReceiver();
        CustomLinkMovementMethod.setFragmentManager(getSupportFragmentManager());
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(downloadReceiver);
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
        // To be safe, remove any observations that might be active for the previous tab
        // since that might trigger an unwanted fragment transaction
        removeObservations();
        setActionBarTitle(getString(R.string.app_name));
        switch (item.getItemId()) {
            case R.id.navigation_home:
                loadCoursesFragment();
                return true;
            case R.id.navigation_assignments:
                loadAssignmentsFragment();
                return true;
            case R.id.navigation_announcements:
                loadAnnouncementsFragment();
                return true;
            case R.id.navigation_gradebook:
                loadGradesFragment();
                return true;
            case R.id.navigation_settings:
                loadFragment(new SettingsFragment(), FRAGMENT_REPLACE, true, false);
                return true;
            default:
                return false;
        }
    }

    //============================
    // INTERFACE IMPLEMENTATIONS
    //============================


    @Override
    public void onAnnouncementSelected(Announcement announcement, Map<String, Course> siteIdToCourse) {
        Bundle b = new Bundle();
        b.putSerializable(getString(R.string.single_announcement_tag), announcement);
        // for some reason map isn't serializable, so i had to cast to HashMap
        //TODO check before casting
        b.putSerializable(getString(R.string.siteid_to_course_map), (HashMap) siteIdToCourse);

        SingleAnnouncementFragment fragment = new SingleAnnouncementFragment();
        fragment.setArguments(b);

        loadFragment(fragment, FRAGMENT_ADD, true, R.anim.grow_enter, R.anim.pop_exit);
    }

    //================================
    // LIFECYCLE CONVENIENCE METHODS
    //================================

    public void registerDownloadReceiver() {
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        this.downloadReceiver = new DownloadCompleteReceiver();
        registerReceiver(downloadReceiver, filter);
    }


    //=======================
    // FRAGMENT MANAGEMENT
    //=======================

    /**
     * Loads a given fragment into the fragment container in the NavActivity layout
     *
     * @param fragment the Fragment to make visible
     */
    private void loadFragment(Fragment fragment, int replace, boolean addToBackStack, boolean showAnimations) {
        if (showAnimations)
            loadFragment(fragment, replace, addToBackStack, R.anim.enter, R.anim.exit);
        else
            loadFragment(fragment, replace, addToBackStack, -1, -1);
    }

    /**
     * Loads a given fragment into the fragment container in the NavActivity layout
     *
     * @param fragment the Fragment to make visible
     */
    private void loadFragment(Fragment fragment, int replace, boolean addToBackStack, int animEnter, int animExit) {
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
        }
    }

    /**
     * Loads the all courses fragment (home page)
     */
    public void loadCoursesFragment() {
        Bundle bundle = new Bundle();
        boolean shouldRefresh = getAndUpdateRefreshedState(AllCoursesFragment.class);
        bundle.putBoolean(AllCoursesFragment.SHOULD_REFRESH, shouldRefresh);

        Fragment fragment = new AllCoursesFragment();
        fragment.setArguments(bundle);
        loadFragment(fragment, FRAGMENT_REPLACE, false, false);
    }

    /**
     * Loads all assignments tab
     */
    public void loadAssignmentsFragment() {
        Bundle bundle = new Bundle();
        boolean shouldRefresh = getAndUpdateRefreshedState(AssignmentsFragment.class);
        bundle.putBoolean(AssignmentsFragment.SHOULD_REFRESH, shouldRefresh);

        Fragment fragment = new AssignmentsFragment();
        fragment.setArguments(bundle);
        loadFragment(fragment, FRAGMENT_REPLACE, false, false);
    }

    /**
     * Loads the all grades fragment
     */
    public void loadGradesFragment() {
        Bundle bundle = new Bundle();
        boolean shouldRefresh = getAndUpdateRefreshedState(AllGradesFragment.class);
        bundle.putBoolean(AllGradesFragment.SHOULD_REFRESH, shouldRefresh);

        Fragment fragment = new AllGradesFragment();
        fragment.setArguments(bundle);
        loadFragment(fragment, FRAGMENT_REPLACE, false, false);
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

        LiveData<List<List<Course>>> coursesLiveData =
            ViewModelProviders.of(this, viewModelFactory).get(CourseViewModel.class)
                .getCoursesByTerm(false);

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

            coursesLiveData.removeObservers(this);
        });
    }

    //=======================
    // CONVENIENCE METHODS
    //=======================

    private boolean getAndUpdateRefreshedState(Class clazz) {
        if(refreshedFragments.contains(clazz)) {
            return false;
        } else {
            refreshedFragments.add(clazz);
            return true;
        }
    }

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

    protected void removeObservations() {
        for (LiveData liveData : beingObserved) {
            liveData.removeObservers(this);
        }
        beingObserved.clear();
    }
}
