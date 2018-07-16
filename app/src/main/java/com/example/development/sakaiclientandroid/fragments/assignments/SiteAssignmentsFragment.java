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
 * A simple {@link Fragment} subclass.
 */
public class SiteAssignmentsFragment extends Fragment {

    public static String ASSIGNMENT_NUMBER = "ASSIGNMENT_NUMBER";

    private List<Assignment> assignments;
    private int initialPosition;

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

        ViewPager assignmentsPager = layout.findViewById(R.id.assignment_viewpager);
        AssignmentsPagerAdapter pagerAdapter =
                new AssignmentsPagerAdapter(getActivity().getSupportFragmentManager(),
                                            assignments);
        assignmentsPager.setAdapter(pagerAdapter);
        assignmentsPager.setCurrentItem(initialPosition);

        // Set up the bottom indicators to show that there are multiple assignments
        // that can be viewed
        TabLayout indicators = layout.findViewById(R.id.view_pager_indicators);
        indicators.setupWithViewPager(assignmentsPager, true);

        return layout;
    }

}
