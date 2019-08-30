package com.sakaimobile.development.sakaiclient20.ui.adapters;

import android.content.Intent;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.sakaimobile.development.sakaiclient20.R;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Assignment;
import com.sakaimobile.development.sakaiclient20.ui.activities.SitePageActivity;
import com.sakaimobile.development.sakaiclient20.ui.fragments.assignments.SiteAssignmentsFragment;

import java.io.Serializable;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Shoumyo Chakravorti on 6/23/18.
 *
 * An {@link RecyclerView.Adapter} for a {@link RecyclerView}
 * that holds several small {@link CardView}s to represent {@link Assignment} objects,
 * either sorted by date for a given term or sorted within their courses.
 *
 * When an assignment is clicked, this adapter instantiates a {@link SiteAssignmentsFragment}
 * focused on the clicked {@link Assignment} as a
 * {@link com.sakaimobile.development.sakaiclient20.ui.fragments.assignments.SingleAssignmentFragment},
 * allowing the user to view the full assignments and view other assignments of the course.
 */
public class TreeAssignmentAdapter extends RecyclerView.Adapter<TreeAssignmentAdapter.AssignmentViewHolder> {

    /**
     * The {@link Assignment} objects for this term or course.
     */
    private List<Assignment> assignments;

    /**
     * The last item in the {@link RecyclerView} that was rendered (and animated)
     */
    private int lastRenderedPosition = -1;

    /**
     * Constructor to keep instantiate the {@link Assignment} objects to create views.
     * @param assignments the list of {@link Assignment}s for this {@link RecyclerView}
     */
    public TreeAssignmentAdapter(List<Assignment> assignments) {
        this.assignments = assignments;
    }

    /**
     * Returns the size of your data set (invoked by the layout manager)
     * @return the number of assignments represented by this
     *  {@link RecyclerView.Adapter}
     */
    @Override
    public int getItemCount() {
        return assignments.size();
    }

    /**
     * Invoked by the {@link RecyclerView.LayoutManager},
     * allows the {@link RecyclerView} to instantiate views for the assignments.
     * @param parent the parent {@link ViewGroup} to attach the {@link View} to
     * @param viewType the type of {@link RecyclerView.ViewHolder}
     *                 to create (unused)
     * @return A {@link AssignmentViewHolder} representing an {@link Assignment} (but not a specifically
     * any single assignment).
     */
    @Override
    public AssignmentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
     * Invoked by the {@link RecyclerView.LayoutManager},
     * binds the data of an {@link Assignment} to its {@link View} through the
     * {@link AssignmentViewHolder}.
     * @param viewHolder the {@link AssignmentViewHolder} with the sub-{@link View}s to populate
     * @param position the index of the {@link Assignment} in {@code assignments}.
     */
    @Override
    public void onBindViewHolder(AssignmentViewHolder viewHolder, int position) {
        // Update the AssignmentViewHolder's position so that the click
        // listener can dictate how to initialize the SiteAssignmentsFragment
        viewHolder.setPosition(position);

        // Set the assignment title
        Assignment assignment = assignments.get(position);
        viewHolder.titleView.setText(assignment.title);

        // Set the assignment description
        // fromHtml(String) was deprecated in android N, so check the build version
        // before converting the html to text
        String instructions = assignment.instructions;
        Spanned description;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            description = Html.fromHtml(instructions, Html.FROM_HTML_MODE_LEGACY);
        } else {
            description = Html.fromHtml(instructions);
        }
        viewHolder.descriptionView.setText(description);

        // Set the assignment due date
        viewHolder.dueDateView.setText("Due: " + assignment.dueTimeString);

        // Animate the view if it has not been done already
        if(position > lastRenderedPosition) {
            startAnimation(viewHolder.itemView, position);
        }
    }

    /**
     * When a view is detached from the window, its animation is cleared
     * to prevent issues with fast scrolling/flinging.
     * @param holder
     */
    @Override
    public void onViewDetachedFromWindow(AssignmentViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    /**
     * Animates the given view with the {@code grow_enter} animation.
     * Called whenever the view is binded for the first time. Updates the
     * {@code lastPositionRendered} after the animation is dispatched.
     * @param view The view to animate
     * @param position The position of the view that is being animated
     */
    private void startAnimation(View view, int position) {
        Animation growAnimation = AnimationUtils.loadAnimation(view.getContext(), R.anim.grow_enter);
        view.startAnimation(growAnimation);
        lastRenderedPosition = position;
    }

    /**
     * Subclasses {@link RecyclerView.ViewHolder} to manage
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
            // Start the SitePageActivity to expand all assignment cards
            Intent i = new Intent(activity, SitePageActivity.class);
            // Specify that we want to show the Assignments site page
            i.putExtra(activity.getString(R.string.site_type_tag), SitePageActivity.ASSIGNMENTS);
            i.putExtra(activity.getString(R.string.assignments_tag), (Serializable) assignments);
            i.putExtra(SiteAssignmentsFragment.INITIAL_VIEW_POSITION, position);

            activity.startActivity(i);
        }
    }

}
