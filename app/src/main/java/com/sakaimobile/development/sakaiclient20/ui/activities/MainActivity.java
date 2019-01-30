package com.sakaimobile.development.sakaiclient20.ui.activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
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

import com.sakaimobile.development.sakaiclient20.R;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Course;
import com.sakaimobile.development.sakaiclient20.ui.custom_components.CustomLinkMovementMethod;
import com.sakaimobile.development.sakaiclient20.ui.fragments.AllCoursesFragment;
import com.sakaimobile.development.sakaiclient20.ui.fragments.AllGradesFragment;
import com.sakaimobile.development.sakaiclient20.ui.fragments.AnnouncementsFragment;
import com.sakaimobile.development.sakaiclient20.ui.fragments.SettingsFragment;
import com.sakaimobile.development.sakaiclient20.ui.fragments.assignments.AssignmentsFragment;
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.CourseViewModel;
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.ViewModelFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener,
        AllCoursesFragment.OnCoursesRefreshListener {

    private static final short FRAGMENT_ADD = 1;
    private static final short FRAGMENT_REPLACE = 0;

    private CourseViewModel courseViewModel;
    @Inject ViewModelFactory viewModelFactory;

    private ProgressBar spinner;
    private FrameLayout container;
    private boolean allowNavigation;
    private Set<Class> refreshedFragments;

    //==============================
    // LIFECYCLE/INTERFACE METHODS
    //==============================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.courseViewModel =
                ViewModelProviders.of(this, viewModelFactory).get(CourseViewModel.class);

        this.container = findViewById(R.id.fragment_container);
        this.spinner = findViewById(R.id.nav_activity_progressbar);
        this.spinner.setVisibility(View.GONE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Do not allow navigation until courses finish refreshing
        this.allowNavigation = false;
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        // Request all site pages for the Home Fragment and then loads the fragment
        // refresh since we are loading for the same time
        refreshedFragments = new HashSet<>();
        loadCoursesFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        CustomLinkMovementMethod.setFragmentManager(getSupportFragmentManager());
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        // Setting this listeners allows us to lock navigation while the courses
        // are refreshing
        if(fragment instanceof AllCoursesFragment) {
            AllCoursesFragment coursesFragment = (AllCoursesFragment) fragment;
            coursesFragment.setOnCoursesRefreshListener(this);
        }
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
        if(!allowNavigation)
            return false;

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
    public void onCoursesRefreshStarted() {
        allowNavigation = false;
    }

    @Override
    public void onCoursesRefreshCompleted() {
        allowNavigation = true;
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

        // create fragment arguments
        Bundle b = new Bundle();
        b.putString(getString(R.string.siteid_tag), null);
        boolean shouldRefresh = getAndUpdateRefreshedState(AnnouncementsFragment.class);
        b.putBoolean(AnnouncementsFragment.SHOULD_REFRESH, shouldRefresh);

        // create and load the fragment
        AnnouncementsFragment frag = new AnnouncementsFragment();
        frag.setArguments(b);
        loadFragment(frag, FRAGMENT_REPLACE, false, false);

        this.container.setVisibility(View.VISIBLE);
        stopProgressBar();
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
