package com.example.development.sakaiclientandroid.fragments.assignments;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.development.sakaiclientandroid.R;
import com.example.development.sakaiclientandroid.api_models.assignments.Access;
import com.example.development.sakaiclientandroid.api_models.assignments.AssignmentObject;
import com.example.development.sakaiclientandroid.api_models.assignments.Attachment;
import com.example.development.sakaiclientandroid.utils.custom.CustomLinkMovementMethod;

import org.w3c.dom.Text;

import java.util.List;

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

        // Set assignment text header, due date, assignments details (eg. status), etc.
        constructTextView(layout, R.id.assignment_name, assignment.getTitle());
        constructTextView(layout, R.id.assignment_date,
                "Due: " + assignment.getDueTimeString());
        constructTextView(layout, R.id.assignment_status, assignment.getStatus());
        constructTextView(layout, R.id.assignment_max_grade, assignment.getGradeScaleMaxPoints());
        constructTextView(layout, R.id.assignment_allows_resubmission,
                assignment.getAllowResubmission() ? "Yes" : "No");

        // Show the attachments for the assignment
        constructAttachmentsView(layout);
        // Create the assignment description
        constructDescriptionView(layout);

        return layout;
    }

    private void constructAttachmentsView(FrameLayout layout) {
        List<Attachment> attachments = assignment.getAttachments();

        StringBuilder attachmentsString = new StringBuilder();
        for(Attachment attachment : attachments) {
            attachmentsString.append("<p><a href=\"")
                             .append(attachment.getUrl())
                             .append("\">")
                             .append(attachment.getName())
                             .append("</a></p>");
        }

        Spanned attachmentBody = getSpannedFromHtml(attachmentsString.toString());
        TextView attachmentsView = layout.findViewById(R.id.assignment_attachments);
        attachmentsView.setText(attachmentBody);
        attachmentsView.setMovementMethod(CustomLinkMovementMethod.getInstance());
    }

    private void constructDescriptionView(FrameLayout layout) {
        // fromHtml(String) was deprecated in android N, so check the build version
        //before converting the html to text
        String instructions = assignment.getInstructions();
        Spanned description = getSpannedFromHtml(instructions);

        TextView descriptionView = layout.findViewById(R.id.assignment_description);
        descriptionView.setText(description);
        descriptionView.setMovementMethod(CustomLinkMovementMethod.getInstance());
    }

    private Spanned getSpannedFromHtml(String instructions) {
        Spanned description;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            description = Html.fromHtml(instructions, Html.FROM_HTML_MODE_LEGACY);
        } else {
            description = Html.fromHtml(instructions);
        }
        return description;
    }

    private void constructTextView(FrameLayout layout, int assignment_name, String text) {
        TextView textView = layout.findViewById(assignment_name);
        textView.setText(text);
    }

}
