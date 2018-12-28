package com.sakaimobile.development.sakaiclient20.ui.activities;

import android.arch.lifecycle.LiveData;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.Toast;

import com.sakaimobile.development.sakaiclient20.R;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Course;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Grade;
import com.sakaimobile.development.sakaiclient20.ui.adapters.GradeItemAdapter;
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.CourseViewModel;
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.GradeViewModel;

import java.util.List;

public class SiteGradesActivity extends BaseObservingActivity {

    private ListView siteGradesListView;
    private String siteId;

    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_grades);

        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setRefreshing(true);

        siteGradesListView = findViewById(R.id.site_grades_list_view);

        Intent intent = getIntent();
        siteId = intent.getStringExtra(getString(R.string.siteid_tag));

        // setup the toolbar
        setupToolbar(siteId);


        GradeViewModel gradeViewModel = (GradeViewModel) getViewModel(GradeViewModel.class);

        // get the live data
        LiveData<List<Grade>> gradeLiveData = gradeViewModel.getSiteGrades(siteId);
        beingObserved.add(gradeLiveData);

        gradeLiveData.observe(this, grades -> {
            feedGradesIntoListView(grades);

            swipeRefreshLayout.setRefreshing(false);
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            gradeViewModel.refreshSiteData(siteId);
        });
    }

    /**
     * Puts the grades of the current course into an adapter and adds the adapter to the
     * list view
     */
    public void feedGradesIntoListView(List<Grade> grades) {

        if (grades != null && grades.size() > 0) {
            //puts grades into custom adapter
            GradeItemAdapter adapter = new GradeItemAdapter(this, grades);
            siteGradesListView.setAdapter(adapter);
        } else {
            Toast.makeText(this, "No grades found", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Sets up the toolbar, title text, back button, etc.
     * @param siteId siteId which is used to get the course title
     */
    private void setupToolbar(String siteId) {
        // add the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> SiteGradesActivity.super.onBackPressed());


        CourseViewModel courseViewModel = (CourseViewModel) getViewModel(CourseViewModel.class);

        // set the toolbar title as the course title
        LiveData<Course> courseLiveData = courseViewModel.getCourse(siteId);
        beingObserved.add(courseLiveData);

        courseLiveData.observe(this, course -> {
            if(course != null) {
                String title = String.format("%s: %s", getString(R.string.title_gradebook), course.title);
                toolbar.setTitle(title);
            }
        });
    }
}
