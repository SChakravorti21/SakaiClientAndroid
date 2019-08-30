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

import java.util.List;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Shoumyo Chakravorti on 7/8/18.
 *
 * Performs much of the same functions as {@link CourseViewHolder},
 * except this type of a {@link com.unnamed.b.atv.model.TreeNode.BaseNodeViewHolder}
 * holds {@link Assignment} objects that have been sorted by date within their own terms.
 *
 * @see CourseViewHolder
 */

public class AssignmentTermHeaderViewHolder
        extends TreeNode.BaseNodeViewHolder<AssignmentTermHeaderViewHolder.TermHeaderItem> {

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
    public AssignmentTermHeaderViewHolder(Context context) {
        super(context);
    }

    /**
     * Inflates the view for this node, with the {@link RecyclerView} collapsed
     * by default.
     * @param node The node that dictates this view's layout
     * @param value The {@link CourseViewHolder.CourseHeaderItem} used to fill the views
     * @return A {@link View} representing this node
     */
    @Override
    public View createNodeView(TreeNode node, AssignmentTermHeaderViewHolder.TermHeaderItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.tree_node_term_recycler_view, null, false);

        // The course name
        TextView courseView = view.findViewById(R.id.term_name);
        courseView.setText(value.termName);

        // Initialize the RecyclerView with its data
        recyclerView = view.findViewById(R.id.assignments_recycler_view);
        recyclerView.setVisibility(View.GONE); // initially hide the RecyclerView
        recyclerView.setHasFixedSize(true); // supposedly improves performance
        recyclerView.setAdapter(new TreeAssignmentAdapter(value.assignments));

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
        arrowView.setText(CHEVRON_RIGHT);

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

        view.setLayoutParams(new LinearLayoutCompat.LayoutParams(
                widthPx,
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT
        ));

        // Don't want bottom border if this is the last term (looks kinda weird)
        if(node.isLastChild())
            view.findViewById(R.id.term_name_container).setBackgroundResource(R.color.secondaryBackgroundColor);

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
     * Represents the content for a single {@link AssignmentTermHeaderViewHolder} node.
     * Contains the course name, course courseIcon, and the list of {@link Assignment}s for the course.
     */
    public static class TermHeaderItem {
        private String termName;
        private List<Assignment> assignments;

        public TermHeaderItem(String text, List<Assignment> assignments) {
            this.termName = text;
            this.assignments = assignments;
        }
    }
}
