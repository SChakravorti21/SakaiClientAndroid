package com.example.development.sakaiclientandroid.utils.custom;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.development.sakaiclientandroid.R;
import com.example.development.sakaiclientandroid.api_models.assignments.Assignment;
import com.example.development.sakaiclientandroid.fragments.assignments.SiteAssignmentsFragment;

import java.io.Serializable;
import java.util.List;

import static com.example.development.sakaiclientandroid.NavActivity.ASSIGNMENTS_TAG;
import static com.example.development.sakaiclientandroid.fragments.assignments.SiteAssignmentsFragment.ASSIGNMENT_NUMBER;

/**
 * Created by Shoumyo Chakravorti on 6/23/18.
 *
 * An {@link android.support.v7.widget.RecyclerView.Adapter} for a {@link RecyclerView}
 * that holds several small {@link CardView}s to represent {@link Assignment} objects,
 * either sorted by date for a given term or sorted within their courses.
 *
 * When an assignment is clicked, this adapter instantiates a {@link SiteAssignmentsFragment}
 * focused on the clicked {@link Assignment} as a
 * {@link com.example.development.sakaiclientandroid.fragments.assignments.SingleAssignmentFragment},
 * allowing the user to view the full assignments and view other assignments of the course.
 */
public class AssignmentAdapter extends RecyclerView.Adapter {

    /**
     * The {@link Assignment} objects for this term or course.
     */
    private List<Assignment> assignments;

    /**
     * Constructor to keep instantiate the {@link Assignment} objects to create views.
     * @param assignments the list of {@link Assignment}s for this {@link RecyclerView}
     */
    public AssignmentAdapter(List<Assignment> assignments) {
        this.assignments = assignments;
    }

    /**
     * Returns the size of your data set (invoked by the layout manager)
     * @return the number of assignments represented by this
     *  {@link android.support.v7.widget.RecyclerView.Adapter}
     */
    @Override
    public int getItemCount() {
        return assignments.size();
    }

    /**
     * Invoked by the {@link android.support.v7.widget.RecyclerView.LayoutManager},
     * allows the {@link RecyclerView} to instantiate views for the assignments.
     * @param parent the parent {@link ViewGroup} to attach the {@link View} to
     * @param viewType the type of {@link android.support.v7.widget.RecyclerView.ViewHolder}
     *                 to create (unused)
     * @return A {@link AssignmentViewHolder} representing an {@link Assignment} (but not a specifically
     * any single assignment).
     */
    @Override
    public AssignmentViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view. The entire CardView must be passed into
        // the AssignmentViewHolder constructor since it is the parent of all inner elements.
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        CardView view = (CardView) inflater.inflate(R.layout.assignment_cell_layout, parent,
                false);

        // Inflate the options menu (mainly just used for expanding the card)
        TextView expandButton = view.findViewById(R.id.assignment_expand_button);
        return new AssignmentViewHolder(view, expandButton);
    }

    /**
     * Invoked by the {@link android.support.v7.widget.RecyclerView.LayoutManager},
     * binds the data of an {@link Assignment} to its {@link View} through the
     * {@link AssignmentViewHolder}.
     * @param holder the {@link AssignmentViewHolder} with the sub-{@link View}s to populate
     * @param position the index of the {@link Assignment} in {@code assignments}.
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        AssignmentViewHolder viewHolder = (AssignmentViewHolder) holder;

        // Update the AssignmentViewHolder's position so that the click
        // listener can dictate how to initialize the SiteAssignmentsFragment
        viewHolder.setPosition(position);

        // Set the assignment title
        Assignment assignment = assignments.get(position);
        viewHolder.titleView.setText(assignment.getTitle());

        // Set the assignment description
        // fromHtml(String) was deprecated in android N, so check the build version
        // before converting the html to text
        String instructions = assignment.getInstructions();
        Spanned description;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            description = Html.fromHtml(instructions, Html.FROM_HTML_MODE_LEGACY);
        } else {
            description = Html.fromHtml(instructions);
        };
        viewHolder.descriptionView.setText(description);
        viewHolder.descriptionView.setMovementMethod(CustomLinkMovementMethod.getInstance());

        // Set the assignment due date
        viewHolder.dueDateView.setText("Due: " + assignment.getDueTime().getDisplay());
    }


    /**
     * Subclasses {@link android.support.v7.widget.RecyclerView.ViewHolder} to manage
     * the {@link View} for a single {@link Assignment}.
     */
    public class AssignmentViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        /**
         * The assignment title view in the {@link CardView} header
         */
        private TextView titleView;

        /**
         * The assignment description view in the main {@link CardView} body
         */
        private TextView descriptionView;

        /**
         * The view for the assignment due date in the {@link CardView} footer.
         */
        private TextView dueDateView;
        private int position;

        /**
         * Instantiates the {@link AssignmentViewHolder} with an inflated
         * {@link CardView} to represent the {@link Assignment} and
         * the {@link TextView} that expands the assignment on being clicked.
         * @param cardView the parent view
         * @param expandButton the {@link TextView} that expands the assignment upon being clicked
         */
        private AssignmentViewHolder(CardView cardView, View expandButton) {
            super(cardView);
            this.titleView = cardView.findViewById(R.id.assignment_name);
            this.descriptionView = cardView.findViewById(R.id.assignment_description);
            this.dueDateView = cardView.findViewById(R.id.assignment_date);

            // Set the click listener on the header and footer
            cardView.setOnClickListener(this);
            titleView.setOnClickListener(this);
            descriptionView.setOnClickListener(this);
            dueDateView.setOnClickListener(this);
            expandButton.setOnClickListener(this);
        }

        /**
         * Sets the position of this {@link Assignment} in the {@link RecyclerView}.
         * Necessary for automatically panning to this {@link Assignment} when
         * the {@link SiteAssignmentsFragment} is shown.
         * @param position the index of this assignment
         */
        private void setPosition(int position) {
            this.position = position;
        }

        /**
         * Expands the assignment when it is clicked
         * @param view the view that is clicked
         */
        @Override
        public void onClick(View view) {
            this.expandAssignment( (AppCompatActivity) view.getContext() );
        }

        /**
         * Expands the assignment by instantiating a {@link SiteAssignmentsFragment}
         * with the current course's {@link Assignment}s and auto-focusing to the
         * assignment that has been clicked.
         * @param activity the parent {@link AppCompatActivity}
         */
        private void expandAssignment(AppCompatActivity activity) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(ASSIGNMENTS_TAG, (Serializable) assignments);
            bundle.putInt(ASSIGNMENT_NUMBER, position);

            SiteAssignmentsFragment fragment = new SiteAssignmentsFragment();
            fragment.setArguments(bundle);

            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.anim_fade_in,
                            R.anim.anim_fade_out,
                            R.anim.anim_fade_in,
                            R.anim.anim_fade_out)
                    // Add instead of replacing so that the state of opened assignments
                    // remains the same after returning
                    .add(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

}
