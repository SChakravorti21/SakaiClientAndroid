package com.sakaimobile.development.sakaiclient20.ui.activities;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sakaimobile.development.sakaiclient20.R;
import com.sakaimobile.development.sakaiclient20.ui.custom_components.CustomLinkMovementMethod;
import com.sakaimobile.development.sakaiclient20.ui.fragments.AllCoursesFragment;
import com.sakaimobile.development.sakaiclient20.ui.fragments.AllGradesFragment;
import com.sakaimobile.development.sakaiclient20.ui.fragments.AnnouncementsFragment;
import com.sakaimobile.development.sakaiclient20.ui.fragments.preferences.SettingsFragment;
import com.sakaimobile.development.sakaiclient20.ui.fragments.assignments.AssignmentsFragment;
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.ViewModelFactory;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import dagger.android.AndroidInjection;

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener,
        AllCoursesFragment.OnCoursesRefreshListener {

    private static final short FRAGMENT_ADD = 1;
    private static final short FRAGMENT_REPLACE = 0;

    @Inject ViewModelFactory viewModelFactory;
    private boolean allowNavigation;

    //==============================
    // LIFECYCLE/INTERFACE METHODS
    //==============================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Do not allow navigation until courses finish refreshing
        this.allowNavigation = false;
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        // Request all site pages for the Home Fragment and then loads the fragment
        // refresh since we are loading for the same time
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
     * Loads a given fragment into the fragment container in the MainActivity layout
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

    public void loadCoursesFragment() {
        Bundle bundle = new Bundle();
        Fragment fragment = new AllCoursesFragment();
        fragment.setArguments(bundle);
        loadFragment(fragment, FRAGMENT_REPLACE, false, false);
    }

    public void loadAssignmentsFragment() {
        Bundle bundle = new Bundle();
        Fragment fragment = new AssignmentsFragment();
        fragment.setArguments(bundle);
        loadFragment(fragment, FRAGMENT_REPLACE, false, false);
    }

    public void loadGradesFragment() {
        Bundle bundle = new Bundle();
        Fragment fragment = new AllGradesFragment();
        fragment.setArguments(bundle);
        loadFragment(fragment, FRAGMENT_REPLACE, false, false);
    }

    public void loadAnnouncementsFragment() {
        // create fragment arguments
        Bundle b = new Bundle();
        b.putString(getString(R.string.siteid_tag), null);

        // create and load the fragment
        AnnouncementsFragment frag = new AnnouncementsFragment();
        frag.setArguments(b);
        loadFragment(frag, FRAGMENT_REPLACE, false, false);
    }

    //=======================
    // CONVENIENCE METHODS
    //=======================

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }
}
