package com.example.development.sakaiclient20.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.development.sakaiclient20.R;
import com.example.development.sakaiclient20.networking.services.CoursesService;
import com.example.development.sakaiclient20.networking.services.ServiceFactory;
import com.example.development.sakaiclient20.networking.utilities.SharedPrefsUtil;
import com.example.development.sakaiclient20.persistence.SakaiDatabase;
import com.example.development.sakaiclient20.persistence.access.CourseDao;
import com.example.development.sakaiclient20.persistence.access.SitePageDao;
import com.example.development.sakaiclient20.repositories.CourseRepository;
import com.example.development.sakaiclient20.ui.fragments.AllCoursesFragment;
import com.example.development.sakaiclient20.ui.helpers.BottomNavigationViewHelper;
import com.example.development.sakaiclient20.ui.viewmodels.CourseViewModel;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    public static final String ALL_COURSES_TAG = "ALL_COURSES";
    public static final String COURSE_TAG = "COURSE";
    public static final String ALL_GRADES_TAG = "GRADES";
    public static final String ASSIGNMENTS_TAG = "ASSIGNMENTS";
    public static final String SITE_GRADES_TAG = "SITEGRADES";

    private FrameLayout container;
    private ProgressBar spinner;

    public boolean isLoadingAllCourses;

    public CourseViewModel courseViewModel;
    LiveData beingObserved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDependencies();

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
        loadAllCoursesFragment();
    }

    private void initDependencies() {
        CourseDao courseDao = SakaiDatabase.getInstance(this).getCourseDao();
        SitePageDao sitePageDao = SakaiDatabase.getInstance(this).getSitePageDao();
        CoursesService coursesService = ServiceFactory.getService(this, CoursesService.class);
        CourseRepository courseRepository = new CourseRepository(courseDao, sitePageDao, coursesService);
        courseViewModel = ViewModelProviders.of(this, new ViewModelProvider.Factory() {
                @NonNull
                @Override
                public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                    return (T) new CourseViewModel(courseRepository);
                }
            }).get(CourseViewModel.class);
    }

    @Override
    protected void onPause() {
        super.onPause();
        beingObserved.removeObservers(this);
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
            if (addToBackStack)
                transaction.addToBackStack(null);

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

        switch (item.getItemId()) {
            case R.id.navigation_home:
                loadAllCoursesFragment();
                return true;
            default:
                return false;
        }
    }

    /**
     * Loads the all courses fragment (home page)
     */
    public void loadAllCoursesFragment() {
        this.container.setVisibility(View.GONE);
        startProgressBar();
        isLoadingAllCourses = true;

        beingObserved = courseViewModel.getCoursesByTerm();
        courseViewModel.getCoursesByTerm()
            .observe(this, courses -> {
                stopProgressBar();

                Bundle b = new Bundle();
                b.putSerializable(ALL_COURSES_TAG, (Serializable) courses);

                AllCoursesFragment coursesFragment = new AllCoursesFragment();
                coursesFragment.setArguments(b);
                loadFragment(coursesFragment, false, false);
                container.setVisibility(View.VISIBLE);

                setActionBarTitle(getString(R.string.app_name));
                isLoadingAllCourses = false;
            });
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

    public void startProgressBar() {
        spinner.setVisibility(View.VISIBLE);
    }

    public void stopProgressBar() {
        spinner.setVisibility(View.GONE);
    }
}
