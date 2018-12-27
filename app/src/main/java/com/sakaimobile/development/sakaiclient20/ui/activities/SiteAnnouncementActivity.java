package com.sakaimobile.development.sakaiclient20.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.sakaimobile.development.sakaiclient20.R;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Course;
import com.sakaimobile.development.sakaiclient20.ui.fragments.AnnouncementsFragment;

import java.util.HashMap;

public class SiteAnnouncementActivity extends BaseObservingActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(v -> SiteAnnouncementActivity.super.onBackPressed());

        // extract the arguments from the intent
        Intent intent = getIntent();
        String siteId = intent.getStringExtra(getString(R.string.siteid_tag));
        HashMap<String, Course> siteIdToCourse = (HashMap) intent.getSerializableExtra(getString(R.string.siteid_to_course_map));

        // create the bundle for site announcements frag args
        Bundle b = new Bundle();
        b.putString(getString(R.string.siteid_tag), siteId);
        b.putSerializable(getString(R.string.siteid_to_course_map), siteIdToCourse);

        // create the fragment
        AnnouncementsFragment frag = new AnnouncementsFragment();
        frag.setArguments(b);

        // load the fragment
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, frag)
                .commit();

    }

}
