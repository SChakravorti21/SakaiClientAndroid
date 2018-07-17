package com.example.development.sakaiclientandroid.utils.holders;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.development.sakaiclientandroid.R;
import com.unnamed.b.atv.model.TreeNode;

public class GradeNodeViewHolder extends TreeNode.BaseNodeViewHolder<GradeNodeViewHolder.GradeTreeItem> {


    public GradeNodeViewHolder(Context c) {
        super(c);
    }

    @Override
    public View createNodeView(TreeNode node, GradeTreeItem value) {

        final LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.gradeitem_node, null, false);


        TextView assignmentText = view.findViewById(R.id.txt_assignment_name);
        assignmentText.setText(value.assignment);

        TextView gradeText = view.findViewById(R.id.txt_grade);

        //dynamically fills in the string resource
        if(value.grade == null)
        {
            gradeText.setText(
                    context.getString(R.string.grade_item_null, value.points)
            );
        }
        else
        {
            gradeText.setText(
                    context.getString(R.string.grade_item, value.grade, value.points)
            );
        }

        return view;
    }


    public static class GradeTreeItem {
        public String assignment;
        public String grade;
        public Double points;

        public GradeTreeItem(String a, String g, Double p) {
            this.assignment = a;
            this.grade = g;
            this.points = p;
        }
    }
}
