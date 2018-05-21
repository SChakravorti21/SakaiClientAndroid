package com.example.development.sakaiclientandroid;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.example.development.sakaiclientandroid.fragments.AnnouncementsFragment;
import com.example.development.sakaiclientandroid.fragments.AssignmentsFragment;
import com.example.development.sakaiclientandroid.fragments.GradebookFragment;
import com.example.development.sakaiclientandroid.fragments.HomeFragment;
import com.example.development.sakaiclientandroid.fragments.SettingsFragment;
import com.example.development.sakaiclientandroid.services.SakaiService;
import com.example.development.sakaiclientandroid.utils.HeaderInterceptor;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



public class NavActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener{



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);




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
     * and then loads the fragment into the Frame Layout. For the HomeFragment, we have to
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

                Bundle bundle = new Bundle();
                bundle.putString(getString(R.string.title_activity_nav), responseBody);

                fragment = new HomeFragment();
                fragment.setArguments(bundle);
                break;


            case R.id.navigation_announcements:
                fragment = new AnnouncementsFragment();
                break;

            case R.id.navigation_assignments:
                fragment = new AssignmentsFragment();
                break;

            case R.id.navigation_gradebook:
                fragment = new GradebookFragment();
                break;

            case R.id.navigation_settings:
                fragment = new SettingsFragment();
                break;

        }

        return this.loadFragment(fragment);

    }
}
