package com.sakaimobile.development.sakaiclient20.ui.viewholders;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.sakaimobile.development.sakaiclient20.R;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Assignment;
import com.sakaimobile.development.sakaiclient20.ui.adapters.TreeAssignmentAdapter;
import com.sakaimobile.development.sakaiclient20.ui.helpers.TreeAnimationUtils;
import com.unnamed.b.atv.model.TreeNode;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Shoumyo Chakravorti on 6/9/18.
 *
 * A subclass of {@link com.unnamed.b.atv.model.TreeNode.BaseNodeViewHolder} that acts as a
 * specialized {@link RecyclerView.ViewHolder} for a course that
 * contains {@link Assignment} objects. There are two halves to the view: the course header
 * indicating the course, and the {@link RecyclerView} that contains the cards for the assignments.
 * Assignments are populated using an {@link TreeAssignmentAdapter}, and clicking an {@link Assignment}
 * card opens up a
 * {@link com.sakaimobile.development.sakaiclient20.ui.fragments.assignments.SiteAssignmentsFragment}
 * that has been focused to the selected assignment's
 * {@link com.sakaimobile.development.sakaiclient20.ui.fragments.assignments.SingleAssignmentFragment}.
 */
public class CourseViewHolder extends TreeNode.BaseNodeViewHolder<CourseViewHolder.CourseHeaderItem> {

    private static final String CHEVRON_DOWN = "\uF107";
    private static final String CHEVRON_RIGHT = "\uF105";

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
    public CourseViewHolder(Context context) {
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
        View view = inflater.inflate(R.layout.tree_node_course_recycler_view,
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
        recyclerView.setAdapter(value.adapter);
        recyclerView.setHasFixedSize(true); // supposedly improves performance

        RecyclerView.LayoutManager layoutManager;
        if(value.adapter instanceof TreeAssignmentAdapter) {
            // The RecyclerView should only occupy one row, so use a GridLayoutManager
            // to dictate this style of a layout.
            layoutManager = new GridLayoutManager(
                    context,
                    1, // span count here is number of rows
                    GridLayoutManager.HORIZONTAL, // fill cards from left to right
                    false // do not reverse the layout
            );
        } else {
            layoutManager = new LinearLayoutManager(context);
            // There is no need to scroll vertically (all items should fit inside the view)
            recyclerView.setVerticalScrollBarEnabled(false);
            // Scrolling is not needed anyways, and nested scrolling also messes up fling scroll
            recyclerView.setNestedScrollingEnabled(false);
        }
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
            TreeAnimationUtils.expandRecyclerView(recyclerView);
        } else {
            arrowView.setText(CHEVRON_RIGHT);
            TreeAnimationUtils.collapseRecyclerView(recyclerView);
        }
    }

    /**
     * Represents the content for a single {@link CourseViewHolder} node.
     * Contains the course name, course courseIcon, and the list of {@link Assignment}s for the course.
     */
    public static class CourseHeaderItem {
        private String courseName;
        private String courseIcon;
        private RecyclerView.Adapter adapter;

        public CourseHeaderItem(String courseName, String courseIcon, RecyclerView.Adapter adapter) {
            this.courseName = courseName;
            this.courseIcon = courseIcon;
            this.adapter = adapter;
        }
    }
}
