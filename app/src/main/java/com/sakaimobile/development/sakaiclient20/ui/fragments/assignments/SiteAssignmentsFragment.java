package com.sakaimobile.development.sakaiclient20.ui.fragments.assignments;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.sakaimobile.development.sakaiclient20.R;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Assignment;
import com.sakaimobile.development.sakaiclient20.ui.activities.MainActivity;
import com.sakaimobile.development.sakaiclient20.ui.adapters.AssignmentsPagerAdapter;
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.AssignmentViewModel;
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.ViewModelFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

/**
 * Created by Shoumyo Chakravorti.
 *
 * A {@link Fragment} subclass that represents all assignments for a particular
 * class. Assignments are represented through the {@link Assignment} model object
 * and are displayed with {@link SingleAssignmentFragment}s that are generated
 * through a {@link ViewPager}. The position of the current assignment
 * and panning to other assignments is supported through a {@link TabLayout}.
 */
public class SiteAssignmentsFragment extends Fragment {

    public static final String ASSIGNMENTS_TAG = "ASSIGNMENTS";

    /**
     * Tag for passing the active assignment position to this {@link Fragment}.
     */
    public static String ASSIGNMENT_NUMBER = "ASSIGNMENT_NUMBER";

    /**
     * The site IDs of the course(s) for which we will load assignments
     */
    private List<String> siteIds;

    /**
     * The position of the {@link Assignment} that should be shown first.
     * The position of the {@link ViewPager} is set to this index.
     */
    private int initialPosition;

    @Inject ViewModelFactory viewModelFactory;
    private AssignmentViewModel assignmentViewModel;

    private ProgressBar progressBar;
    private ViewPager assignmentsPager;
    private Map<String, String> mapSiteIdToSitePageUrl;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        this.siteIds = new ArrayList<>();

        Bundle arguments = getArguments();
        if(arguments != null) {
            List<Assignment> assignments = (List<Assignment>) arguments.getSerializable(ASSIGNMENTS_TAG);
            initialPosition = arguments.getInt(ASSIGNMENT_NUMBER, 0);

            this.mapSiteIdToSitePageUrl = new HashMap<>();
            for(Assignment assignment : assignments)
                this.mapSiteIdToSitePageUrl.put(assignment.siteId, assignment.assignmentSitePageUrl);
            this.siteIds.addAll(this.mapSiteIdToSitePageUrl.keySet());
        }
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);

        this.assignmentViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(AssignmentViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.fragment_course_assignments,
                                                            container, false);

        // Find the ViwPager and so that we can give it an adapter once LiveData triggers
        this.assignmentsPager = layout.findViewById(R.id.assignment_viewpager);
        this.progressBar = layout.findViewById(R.id.progressbar);
        this.progressBar.setVisibility(View.GONE);

        // Set up the bottom indicators to show that there are multiple assignments
        // that can be viewed
        TabLayout indicators = layout.findViewById(R.id.view_pager_indicators);
        indicators.setupWithViewPager(this.assignmentsPager, true);

        return layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.assignmentViewModel.getSiteAssignments(siteIds)
                .observe(getViewLifecycleOwner(), assignments -> {
                    if(assignments == null) {
                        Toast.makeText(getContext(), "No assignments found", Toast.LENGTH_LONG).show();
                        return;
                    }

                    for(Assignment assignment : assignments)
                        attachAssignmentSitePageUrl(assignment);

                    AssignmentsPagerAdapter pagerAdapter = new AssignmentsPagerAdapter(
                            getActivity().getSupportFragmentManager(),
                            assignments);
                    this.assignmentsPager.setAdapter(pagerAdapter);

                    // Even if initial position was not provided, it will default to zero and
                    // show the first assignment
                    this.assignmentsPager.setCurrentItem(initialPosition);
                    this.progressBar.setVisibility(View.GONE);
                });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.site_assignments_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_refresh:
                this.initialPosition = this.assignmentsPager.getCurrentItem();
                this.assignmentViewModel.refreshSiteData(this.siteIds);
                this.progressBar.setVisibility(View.VISIBLE);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void attachAssignmentSitePageUrl(Assignment assignment) {
        assignment.assignmentSitePageUrl = this.mapSiteIdToSitePageUrl.get(assignment.siteId);
    }
}
