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
    public CourseHeaderViewHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, CourseHeaderItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.course_header_layout, null, false);

        TextView tvValue = view.findViewById(R.id.course_name);
        tvValue.setText(value.text);

        return view;
    }

    public static class CourseHeaderItem {
        public String text;
        public String icon;

        public CourseHeaderItem(String text) {
            this.text = text;
        }
    }
}
