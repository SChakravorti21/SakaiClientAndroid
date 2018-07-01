package com.example.development.sakaiclientandroid.fragments.assignments;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.development.sakaiclientandroid.R;
import com.example.development.sakaiclientandroid.api_models.assignments.AssignmentObject;
import com.example.development.sakaiclientandroid.utils.custom.CustomLinkMovementMethod;

import org.w3c.dom.Text;

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
        FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.fragment_single_assignment,
                                                    container, false);

        // Set assignment text header
        TextView titleView = layout.findViewById(R.id.assignment_name);
        titleView.setText(assignment.getTitle());

        // Set the assignment due date
        TextView dueDateView = layout.findViewById(R.id.assignment_date);
        dueDateView.setText("Due: " + assignment.getDueTimeString());

        // Set details of body
        TextView statusView = layout.findViewById(R.id.assignment_status);
        statusView.setText(assignment.getStatus());

        TextView maxGradeView = layout.findViewById(R.id.assignment_max_grade);
        maxGradeView.setText(assignment.getGradeScaleMaxPoints());

        TextView allowsResubView = layout.findViewById(R.id.assignment_allows_resubmission);
        allowsResubView.setText(assignment.getAllowResubmission() ? "Yes" : "No");

        // fromHtml(String) was deprecated in android N, so check the build version
        //before converting the html to text
        TextView descriptionView = layout.findViewById(R.id.assignment_description);
        String instructions = assignment.getInstructions();
        Spanned description;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            description = Html.fromHtml(instructions + instructions, Html.FROM_HTML_MODE_LEGACY);
        } else {
            description = Html.fromHtml(instructions + instructions);
        };
        descriptionView.setText(description);
        descriptionView.setMovementMethod(CustomLinkMovementMethod.getInstance());

        return layout;
    }

}
