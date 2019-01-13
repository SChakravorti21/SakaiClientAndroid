package com.sakaimobile.development.sakaiclient20.ui.fragments.assignments;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sakaimobile.development.sakaiclient20.R;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Assignment;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Attachment;
import com.sakaimobile.development.sakaiclient20.ui.activities.MainActivity;
import com.sakaimobile.development.sakaiclient20.ui.custom_components.CustomLinkMovementMethod;

import java.util.List;

/**
 * Created by Shoumyo Chakravorti.
 *
 * A {@link Fragment} subclass that represents a single
 * {@link Assignment} as a large {@link android.support.v7.widget.CardView}.
 */
public class SingleAssignmentFragment extends Fragment implements View.OnClickListener {

    public static final String ASSIGNMENT_TAG = "ASSIGNMENT_TAG";

    /**
     * The {@link Assignment} that feeds this {@link Fragment}'s data.
     */
    Assignment assignment;

    /**
     * Mandatory empty constructor
     */
    public SingleAssignmentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the assignment for this fragment
        this.assignment = (Assignment) getArguments().getSerializable(SingleAssignmentFragment.ASSIGNMENT_TAG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.fragment_single_assignment,
                                                    container, false);

        // Set assignment text header, due date, assignments details (eg. status), etc.
        constructTextView(layout, R.id.assignment_name, assignment.title);
        constructTextView(layout, R.id.assignment_date,
                "Due: " + assignment.dueTime.toString());
        constructTextView(layout, R.id.assignment_status, assignment.status);
        constructTextView(layout, R.id.assignment_max_grade, assignment.gradeScaleMaxPoints);
        constructTextView(layout, R.id.assignment_allows_resubmission,
                assignment.allowResubmission ? "Yes" : "No");

        // Show the attachments for the assignment
        constructAttachmentsView(layout);
        // Create the assignment description
        constructDescriptionView(layout);

        layout.findViewById(R.id.assignment_submit_button).setOnClickListener(this);
        layout.findViewById(R.id.assignment_close_button).setOnClickListener(this);

        return layout;
    }

    /**
     * Handles click events, with two main events that are fired:
     *  1) Opening the submission dialog (with an embeded
     *      {@link com.sakaimobile.development.sakaiclient20.ui.custom_components.FileCompatWebView})
     *      for assignment submission
     *  2) Closing the assignment card when the "X" button is clicked (this also
     *      has the effect of removing the entire {@link SiteAssignmentsFragment}
     *      this this {@link SingleAssignmentFragment} resides within to return to the
     *      screen that directed us to this view.
     * @param view The {@link View} that is clicked
     */
    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        switch (viewId) {
            case R.id.assignment_submit_button:
                showSubmissionSheet();
                break;
            case R.id.assignment_close_button:
                // Return to the previous fragment in the back stack
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                activity.onBackPressed();
                break;
        }
    }

    /**
     * Shows the assignment submission dialog by instantiating a
     * {@link AssignmentSubmissionDialogFragment} that behaves like a
     * {@link android.support.design.widget.BottomSheetDialog} when the
     * {@link android.support.v4.app.FragmentManager} shows it.
     */
    private void showSubmissionSheet() {
        String assignmentSubmissionUrl =
                String.format("%s?assignmentReference=%s&sakai_action=doView_submission",
                        assignment.assignmentSitePageUrl,
                        assignment.assignmentId
                );

        Bundle arguments = new Bundle();
        arguments.putString(AssignmentSubmissionDialogFragment.URL_PARAM, assignmentSubmissionUrl);

        // Instantiate bottom sheet dialog fragment
        BottomSheetDialogFragment dialogFragment = new AssignmentSubmissionDialogFragment();
        dialogFragment.setArguments(arguments);

        // Inherited method of BottomSheetDialogFragment "show" allows
        // the sheet to be shown very easily
        dialogFragment.show(getActivity().getSupportFragmentManager(), null);
    }

    /**
     * Constructs the attachments {@link TextView} to display the downloadable
     * {@link Attachment} objects for an {@link Assignment}. A {@link TextView}
     * can accept a {@link Spanned} object representing some HTML, so {@code a}
     * tags are used to make the attachments clickable, which in turn opens
     * a {@link com.sakaimobile.development.sakaiclient20.ui.fragments.WebFragment}
     * (initiated by the {@link android.text.method.MovementMethod}
     * {@link CustomLinkMovementMethod}).
     * @param layout The layout containing the attachments {@link TextView}
     */
    private void constructAttachmentsView(FrameLayout layout) {
        List<Attachment> attachments = assignment.attachments;
        TextView attachmentsView = layout.findViewById(R.id.assignment_attachments);

        // If there are no attachments, do not show the attachments view.
        // Otherwise, construct the HTML and set the TextView's content from that.
        if(attachments == null || attachments.size() == 0) {
            attachmentsView.setText(
                    getSpannedFromHtml("<p>No attachments found for this assignment.</p>"));
        } else {
            StringBuilder attachmentsString = new StringBuilder();
            for (Attachment attachment : attachments) {
                attachmentsString.append("<p><a href=\"")
                        .append(attachment.url)
                        .append("\">")
                        .append(attachment.name)
                        .append("</a></p>");
            }

            Spanned attachmentBody = getSpannedFromHtml(attachmentsString.toString());
            attachmentsView.setText(attachmentBody);

            // The MovementMethod handles creation of a WebFragment
            // whenever a URLSpan is clicked (the WebFragment handles
            // download of attachments if it is possible).
            attachmentsView.setMovementMethod(CustomLinkMovementMethod.getInstance());
        }

    }

    /**
     * Constructs the main portion of the card, the description,
     * with the description of the assignment. Similar to
     * {@link SingleAssignmentFragment#constructAttachmentsView(FrameLayout)},
     * this creates an HTML {@link Spanned} object, and clicking on the
     * description may trigger creation of a {@link com.sakaimobile.development.sakaiclient20.ui.fragments.WebFragment}
     * if a link is clicked.
     * @param layout The parent layout containing the descriptions {@link TextView}
     */
    private void constructDescriptionView(FrameLayout layout) {
        // fromHtml(String) was deprecated in android N, so check the build version
        //before converting the html to text
        String instructions = assignment.instructions;
        Spanned description = getSpannedFromHtml(instructions);

        TextView descriptionView = layout.findViewById(R.id.assignment_description);
        descriptionView.setText(description);
        descriptionView.setMovementMethod(CustomLinkMovementMethod.getInstance());
    }

    /**
     * Takes an HTML {@link String} and converts it into a {@link Spanned}
     * object based on the user's build version.
     * @param text The text to convert
     * @return The {@link Spanned} object that can be used to set the content of a {@link TextView}.
     */
    private Spanned getSpannedFromHtml(String text) {
        Spanned description;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            description = Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY);
        } else {
            description = Html.fromHtml(text);
        }
        return description;
    }

    /**
     * A helper method that allows for setting the {@code text} of any {@link TextView}
     * residing within a parent {@code layout}.
     * @param layout The parent layout containing the {@link TextView}
     * @param assignmentResourceId The ID of the {@link TextView} inside the parent layout
     * @param text The text for the view
     */
    private void constructTextView(FrameLayout layout, int assignmentResourceId, String text) {
        TextView textView = layout.findViewById(assignmentResourceId);
        textView.setText(text);
    }
}
