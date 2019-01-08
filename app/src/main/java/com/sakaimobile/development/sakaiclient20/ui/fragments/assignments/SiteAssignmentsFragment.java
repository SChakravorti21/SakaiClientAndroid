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
import android.view.View;
import android.view.ViewGroup;

import com.sakaimobile.development.sakaiclient20.R;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Assignment;
import com.sakaimobile.development.sakaiclient20.ui.activities.MainActivity;
import com.sakaimobile.development.sakaiclient20.ui.adapters.AssignmentsPagerAdapter;
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.AssignmentViewModel;
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.ViewModelFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    private ViewPager assignmentsPager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.siteIds = new ArrayList<>();

        Bundle arguments = getArguments();
        if(arguments != null) {
            List<Assignment> assignments = (List<Assignment>) arguments.getSerializable(MainActivity.ASSIGNMENTS_TAG);
            initialPosition = arguments.getInt(ASSIGNMENT_NUMBER, 0);

            Set<String> siteIdSet = new HashSet<>();
            for(Assignment assignment : assignments)
                siteIdSet.add(assignment.siteId);
            this.siteIds.addAll(siteIdSet);
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

        // Find the ViwPager and give it the adapter that tells how to instantiate
        // Fragments for each assignment
        this.assignmentsPager = layout.findViewById(R.id.assignment_viewpager);

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
                    AssignmentsPagerAdapter pagerAdapter =
                            new AssignmentsPagerAdapter(getActivity().getSupportFragmentManager(),
                                    assignments);
                    this.assignmentsPager.setAdapter(pagerAdapter);

                    // Even if initial position was not provided, it will default to zero and
                    // show the first assignment
                    this.assignmentsPager.setCurrentItem(initialPosition);
                });
    }
}
