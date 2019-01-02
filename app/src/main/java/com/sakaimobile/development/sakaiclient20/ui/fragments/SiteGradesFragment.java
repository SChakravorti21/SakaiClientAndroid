package com.sakaimobile.development.sakaiclient20.ui.fragments;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.sakaimobile.development.sakaiclient20.R;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Grade;
import com.sakaimobile.development.sakaiclient20.ui.adapters.GradeItemAdapter;
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.GradeViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

public class SiteGradesFragment extends Fragment {

    @Inject
    GradeViewModel gradeViewModel;

    private ListView siteGradesListView;
    private String siteId;

    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        siteId = getArguments().getString(getString(R.string.siteid_tag));

//        setup the toolbar
//        setupToolbar(siteId);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_site_grades, null);

        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setRefreshing(true);

        siteGradesListView = view.findViewById(R.id.site_grades_list_view);


        // get the live data
        LiveData<List<Grade>> gradeLiveData = gradeViewModel.getSiteGrades(siteId);
//        beingObserved.add(gradeLiveData);

        gradeLiveData.observe(this, grades -> {
            feedGradesIntoListView(grades);

            swipeRefreshLayout.setRefreshing(false);
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            gradeViewModel.refreshSiteData(siteId);
        });


        return view;
    }

    /**
     * Puts the grades of the current course into an adapter and adds the adapter to the
     * list view
     */
    public void feedGradesIntoListView(List<Grade> grades) {

        if (grades != null && grades.size() > 0) {
            //puts grades into custom adapter
            GradeItemAdapter adapter = new GradeItemAdapter(getActivity(), grades);
            siteGradesListView.setAdapter(adapter);
        } else {
            Toast.makeText(getActivity(), "No grades found", Toast.LENGTH_SHORT).show();
        }
    }

//    /**
//     * Sets up the toolbar, title text, back button, etc.
//     * @param siteId siteId which is used to get the course title
//     */
//    private void setupToolbar(String siteId) {
//        // add the toolbar
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        toolbar.setNavigationOnClickListener(v -> SiteGradesFragment.super.onBackPressed());
//
//
//        CourseViewModel courseViewModel = (CourseViewModel) getViewModel(CourseViewModel.class);
//
//        // set the toolbar title as the course title
//        LiveData<Course> courseLiveData = courseViewModel.getCourse(siteId);
//        beingObserved.add(courseLiveData);
//
//        courseLiveData.observe(this, course -> {
//            if(course != null) {
//                String title = String.format("%s: %s", getString(R.string.title_gradebook), course.title);
//                toolbar.setTitle(title);
//            }
//        });
//    }
}
