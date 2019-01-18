package com.sakaimobile.development.sakaiclient20.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sakaimobile.development.sakaiclient20.R;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Grade;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SiteGradeAdapter extends ArrayAdapter {

    private static final float PADDING_LEFT_DP = 10f;
    private static final float PADDING_VERTICAL_DP = 8f;
    private final List<Grade> assignmentsList;

    public SiteGradeAdapter(Context context, List<Grade> assignmentsList) {
        super(context, R.layout.tree_node_grade, assignmentsList);

        this.assignmentsList = assignmentsList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Context context = getContext();

        if(convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.grade_item, parent, false);

        //get name and grade textviews
        TextView nameTextView = convertView.findViewById(R.id.txt_assignment_name);
        TextView gradeTextView = convertView.findViewById(R.id.txt_grade);

        //set the text
        Grade assignment = assignmentsList.get(position);
        String name = assignment.itemName;
        nameTextView.setText(name);

        //dynamically fills in the string resource
        String gradeString = assignment.grade == null
                ? context.getString(R.string.grade_item_null, assignment.points)
                : context.getString(R.string.grade_item, assignment.grade, assignment.points);
        gradeTextView.setText(gradeString);
        return convertView;
    }
}