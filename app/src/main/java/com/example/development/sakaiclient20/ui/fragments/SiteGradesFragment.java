package com.example.development.sakaiclient20.ui.fragments;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.development.sakaiclient20.R;
import com.example.development.sakaiclient20.persistence.entities.Course;
import com.example.development.sakaiclient20.persistence.entities.Grade;
import com.example.development.sakaiclient20.ui.adapters.GradeItemAdapter;
import com.example.development.sakaiclient20.ui.viewmodels.GradeViewModel;
import com.example.development.sakaiclient20.ui.viewmodels.ViewModelFactory;

import java.util.List;

import javax.inject.Inject;

public class SiteGradesFragment extends Fragment {

    @Inject
    ViewModelFactory viewModelFactory;
    private ListView siteGradesListView;
    private List<Grade> grades;
    private String siteId;


    public static SiteGradesFragment newInstance(List<Grade> grades, String siteId) {

        SiteGradesFragment siteGradesFragment = new SiteGradesFragment();
        siteGradesFragment.grades = grades;
        siteGradesFragment.siteId = siteId;
        return siteGradesFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //inflates the view
        final View view = inflater.inflate(R.layout.fragment_site_grades, null);
        this.siteGradesListView = view.findViewById(R.id.site_grades_list_view);

        feedGradesIntoListView();

        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            ViewModelProviders.of(getActivity(), viewModelFactory)
                    .get(GradeViewModel.class)
                    .refreshSiteData(this.siteId);
        });


        return view;

    }

    /**
     * Puts the grades of the current course into an adapter and adds the adapter to the
     * list view
     */
    public void feedGradesIntoListView() {

        if (this.grades != null) {
            //puts grades into custom adapter
            GradeItemAdapter adapter = new GradeItemAdapter(getActivity(), this.grades);
            siteGradesListView.setAdapter(adapter);
        } else {
            Toast.makeText(getContext(), "No grades found", Toast.LENGTH_SHORT).show();
        }
    }


}