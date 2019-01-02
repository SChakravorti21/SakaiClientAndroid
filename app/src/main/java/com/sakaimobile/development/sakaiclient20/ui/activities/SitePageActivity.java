package com.sakaimobile.development.sakaiclient20.ui.activities;

import android.arch.lifecycle.LiveData;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.sakaimobile.development.sakaiclient20.R;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Course;
import com.sakaimobile.development.sakaiclient20.ui.fragments.SiteGradesFragment;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class SitePageActivity extends AppCompatActivity {


    protected Set<LiveData> beingObserved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_page);

        beingObserved = new HashSet<>();

        Intent i = getIntent();
        String siteType = i.getStringExtra(getString(R.string.site_type_tag));
        Course course = (Course) i.getSerializableExtra(getString(R.string.course_tag));

        if(siteType.equals(getString(R.string.gradebook))) {
            startSiteGradesFragment(course);
        } else if(siteType.equals(getString(R.string.announcements_site))) {

        } else if(siteType.equals(getString(R.string.resources_site))) {

        } else {

        }
    }


    private void startSiteGradesFragment(Course course) {
        Bundle bun = new Bundle();
        bun.putString(getString(R.string.siteid_tag), course.siteId);

        SiteGradesFragment fragment = new SiteGradesFragment();
        fragment.setArguments(bun);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit();
    }



    @Override
    protected void onPause() {
        super.onPause();
        removeObservations();
    }

    protected void removeObservations() {
        for (LiveData liveData : beingObserved) {
            liveData.removeObservers(this);
        }
        beingObserved.clear();
    }




}
