package com.example.development.sakaiclient20.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.development.sakaiclient20.R;
import com.example.development.sakaiclient20.persistence.entities.Course;
import com.example.development.sakaiclient20.ui.MainActivity;
import com.example.development.sakaiclient20.ui.listeners.OnActionPerformedListener;

public class CourseSitesFragment extends Fragment {

    private ListView sitePagesListView;
    private Course courseToView;
    private OnActionPerformedListener siteClickedListener;

    public static CourseSitesFragment newInstance(Course course, OnActionPerformedListener siteClickedListener) {
        if(course == null)
            throw new IllegalArgumentException("Course cannot be null!");

        CourseSitesFragment fragment = new CourseSitesFragment();
        fragment.courseToView = course;
        fragment.siteClickedListener = siteClickedListener;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflated = inflater.inflate(R.layout.fragment_course, null);

        final String[] siteTitles = new String[this.courseToView.sitePages.size()];
        for (int i = 0; i < siteTitles.length; i++) {
            siteTitles[i] = this.courseToView.sitePages.get(i).title;
        }

        this.sitePagesListView = inflated.findViewById(R.id.sites_list_view);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, siteTitles);
        this.sitePagesListView.setAdapter(adapter);

        this.sitePagesListView.setOnItemClickListener((adapterView, view, pos, id) -> {
            String siteName = (String) sitePagesListView.getItemAtPosition(pos);

            if (siteName.equals(getString(R.string.gradebook))) {
                siteClickedListener.onSiteGradesSelected(courseToView);
            }
            else if(siteName.equals(getString(R.string.announcements_site))) {
                siteClickedListener.onSiteAnnouncementsSelected(courseToView);
            }
        });

        // TODO change the hackies
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.setActionBarTitle(courseToView.title);


        return inflated;
    }
}

