package com.example.development.sakaiclientandroid.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.development.sakaiclientandroid.R;
import com.example.development.sakaiclientandroid.api_models.gradebook.AssignmentObject;

import java.util.ArrayList;
import java.util.List;

public class GradeItemAdapter extends ArrayAdapter {

    private final Context context;
    private final List<AssignmentObject> assignmentsList;

    public GradeItemAdapter(Context context, List<AssignmentObject> assignmentsList) {
        super(context, R.layout.grade_list_item, assignmentsList);

        this.context = context;
        this.assignmentsList = assignmentsList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //create inflater
        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //inflate the row
        View itemView = inflater.inflate(R.layout.grade_list_item, parent, false);

        //get name and grade textviews
        TextView nameTextView = itemView.findViewById(R.id.txt_assignment_name);
        TextView gradeTextView = itemView.findViewById(R.id.txt_grade);

        //set the text
        nameTextView.setText(assignmentsList.get(position).getItemName());
        gradeTextView.setText(assignmentsList.get(position).getGrade() + "/" + assignmentsList.get(position).getPoints());

        return itemView;
    }
}
