package com.example.development.sakaiclientandroid;

import android.app.DownloadManager;
import android.content.IntentFilter;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.development.sakaiclientandroid.api_models.assignments.Assignment;
import com.example.development.sakaiclientandroid.fragments.AllCoursesFragment;
import com.example.development.sakaiclientandroid.fragments.AllGradesFragment;
import com.example.development.sakaiclientandroid.fragments.AnnouncementsFragment;
import com.example.development.sakaiclientandroid.fragments.assignments.AssignmentsFragment;
import com.example.development.sakaiclientandroid.fragments.CourseSitesFragment;
import com.example.development.sakaiclientandroid.models.Course;
import com.example.development.sakaiclientandroid.utils.BottomNavigationViewHelper;
import com.example.development.sakaiclientandroid.utils.DataHandler;
import com.example.development.sakaiclientandroid.utils.custom.CustomLinkMovementMethod;
import com.example.development.sakaiclientandroid.utils.requests.DownloadCompleteReceiver;
import com.example.development.sakaiclientandroid.utils.requests.RequestCallback;
import com.example.development.sakaiclientandroid.utils.requests.RequestManager;

import com.example.development.sakaiclientandroid.utils.requests.SharedPrefsUtil;

import java.util.ArrayList;


public final class NavActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    public static final String ALL_COURSES_TAG = "ALL_COURSES";
    public static final String COURSE_TAG = "COURSE";
    public static final String ALL_GRADES_TAG = "GRADES";
    public static final String ASSIGNMENTS_TAG = "ASSIGNMENTS";
    public static final String SITE_GRADES_TAG = "SITEGRADES";

    private FrameLayout container;
    private ProgressBar spinner;
    private Toolbar toolbar;

    public boolean isLoadingAllCourses;


    public void startProgressBar() {
        spinner.setVisibility(View.VISIBLE);
    }

    public void stopProgressBar() {
        spinner.setVisibility(View.GONE);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);

        // Get reference to the container
        container = findViewById(R.id.fragment_container);

        //starts spinner
        this.spinner = findViewById(R.id.nav_activity_progressbar);
        this.spinner.setVisibility(View.VISIBLE);

        this.toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);


        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        BottomNavigationViewHelper.removeShiftMode(navigation);

        // Create RequestManager's Retrofit instance
        RequestManager.createRetrofitInstance(this);


        //clear the saved tree states in saved preferences so some nodes aren't opened by default
        SharedPrefsUtil.clearTreeStates(this);

        // Request all site pages for the Home Fragment and then loads the fragment
        //refresh since we are loading for the same time
        loadAllCoursesFragment(true);
        registerDownloadReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        CustomLinkMovementMethod.setFragmentManager(getSupportFragmentManager());
    }

    public void registerDownloadReceiver() {
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        DownloadCompleteReceiver receiver = new DownloadCompleteReceiver();
        registerReceiver(receiver, filter);
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

            if (addToBackStack) {
                transaction.addToBackStack(null);
            }


            transaction.replace(R.id.fragment_container, fragment).commit();

            return true;
        }

        return false;
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

        Fragment fragment = null;

        switch (item.getItemId()) {

            case R.id.navigation_home:
                loadAllCoursesFragment(false);
                return true;

            case R.id.navigation_announcements:
                fragment = new AnnouncementsFragment();
                break;

            case R.id.navigation_assignments:
                loadAssignmentsFragment(true, false);
                return true;

            case R.id.navigation_gradebook:
                //set refresh to false since we want to display cached grades
                loadAllGradesFragment(false);
                return true;

        }

        return this.loadFragment(fragment, false, false);

    }

    /**
     * Prevents a glitchy UI transition when replacing fragments in the
     * container by removing all of them ahead of time before the main
     * transaction is performed.
     */
    private void clearContainer() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        for(Fragment fragment : fragmentManager.getFragments()) {
            if(fragment != null)
                fragmentManager.beginTransaction().remove(fragment).commit();
        }
    }

    /**
     * Loads the all courses fragment (home page)
     *
     * @param refresh whether or not to refresh courses
     */
    public void loadAllCoursesFragment(boolean refresh) {
        clearContainer();

        this.container.setVisibility(View.GONE);
        this.spinner.setVisibility(View.VISIBLE);
        isLoadingAllCourses = true;

        DataHandler.requestAllCourses(refresh, new RequestCallback() {

            @Override
            public void onAllCoursesSuccess(ArrayList<ArrayList<Course>> response) {
                spinner.setVisibility(View.GONE);

                Bundle b = new Bundle();
                b.putSerializable(ALL_COURSES_TAG, response);

                AllCoursesFragment frag = new AllCoursesFragment();
                frag.setArguments(b);
                loadFragment(frag, false, false);

                container.setVisibility(View.VISIBLE);

                setActionBarTitle(getString(R.string.app_name));

                isLoadingAllCourses = false;
            }

            @Override
            public void onAllCoursesEmpty(int msgRscId) {

                spinner.setVisibility(View.GONE);
                showErrorToast(getString(msgRscId));
            }

            @Override
            public void onRequestFailure(int msgRscId, Throwable t) {
                spinner.setVisibility(View.GONE);
                showErrorToast(getString(msgRscId));

                t.printStackTrace();
            }
        });
    }


    /**
     * Loads the all grades tab
     *
     * @param refreshGrades whether or not to refresh grades
     */
    public void loadAllGradesFragment(boolean refreshGrades) {
        clearContainer();

        this.container.setVisibility(View.GONE);
        this.spinner.setVisibility(View.VISIBLE);

        DataHandler.requestAllGrades(refreshGrades, new RequestCallback() {

            @Override
            public void onAllGradesSuccess(ArrayList<ArrayList<Course>> response) {
                spinner.setVisibility(View.GONE);

                Bundle b = new Bundle();
                b.putSerializable(ALL_GRADES_TAG, response);

                AllGradesFragment frag = new AllGradesFragment();
                frag.setArguments(b);
                loadFragment(frag, false, false);

                container.setVisibility(View.VISIBLE);

                setActionBarTitle(getString(R.string.app_name));
            }

            @Override
            public void onAllGradesEmpty(int msgRscId) {

                spinner.setVisibility(View.GONE);
                showErrorToast(getString(msgRscId));
            }

            @Override
            public void onRequestFailure(int msgRscId, Throwable t) {
                spinner.setVisibility(View.GONE);
                showErrorToast(getString(msgRscId));

                t.printStackTrace();

            }
        });
    }


    /**
     * Loads the fragment showing a course's sites
     *
     * @param siteId id of course to show
     */
    public void loadCourseFragment(String siteId) {

        Course course = DataHandler.getCourseFromId(siteId);
        Bundle b = new Bundle();
        b.putSerializable(COURSE_TAG, course);

        CourseSitesFragment frag = new CourseSitesFragment();
        frag.setArguments(b);
        loadFragment(frag, true, true);
        setActionBarTitle(course.getTitle());

    }


    /**
     * Loads all assignments tab
     */
    public void loadAssignmentsFragment(final boolean sortedByCourses,
                                        final boolean shouldRefresh) {
        clearContainer();

        this.container.setVisibility(View.GONE);
        this.spinner.setVisibility(View.VISIBLE);

        DataHandler.requestAllAssignments(new RequestCallback() {
            @Override
            public void onAllAssignmentsByCourseSuccess(ArrayList<ArrayList<Course>> response) {
                spinner.setVisibility(View.GONE);

                Bundle bundle = new Bundle();
                bundle.putSerializable(ASSIGNMENTS_TAG, response);
                bundle.putBoolean(AssignmentsFragment.ASSIGNMENTS_SORTED_BY_COURSES, sortedByCourses);

                AssignmentsFragment fragment = new AssignmentsFragment();
                fragment.setArguments(bundle);
                loadFragment(fragment, false, false);

                container.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAllAssignmentsByDateSuccess(ArrayList<ArrayList<Assignment>> response) {
                spinner.setVisibility(View.GONE);

                Bundle bundle = new Bundle();
                bundle.putSerializable(ASSIGNMENTS_TAG, response);
                bundle.putBoolean(AssignmentsFragment.ASSIGNMENTS_SORTED_BY_COURSES, sortedByCourses);

                AssignmentsFragment fragment = new AssignmentsFragment();
                fragment.setArguments(bundle);
                loadFragment(fragment, false, false);

                container.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAllAssignmentsFailure(Throwable throwable) {
                // TODO: Handle errors give proper error message
                Log.i("Response", "failure");
                Log.e("Response error", throwable.getMessage());
            }
        }, sortedByCourses, shouldRefresh);
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }


    public void showErrorToast(String errorString) {
        Toast toast = Toast.makeText(this, errorString, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);

        View view = toast.getView();
        view.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.sakai_light_black), PorterDuff.Mode.SRC_IN);

        TextView text = view.findViewById(android.R.id.message);
        text.setTextColor(ContextCompat.getColor(this, R.color.white));
        text.setTextSize(20);

        toast.show();
    }


}
