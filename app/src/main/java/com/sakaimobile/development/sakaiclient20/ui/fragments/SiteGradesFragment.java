package com.sakaimobile.development.sakaiclient20.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.sakaimobile.development.sakaiclient20.R;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Grade;
import com.sakaimobile.development.sakaiclient20.ui.adapters.SiteGradeAdapter;
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.GradeViewModel;
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.ViewModelFactory;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import dagger.android.support.AndroidSupportInjection;

public class SiteGradesFragment extends BaseFragment {

    @Inject ViewModelFactory viewModelFactory;
    private GradeViewModel gradeViewModel;

    private String siteId;
    private ProgressBar spinner;
    private SiteGradeAdapter adapter;
    private ListView siteGradesListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        siteId = getArguments().getString(getString(R.string.siteid_tag));
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
        gradeViewModel = ViewModelProviders.of(this, viewModelFactory).get(GradeViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_site_grades, null);

        spinner = view.findViewById(R.id.progress_circular);
        spinner.setVisibility(View.VISIBLE);
        siteGradesListView = view.findViewById(R.id.site_grades_list_view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.initRefreshFailureListener(gradeViewModel, () -> {
            this.spinner.setVisibility(View.GONE);
            this.siteGradesListView.setVisibility(View.VISIBLE);
            return null;
        });

        // get the live data
        gradeViewModel.getSiteGrades(siteId)
                .observe(getViewLifecycleOwner(), grades -> {
                    updateGradesList(grades);
                    spinner.setVisibility(View.GONE);
                    siteGradesListView.setVisibility(View.VISIBLE);
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        spinner = null;
        adapter = null;
        siteGradesListView = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.refresh_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                gradeViewModel.refreshSiteData(siteId);
                spinner.setVisibility(View.VISIBLE);
                siteGradesListView.setVisibility(View.GONE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Puts the grades of the current course into an adapter and adds the adapter to the
     * list view
     */
    public void updateGradesList(List<Grade> grades) {
        if (grades != null && grades.size() > 0) {
            // Only create the adapter if necessary, otherwise re-use the same one
            if(adapter == null) {
                adapter = new SiteGradeAdapter(getActivity(), grades);
                siteGradesListView.setAdapter(adapter);
            } else {
                adapter.clear();
                adapter.addAll(grades);
                adapter.notifyDataSetChanged();
            }
        } else {
            Toast.makeText(getActivity(), "No grades found", Toast.LENGTH_SHORT).show();
        }
    }

}
