package com.example.development.sakaiclientandroid.utils.holders;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.development.sakaiclientandroid.R;
import com.unnamed.b.atv.model.TreeNode;

/**
 * Created by Development on 6/9/18.
 */

public class CourseHeaderViewHolder extends TreeNode.BaseNodeViewHolder<CourseHeaderViewHolder.CourseHeaderItem> {

    private static final String CHEVRON_DOWN = "\uF078";
    private static final String CHEVRON_RIGHT = "\uF054";
    private boolean isToggleable;
    private Context context;

    private TextView arrowView;

    public CourseHeaderViewHolder(Context context, boolean isToggleable) {
        super(context);
        this.context = context;
        this.isToggleable = isToggleable;
    }

    @Override
    public View createNodeView(TreeNode node, CourseHeaderItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.course_header_layout, null, false);

        TextView tvValue = view.findViewById(R.id.course_name);
        tvValue.setText(value.text);


        TextView iconTxt = view.findViewById(R.id.course_icon);
        iconTxt.setText(value.icon);


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
        //if default arrow is null, keep default toggle behavior
        if(arrowView != null && isToggleable)
            arrowView.setText(active ? CHEVRON_DOWN : CHEVRON_RIGHT);
    }

    public static class CourseHeaderItem {
        public String text;
        public String icon;
        public String siteId;

        public CourseHeaderItem(String text, String siteId, String iconCode) {
            this.text = text;
            this.siteId = siteId;
            this.icon = iconCode;

        }
    }
}
