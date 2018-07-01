package com.example.development.sakaiclientandroid.fragments.assignments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.development.sakaiclientandroid.R;
import com.example.development.sakaiclientandroid.api_models.assignments.AssignmentObject;

import static com.example.development.sakaiclientandroid.NavActivity.ASSIGNMENTS_TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class SingleAssignmentFragment extends Fragment {

    AssignmentObject assignment;

    public SingleAssignmentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if(arguments != null) {
            this.assignment = (AssignmentObject) arguments.getSerializable(ASSIGNMENTS_TAG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        CardView cardView = (CardView) inflater.inflate(R.layout.fragment_single_assignment,
                                                    container, false);

        // Set assignment text header
        TextView titleView = cardView.findViewById(R.id.assignment_name);
        titleView.setText(assignment.getTitle());

        return cardView;
    }

}
