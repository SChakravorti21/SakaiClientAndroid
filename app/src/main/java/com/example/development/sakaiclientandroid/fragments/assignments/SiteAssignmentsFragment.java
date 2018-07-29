package com.example.development.sakaiclientandroid.fragments.assignments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.development.sakaiclientandroid.R;
import com.example.development.sakaiclientandroid.api_models.assignments.Assignment;
import com.example.development.sakaiclientandroid.utils.custom.AssignmentsPagerAdapter;

import java.util.List;

import static com.example.development.sakaiclientandroid.NavActivity.ASSIGNMENTS_TAG;

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
     * The assignments for this course.
     */
    private List<Assignment> assignments;

    /**
     * The position of the {@link Assignment} that should be shown first.
     * The position of the {@link ViewPager} is set to this index.
     */
    private int initialPosition;

    /**
     * Mandatory empty constructor.
     */
    public SiteAssignmentsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if(arguments != null) {
            assignments = (List<Assignment>) arguments.getSerializable(ASSIGNMENTS_TAG);
            initialPosition = arguments.getInt(ASSIGNMENT_NUMBER, 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.fragment_course_assignments,
                                                            container, false);

        // Find the ViwPager and give it the adapter that tells how to instantiate
        // Fragments for each assignment
        ViewPager assignmentsPager = layout.findViewById(R.id.assignment_viewpager);
        AssignmentsPagerAdapter pagerAdapter =
                new AssignmentsPagerAdapter(getActivity().getSupportFragmentManager(),
                                            assignments);
        assignmentsPager.setAdapter(pagerAdapter);

        // Even if initial position was not provided, it will default to zero and
        // show the first assignment
        assignmentsPager.setCurrentItem(initialPosition);

        // Set up the bottom indicators to show that there are multiple assignments
        // that can be viewed
        TabLayout indicators = layout.findViewById(R.id.view_pager_indicators);
        indicators.setupWithViewPager(assignmentsPager, true);

        return layout;
    }

}
