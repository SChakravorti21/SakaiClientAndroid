package com.sakaimobile.development.sakaiclient20.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sakaimobile.development.sakaiclient20.R;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Grade;

import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TreeGradeAdapter extends RecyclerView.Adapter<TreeGradeAdapter.GradeViewHolder> {

    private List<Grade> grades;

    public TreeGradeAdapter(List<Grade> grades) {
        this.grades = grades;
    }

    @NonNull
    @Override
    public GradeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        return new GradeViewHolder(inflater.inflate(R.layout.tree_node_grade, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GradeViewHolder viewHolder, int position) {
        Grade grade = grades.get(position);
        // The assignment text must be selected for auto-scrolling to work
        viewHolder.assignmentText.setSelected(true);
        viewHolder.assignmentText.setText(grade.itemName);
        viewHolder.gradeText.setText(grade.grade == null
                ? String.format(Locale.US, "/%f", grade.points)
                : String.format(Locale.US, "%s/%.1f", grade.grade, grade.points));
    }

    @Override
    public int getItemCount() {
        return grades != null ? grades.size() : 0;
    }

    class GradeViewHolder extends RecyclerView.ViewHolder {
        private TextView assignmentText;
        private TextView gradeText;

        private GradeViewHolder(@NonNull View itemView) {
            super(itemView);
            assignmentText = itemView.findViewById(R.id.txt_assignment_name);
            gradeText = itemView.findViewById(R.id.txt_grade);
        }
    }
}
