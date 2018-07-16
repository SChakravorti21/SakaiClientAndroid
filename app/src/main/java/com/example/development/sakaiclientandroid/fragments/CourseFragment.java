package com.example.development.sakaiclientandroid.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.development.sakaiclientandroid.NavActivity;
import com.example.development.sakaiclientandroid.R;
import com.example.development.sakaiclientandroid.api_models.assignments.Assignment;
import com.example.development.sakaiclientandroid.fragments.assignments.SiteAssignmentsFragment;
import com.example.development.sakaiclientandroid.models.Course;
import com.example.development.sakaiclientandroid.utils.DataHandler;

import java.io.Serializable;
import java.util.List;

public class CourseFragment extends BaseFragment {

    private ListView sitePagesListView;
    private Course course;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_course, null);


        Bundle bundle = this.getArguments();
        if(bundle == null) {
            //TODO error message even though we shouldn't get an error here
            return view;
        }


        String courseId = bundle.getString(getString(R.string.site_id));
        this.course = DataHandler.getCourseFromId(courseId);
        final String[] siteTitles = new String[this.course.getSitePages().size()];
        for(int i = 0; i < siteTitles.length; i++) {
            siteTitles[i] = this.course.getSitePages().get(i).getTitle();
        }


        //set activity title to current course
        ((NavActivity) getActivity()).setActionBarTitle(course.getTitle());

        this.sitePagesListView = view.findViewById(R.id.sites_list_view);
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, siteTitles);
        this.sitePagesListView.setAdapter(adapter);

        this.sitePagesListView.setOnItemClickListener(new SitePageClickListener());

        return view;
    }

    private class SitePageClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {

            String siteName = (String) sitePagesListView.getItemAtPosition(pos);

            // Initialized as dummy fragment to prevent crashing
            // TODO: Initialize as webview with appropriate destination url
            Fragment fragment = new Fragment();
            if (siteName.equalsIgnoreCase(getString(R.string.gradebook))) {
                fragment = constructSiteGradesFragment();
            } else if (siteName.equalsIgnoreCase(getString(R.string.assignments))) {
                fragment = constructSiteAssignmentsFragment();
            }

            loadFragment(fragment);
        }

        private SiteAssignmentsFragment constructSiteAssignmentsFragment() {
            List<Assignment> assignments = course.getAssignmentList();

            Bundle bundle = new Bundle();
            bundle.putSerializable(NavActivity.ASSIGNMENTS_TAG, (Serializable) assignments);

            SiteAssignmentsFragment fragment = new SiteAssignmentsFragment();
            fragment.setArguments(bundle);

            return fragment;
        }

        private SiteGradesFragment constructSiteGradesFragment() {
            final String siteId = course.getId();
            //puts the siteId into the bundle
            Bundle bun = new Bundle();
            bun.putString(getString(R.string.site_id), siteId);

            SiteGradesFragment frag = new SiteGradesFragment();
            frag.setArguments(bun);
            return frag;
        }

        private void loadFragment(Fragment fragment) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                    .replace(R.id.fragment_container, fragment, null)
                    .addToBackStack(null)
                    .commit();
        }
    }
}

