package com.example.development.sakaiclientandroid;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.development.sakaiclientandroid.fragments.AllCoursesFragment;
import com.example.development.sakaiclientandroid.fragments.AllGradesFragment;
import com.example.development.sakaiclientandroid.fragments.AnnouncementsFragment;
import com.example.development.sakaiclientandroid.fragments.AssignmentsFragment;
import com.example.development.sakaiclientandroid.fragments.CourseSitesFragment;
import com.example.development.sakaiclientandroid.fragments.SettingsFragment;
import com.example.development.sakaiclientandroid.fragments.SiteGradesFragment;
import com.example.development.sakaiclientandroid.models.Course;
import com.example.development.sakaiclientandroid.utils.DataHandler;
import com.example.development.sakaiclientandroid.utils.requests.RequestCallback;
import com.example.development.sakaiclientandroid.utils.requests.RequestManager;
import com.example.development.sakaiclientandroid.utils.requests.SharedPrefsUtil;
import java.util.ArrayList;


public class NavActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    public static final String ALL_COURSES_TAG = "ALL_COURSES";
    public static final String COURSE_TAG = "COURSE";
    public static final String ALL_GRADES_TAG = "GRADES";
    public static final String ASSIGNMENTS_TAG = "ASSIGNMENTS";
    public static final String SITE_GRADES_TAG = "SITEGRADES";

    private FrameLayout container;
    private ProgressBar spinner;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
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

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        // Create RequestManager's Retrofit instance
        RequestManager.createRetrofitInstance(this);


        //clear the saved tree states in saved preferences so some nodes aren't opened by default
        SharedPrefsUtil.clearTreeStates(this);

        // Request all site pages for the Home Fragment and then loads the fragment
        //refresh since we are loading for the same time
        loadAllCoursesFragment(true);

    }


    /**
     * Loads a given fragment into the fragment container in the NavActivity layout
     * @param fragment
     * @return boolean whether the fragment was successfully loaded
     */
    private boolean loadFragment(Fragment fragment, boolean showAnimations) {

        if(fragment != null) {

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            if(showAnimations)
                transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                        .addToBackStack(null);


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


        Fragment fragment = null;

        switch(item.getItemId()) {

            case R.id.navigation_home:
                loadAllCoursesFragment(false);
                return true;

            case R.id.navigation_announcements:
                fragment = new AnnouncementsFragment();
                break;

            case R.id.navigation_assignments:
                loadAssignmentsFragment();
                return true;

            case R.id.navigation_gradebook:
                //set refresh to false since we want to display cached grades
                loadAllGradesFragment(false);
                return true;

            case R.id.navigation_settings:
                fragment = new SettingsFragment();
                break;

        }

        return this.loadFragment(fragment, false);

    }


    public void loadAllCoursesFragment(boolean refresh)
    {
        this.container.setVisibility(View.GONE);
        this.spinner.setVisibility(View.VISIBLE);

        DataHandler.requestAllSites(refresh, new RequestCallback()
        {

            @Override
            public void onAllCoursesSuccess(ArrayList<ArrayList<Course>> response)
            {
                spinner.setVisibility(View.GONE);

                Bundle b = new Bundle();
                b.putSerializable(ALL_COURSES_TAG, response);

                AllCoursesFragment frag = new AllCoursesFragment();
                frag.setArguments(b);
                loadFragment(frag, false);

                container.setVisibility(View.VISIBLE);

                setActionBarTitle(getString(R.string.app_name));
            }

            @Override
            public void onAllGradesFailure(Throwable t)
            {
                //TODO deal with error
                Log.e("ERROR: ", t.getMessage());
            }
        });
    }


    public void loadAllGradesFragment(boolean refreshGrades)
    {
        this.container.setVisibility(View.GONE);
        this.spinner.setVisibility(View.VISIBLE);

        DataHandler.requestAllGrades(refreshGrades, new RequestCallback()
        {

            @Override
            public void onAllGradesSuccess(ArrayList<ArrayList<Course>> response)
            {
                spinner.setVisibility(View.GONE);

                Bundle b = new Bundle();
                b.putSerializable(ALL_GRADES_TAG, response);

                AllGradesFragment frag = new AllGradesFragment();
                frag.setArguments(b);
                loadFragment(frag, false);

                container.setVisibility(View.VISIBLE);

                setActionBarTitle(getString(R.string.app_name));
            }

            @Override
            public void onAllGradesFailure(Throwable t)
            {
                //TODO deal with error
                Log.e("ERROR: ", t.getMessage());
            }
        });
    }

    public void loadSiteGradesFragment(boolean refreshGrades, String siteId)
    {
        this.spinner.setVisibility(View.VISIBLE);

        DataHandler.requestGradesForSite(siteId, refreshGrades, new RequestCallback()
        {

            @Override
            public void onSiteGradesSuccess(Course course)
            {
                spinner.setVisibility(View.GONE);

                if(course == null) {
                    Toast.makeText(NavActivity.this, "Course has no grades", Toast.LENGTH_SHORT).show();
                }
                //course has grades
                else {
                    Bundle b = new Bundle();
                    b.putSerializable(SITE_GRADES_TAG, course);

                    SiteGradesFragment frag = new SiteGradesFragment();
                    frag.setArguments(b);
                    loadFragment(frag, true);

                    setActionBarTitle("Gradebook: " + course.getTitle());
                }
            }

            @Override
            public void onSiteGradesFailure(Throwable t)
            {
                //TODO deal with error
                Log.e("ERROR: ", t.getMessage());
            }
        });
    }

    public void loadCourseFragment(String siteId)
    {

        Course course = DataHandler.getCourseFromId(siteId);
        Bundle b = new Bundle();
        b.putSerializable(COURSE_TAG, course);

        CourseSitesFragment frag = new CourseSitesFragment();
        frag.setArguments(b);
        loadFragment(frag, true);
        setActionBarTitle(course.getTitle());

    }


    public void loadAssignmentsFragment() {
        this.container.setVisibility(View.GONE);
        this.spinner.setVisibility(View.VISIBLE);

        DataHandler.requestAllAssignments(new RequestCallback() {
            @Override
            public void onAllAssignmentsSuccess(ArrayList<ArrayList<Course>> response) {
                super.onAllAssignmentsSuccess(response);
                spinner.setVisibility(View.GONE);

                Bundle bundle = new Bundle();
                bundle.putSerializable(ASSIGNMENTS_TAG, response);

                AssignmentsFragment fragment = new AssignmentsFragment();
                fragment.setArguments(bundle);
                loadFragment(fragment, false);

                container.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAllAssignmentsFailure(Throwable throwable) {
                // TODO: Handle errors give proper error message
                Log.i("Response", "failure");
                Log.e("Response error", throwable.getMessage());
            }
        });
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

}
