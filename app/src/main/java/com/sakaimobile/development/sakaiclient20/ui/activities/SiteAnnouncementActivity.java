package com.sakaimobile.development.sakaiclient20.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.sakaimobile.development.sakaiclient20.R;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Announcement;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Course;
import com.sakaimobile.development.sakaiclient20.ui.fragments.AnnouncementsFragment;
import com.sakaimobile.development.sakaiclient20.ui.fragments.SingleAnnouncementFragment;
import com.sakaimobile.development.sakaiclient20.ui.listeners.OnAnnouncementSelected;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class SiteAnnouncementActivity extends BaseObservingActivity
        implements HasSupportFragmentInjector, OnAnnouncementSelected {

    @Inject
    DispatchingAndroidInjector<Fragment> supportFragmentInjector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_announcements);


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

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return supportFragmentInjector;
    }


    @Override
    public void onAnnouncementSelected(Announcement announcement, Map<String, Course> siteIdToCourse) {
        Bundle b = new Bundle();
        b.putSerializable(getString(R.string.single_announcement_tag), announcement);
        // for some reason map isn't serializable, so i had to cast to hashmap
        b.putSerializable(getString(R.string.siteid_to_course_map), (HashMap) siteIdToCourse);

        SingleAnnouncementFragment fragment = new SingleAnnouncementFragment();
        fragment.setArguments(b);

        // load fragment
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.grow_enter, R.anim.pop_exit, R.anim.pop_enter, R.anim.pop_exit)
                .addToBackStack(null)
                .add(R.id.fragment_container, fragment)
                .commit();

    }
}
