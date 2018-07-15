package com.example.development.sakaiclientandroid.utils.holders;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.development.sakaiclientandroid.R;
import com.example.development.sakaiclientandroid.api_models.assignments.Assignment;
import com.example.development.sakaiclientandroid.utils.custom.AssignmentAdapter;
import com.unnamed.b.atv.model.TreeNode;

import java.util.List;

/**
 * Created by Development on 6/9/18.
 */

public class AssignmentCourseViewHolder extends TreeNode.BaseNodeViewHolder<AssignmentCourseViewHolder.CourseHeaderItem> {

    private static final String CHEVRON_DOWN = "\uF078";
    private static final String CHEVRON_RIGHT = "\uF054";

    private TextView arrowView;
    private RecyclerView recyclerView;

    public AssignmentCourseViewHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, CourseHeaderItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.course_with_recycler_view, null, false);

        TextView tvValue = view.findViewById(R.id.course_name);
        tvValue.setText(value.text);

        recyclerView = view.findViewById(R.id.assignments_recycler_view);
        recyclerView.setHasFixedSize(true);

        GridLayoutManager layoutManager = new GridLayoutManager(
                context,
                1, //span count here is number of rows
                GridLayoutManager.HORIZONTAL,
                false
        );

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new AssignmentAdapter(value.assignments));
        recyclerView.setVisibility(View.GONE);

        // Initialize the arrow view for toggling the list
        arrowView = view.findViewById(R.id.arrow_image);
        if(node.getLevel() < 4) {
            arrowView.setText(CHEVRON_RIGHT);
        } else {
            arrowView.setVisibility(View.GONE);
            arrowView = null;
        }

        Resources r = inflater.getContext().getResources();
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

    @Override
    public void toggle(boolean active) {
        if(arrowView == null)
            return;

        if(active) {
            arrowView.setText(CHEVRON_DOWN);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            arrowView.setText(CHEVRON_RIGHT);
            recyclerView.setVisibility(View.GONE);
        }
    }

    public static class CourseHeaderItem {
        public String text;
        public String icon;
        public List<Assignment> assignments;

        public CourseHeaderItem(String text, List<Assignment> assignments) {
            this.text = text;
            this.assignments = assignments;
        }
    }
}
