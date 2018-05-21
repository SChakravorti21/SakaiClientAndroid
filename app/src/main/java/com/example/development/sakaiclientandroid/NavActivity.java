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
import com.example.development.sakaiclientandroid.utils.DataHandler;
import com.example.development.sakaiclientandroid.utils.RequestCallback;
import com.example.development.sakaiclientandroid.utils.RequestManager;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NavActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener{


    private String baseUrl;
    private String cookieUrl;

    OkHttpClient httpClient;

    private String responseBody;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        // Create RequestManager's Retrofit instance
        RequestManager.createRetrofitInstance(this);
        // Request all site pages for the Home Fragment
        DataHandler.getAllSites(new RequestCallback() {
            @Override
            public void onGradesSuccess(Response<ResponseBody> response) {
                Log.i("Response", "SUCCESS!");
                Log.i("Status Code", "" + response.code());

                try {

                    responseBody = response.body().string();
                    Log.i("Response body string", responseBody);

                    Bundle bundle = new Bundle();
                    bundle.putString(getString(R.string.title_activity_nav), responseBody);

                    HomeFragment fragment = new HomeFragment();
                    fragment.setArguments(bundle);
                    loadFragment(fragment);

                }

                catch(Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onGradesFailure(Throwable throwable) {
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
