package com.example.development.sakaiclientandroid.fragments.assignments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.development.sakaiclientandroid.R;
import com.example.development.sakaiclientandroid.api_models.assignments.AssignmentObject;
import com.example.development.sakaiclientandroid.utils.custom.AssignmentsPagerAdapter;

import java.util.List;

import static com.example.development.sakaiclientandroid.NavActivity.ASSIGNMENTS_TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class CourseAssignmentsFragment extends Fragment {

    private List<AssignmentObject> assignments;

    public CourseAssignmentsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if(arguments != null) {
            assignments = (List<AssignmentObject>) arguments.getSerializable(ASSIGNMENTS_TAG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.fragment_course_assignments,
                                                            container, false);

        ViewPager assignmentsPager = layout.findViewById(R.id.assignment_viewpager);
        AssignmentsPagerAdapter pagerAdapter =
                new AssignmentsPagerAdapter(getActivity().getSupportFragmentManager(),
                                            assignments);
        assignmentsPager.setAdapter(pagerAdapter);

        return layout;
    }

}
