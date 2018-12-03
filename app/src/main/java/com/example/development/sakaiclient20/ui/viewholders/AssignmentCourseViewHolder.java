package com.example.development.sakaiclient20.ui.viewholders;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.TextView;

import com.example.development.sakaiclient20.R;
import com.example.development.sakaiclient20.persistence.entities.Assignment;
import com.example.development.sakaiclient20.ui.adapters.AssignmentAdapter;
import com.unnamed.b.atv.model.TreeNode;

import java.util.List;

/**
 * Created by Shoumyo Chakravorti on 6/9/18.
 *
 * A subclass of {@link com.unnamed.b.atv.model.TreeNode.BaseNodeViewHolder} that acts as a
 * specialized {@link android.support.v7.widget.RecyclerView.ViewHolder} for a course that
 * contains {@link Assignment} objects. There are two halves to the view: the course header
 * indicating the course, and the {@link RecyclerView} that contains the cards for the assignments.
 * Assignments are populated using an {@link AssignmentAdapter}, and clicking an {@link Assignment}
 * card opens up a
 * {@link com.example.development.sakaiclient20.ui.fragments.assignments.SiteAssignmentsFragment}
 * that has been focused to the selected assignment's
 * {@link com.example.development.sakaiclient20.ui.fragments.assignments.SingleAssignmentFragment}.
 */
public class AssignmentCourseViewHolder extends TreeNode.BaseNodeViewHolder<AssignmentCourseViewHolder.CourseHeaderItem> {

    private static final String CHEVRON_DOWN = "\uF078";
    private static final String CHEVRON_RIGHT = "\uF054";

    /**
     * The {@link TextView} that indicates whether the node is expanded or collapsed.
     */
    private TextView arrowView;

    /**
     * The {@link RecyclerView} that conatains all cards for the {@link Assignment} objects.
     */
    private RecyclerView recyclerView;

    /**
     * Mandatory {@link com.unnamed.b.atv.model.TreeNode.BaseNodeViewHolder} constructor.
     * @param context The context of the tree view.
     */
    public AssignmentCourseViewHolder(Context context) {
        super(context);
    }

    /**
     * Inflates the view for this node, with the {@link RecyclerView} collapsed
     * by default.
     * @param node The node that dictates this view's layout
     * @param value The {@link CourseHeaderItem} used to fill the views
     * @return A {@link View} representing this node
     */
    @Override
    public View createNodeView(TreeNode node, CourseHeaderItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.course_with_recycler_view,
                null, false);

        // The course courseIcon
        TextView iconView = view.findViewById(R.id.course_icon);
        iconView.setText(value.courseIcon);

        // The course name
        TextView courseView = view.findViewById(R.id.course_name);
        courseView.setText(value.courseName);

        // Initialize the RecyclerView with its data
        recyclerView = view.findViewById(R.id.assignments_recycler_view);
        recyclerView.setVisibility(View.GONE); // initially hide the RecyclerView
        recyclerView.setHasFixedSize(true); // supposedly improves performance
        recyclerView.setAdapter(new AssignmentAdapter(value.assignments));

        // The RecyclerView should only occupy one row, so use a GridLayoutManager
        // to dictate this style of a layout.
        GridLayoutManager layoutManager = new GridLayoutManager(
                context,
                1, // span count here is number of rows
                GridLayoutManager.HORIZONTAL, // fill cards from left to right
                false // do not reverse the layout
        );

        recyclerView.setLayoutManager(layoutManager);

        // Initialize the arrow view for toggling the list
        arrowView = view.findViewById(R.id.arrow_image);
        if(node.getLevel() < 4) {
            arrowView.setText(CHEVRON_RIGHT);
        } else {
            arrowView.setVisibility(View.GONE);
            arrowView = null;
        }

        // Need to programmatically define the width as being the device
        // screen width since there was no container that we could inflate the
        // view relative to.
        Resources r = inflater.getContext().getResources();

        // Convert pixels to density-independent units
        int widthPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                r.getDisplayMetrics().widthPixels,
                r.getDisplayMetrics()
        );

        LinearLayoutCompat.LayoutParams params =  new LinearLayoutCompat.LayoutParams(
                widthPx,
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT
        );

        view.setLayoutParams(params);
        return view;
    }

    /**
     * Toggles the node, expanding or collapsing the {@link RecyclerView} as necessary.
     * @param active Whether the node should be expanded
     */
    @Override
    public void toggle(boolean active) {
        if(arrowView == null)
            return;

        if(active) {
            arrowView.setText(CHEVRON_DOWN);
            recyclerView.setVisibility(View.VISIBLE);
            expandRecyclerView();
        } else {
            arrowView.setText(CHEVRON_RIGHT);
            collapseRecyclerView();
        }
    }

    /**
     * Expands the {@link RecyclerView} by measuring its initial and target height,
     * then applying an {@link Animation} that increases the view height from the initial
     * height to the target height.
     */
    private void expandRecyclerView() {
        // Measure the layout's desired height (this step does not affect the
        // actual height, just measures what it would be if the below dimensions were applied).
        recyclerView.measure(RecyclerView.LayoutParams.MATCH_PARENT,
                             RecyclerView.LayoutParams.WRAP_CONTENT);
        final int targetHeight = recyclerView.getMeasuredHeight();

        // Initial height of 0 causes glitchy animation (presumably a height of 0
        // indicates that the view should fill its parent or the screen)
        recyclerView.getLayoutParams().height = 1;

        // Create the animation to expand the view
        Animation expansionAnimation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation transformation) {
                int newHeight = (int) ( targetHeight * interpolatedTime );
                recyclerView.getLayoutParams().height = newHeight;
                recyclerView.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Duration of 200 milliseconds is the same as the default toggle
        // animation time for all nodes
        expansionAnimation.setDuration(200);
        recyclerView.startAnimation(expansionAnimation);
    }

    /**
     * Performs the exact opposite of {@code expandRecyclerView}.
     */
    private void collapseRecyclerView() {
        // See expandRecyclerView for how this type of animation works.
        final int initialHeight = recyclerView.getMeasuredHeight();

        Animation collapseAnimation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation transformation) {
                if(interpolatedTime < 1.0) {
                    int newHeight = initialHeight - (int) (initialHeight * interpolatedTime);
                    recyclerView.getLayoutParams().height = newHeight;
                    recyclerView.requestLayout();
                } else {
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        collapseAnimation.setDuration(200);
        recyclerView.startAnimation(collapseAnimation);
    }

    /**
     * Represents the content for a single {@link AssignmentCourseViewHolder} node.
     * Contains the course name, course courseIcon, and the list of {@link Assignment}s for the course.
     */
    public static class CourseHeaderItem {
        private String courseName;
        private String courseIcon;

        /**
         * The assignments associated with the course.
         */
        private List<Assignment> assignments;

        public CourseHeaderItem(String courseName, String courseIcon, List<Assignment> assignments) {
            this.courseName = courseName;
            this.courseIcon = courseIcon;
            this.assignments = assignments;
        }
    }
}
