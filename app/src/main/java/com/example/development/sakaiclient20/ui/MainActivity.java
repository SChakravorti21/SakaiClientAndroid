package com.example.development.sakaiclient20.ui;

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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.development.sakaiclient20.R;
import com.example.development.sakaiclient20.networking.utilities.SharedPrefsUtil;
import com.example.development.sakaiclient20.persistence.entities.Course;
import com.example.development.sakaiclient20.ui.custom_components.CustomLinkMovementMethod;
import com.example.development.sakaiclient20.ui.custom_components.DownloadCompleteReceiver;
import com.example.development.sakaiclient20.ui.fragments.AllCoursesFragment;
import com.example.development.sakaiclient20.ui.fragments.CourseSitesFragment;
import com.example.development.sakaiclient20.ui.fragments.assignments.AssignmentsFragment;
import com.example.development.sakaiclient20.ui.helpers.BottomNavigationViewHelper;
import com.example.development.sakaiclient20.ui.listeners.OnActionPerformedListener;
import com.example.development.sakaiclient20.ui.viewmodels.AssignmentViewModel;
import com.example.development.sakaiclient20.ui.viewmodels.CourseViewModel;
import com.example.development.sakaiclient20.ui.viewmodels.ViewModelFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener,
        HasSupportFragmentInjector, OnActionPerformedListener {

    @Inject DispatchingAndroidInjector<Fragment> supportFragmentInjector;

    public static final String ALL_COURSES_TAG = "ALL_COURSES";
    public static final String COURSE_TAG = "COURSE";
    public static final String ALL_GRADES_TAG = "GRADES";
    public static final String ASSIGNMENTS_TAG = "ASSIGNMENTS";
    public static final String SITE_GRADES_TAG = "SITE_GRADES";

    private FrameLayout container;
    private ProgressBar spinner;
    public boolean isLoadingAllCourses;

    @Inject CourseViewModel courseViewModel;

    @Inject ViewModelFactory viewModelFactory;
    private List<LiveData> beingObserved;

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
        beingObserved = new ArrayList<>();
        loadCoursesFragment(true);
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return supportFragmentInjector;
    }

    @Override
    protected void onResume() {
        super.onResume();
        CustomLinkMovementMethod.setFragmentManager(getSupportFragmentManager());
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
        if(fragmentManager.getBackStackEntryCount() == 0) {
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
        switch(item.getItemId()) {
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
        if(isLoadingAllCourses)
            return false;

        // To be safe, remove any observations that might be active for the previous tab
        // since that might trigger an unwanted fragment transaction
        removeObservations();
        switch (item.getItemId()) {
            case R.id.navigation_home:
                loadCoursesFragment(false);
                return true;
            case R.id.navigation_assignments:
                loadAssignmentsFragment(true, true);
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
            CourseSitesFragment fragment = CourseSitesFragment.newInstance(course);
            loadFragment(fragment, true, true);
            setActionBarTitle(course.title);
        });
    }

    /*******************************\
      LIFECYCLE CONVENIENCE METHODS
    \*******************************/

    public void registerDownloadReceiver() {
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        DownloadCompleteReceiver receiver = new DownloadCompleteReceiver();
        registerReceiver(receiver, filter);
    }

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
    public boolean loadFragment(Fragment fragment, boolean showAnimations, boolean addToBackStack) {
        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (showAnimations)
                transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
            if (addToBackStack)
                transaction.addToBackStack(null);

            transaction.replace(R.id.fragment_container, fragment).commit();
            return true;
        }

        return false;
    }

    /**
     * Loads the all courses fragment (home page)
     */
    public void loadCoursesFragment(boolean refresh) {
        this.container.setVisibility(View.GONE);
        startProgressBar();
        isLoadingAllCourses = true;

        LiveData<List<List<Course>>> courseLiveData =
                ViewModelProviders.of(this, viewModelFactory)
                        .get(CourseViewModel.class)
                        .getCoursesByTerm(refresh);
        courseLiveData.observe(this, courses -> {
            stopProgressBar();

            AllCoursesFragment coursesFragment = AllCoursesFragment.newInstance(courses, this);
            loadFragment(coursesFragment, false, false);
            container.setVisibility(View.VISIBLE);

            setActionBarTitle(getString(R.string.app_name));
            isLoadingAllCourses = false;

            courseLiveData.removeObservers(this);
            if(refresh)
                Toast.makeText(
                        this,
                        "Successfully refreshed courses",
                        Toast.LENGTH_SHORT
                ).show();
        });
    }

    /**
     * Loads all assignments tab
     */
    public void loadAssignmentsFragment(final boolean sortedByCourses,
                                        final boolean shouldRefresh) {

        this.container.setVisibility(View.GONE);
        this.spinner.setVisibility(View.VISIBLE);

        LiveData<List<List<Course>>> coursesLiveData =
                ViewModelProviders.of(this, viewModelFactory)
                        .get(AssignmentViewModel.class)
                        .getCoursesByTerm();
        beingObserved.add(coursesLiveData);
        coursesLiveData.observe(this, courses -> {
            spinner.setVisibility(View.GONE);

            Bundle bundle = new Bundle();
            bundle.putSerializable(ASSIGNMENTS_TAG, (Serializable) courses);
            bundle.putBoolean(AssignmentsFragment.ASSIGNMENTS_SORTED_BY_COURSES, sortedByCourses);

            AssignmentsFragment fragment = new AssignmentsFragment();
            fragment.setArguments(bundle);
            loadFragment(fragment, false, false);

            container.setVisibility(View.VISIBLE);
        });
    }

    /******************************\
           CONVENIENCE METHODS
    \******************************/

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
