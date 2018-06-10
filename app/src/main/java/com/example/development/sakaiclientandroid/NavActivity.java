package com.example.development.sakaiclientandroid;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.example.development.sakaiclientandroid.fragments.AllCoursesFragment;
import com.example.development.sakaiclientandroid.fragments.AllGradesFragment;
import com.example.development.sakaiclientandroid.fragments.AnnouncementsFragment;
import com.example.development.sakaiclientandroid.fragments.AssignmentsFragment;
import com.example.development.sakaiclientandroid.fragments.SettingsFragment;
import com.example.development.sakaiclientandroid.utils.DataHandler;
import com.example.development.sakaiclientandroid.utils.interfaces.OnViewCreatedListener;
import com.example.development.sakaiclientandroid.utils.requests.RequestCallback;
import com.example.development.sakaiclientandroid.utils.requests.RequestManager;


public class NavActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener, OnViewCreatedListener {

    private ProgressBar spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);


        //starts spinner
        this.spinner = findViewById(R.id.nav_activity_progressbar);
        this.spinner.setVisibility(View.VISIBLE);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        // Create RequestManager's Retrofit instance
        RequestManager.createRetrofitInstance(this);
        // Request all site pages for the Home Fragment
        DataHandler.requestAllSites(new RequestCallback() {
            @Override
            public void onCoursesSuccess() {

                spinner.setVisibility(View.GONE);

                Bundle bun = new Bundle();
                bun.putString("showHomeOrGrades", "Home");

                AllCoursesFragment fragment = new AllCoursesFragment();
                fragment.setArguments(bun);
                loadFragment(fragment);
            }

            @Override
            public void onCoursesFailure(Throwable throwable) {
                // TODO: Handle errors give proper error message
                Log.i("Response", "failure");
                Log.e("Response error", throwable.getMessage());
            }
        });
    }


    /**
     * Loads a given fragment into the fragment container in the NavActivity layout
     * @param fragment
     * @return boolean whether the fragment was successfully loaded
     */
    private boolean loadFragment(Fragment fragment) {

        if(fragment != null) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();

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
                fragment = new AllCoursesFragment();
                break;


            case R.id.navigation_announcements:
                fragment = new AnnouncementsFragment();
                break;

            case R.id.navigation_assignments:
                fragment = AssignmentsFragment.newInstance(this);
                break;

            case R.id.navigation_gradebook:
                fragment = new AllGradesFragment();
                break;

            case R.id.navigation_settings:
                fragment = new SettingsFragment();
                break;

        }

        return this.loadFragment(fragment);

    }


    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }


    @Override
    public void onViewCreated(View view) {
        FrameLayout fragmentContainer = findViewById(R.id.fragment_container);
        fragmentContainer.removeAllViews();
        fragmentContainer.addView(view);
    }
}
