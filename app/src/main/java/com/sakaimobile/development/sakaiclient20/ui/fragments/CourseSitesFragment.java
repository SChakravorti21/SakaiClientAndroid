package com.sakaimobile.development.sakaiclient20.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.sakaimobile.development.sakaiclient20.R;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Course;
import com.sakaimobile.development.sakaiclient20.persistence.entities.SitePage;
import com.sakaimobile.development.sakaiclient20.ui.activities.SitePageActivity;

import java.util.ArrayList;
import java.util.List;

public class CourseSitesFragment extends Fragment {

    private ListView sitePagesListView;
    private Course courseToView;

    public static CourseSitesFragment newInstance(Course course) {
        if(course == null)
            throw new IllegalArgumentException("Course cannot be null!");

        CourseSitesFragment fragment = new CourseSitesFragment();
        fragment.courseToView = course;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflated = inflater.inflate(R.layout.fragment_course, null);

        // get the list of site titles
        // only display in adapter if it has a valid url
        final List<String> siteTitles = new ArrayList<>();
        for(SitePage page : courseToView.sitePages) {
            if(page.url != null)
                siteTitles.add(page.title);
        }

        // create the adapter and list view
        this.sitePagesListView = inflated.findViewById(R.id.sites_list_view);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, siteTitles);
        this.sitePagesListView.setAdapter(adapter);

        this.sitePagesListView.setOnItemClickListener((adapterView, view, pos, id) -> {
            String siteType = (String) sitePagesListView.getItemAtPosition(pos);

            // start the sitepage activity to handle the appropriate site page
            Intent i = new Intent(getActivity(), SitePageActivity.class);
            i.putExtra(getString(R.string.site_type_tag), siteType);
            i.putExtra(getString(R.string.course_tag), courseToView);

            startActivity(i);
        });

        return inflated;
    }

}