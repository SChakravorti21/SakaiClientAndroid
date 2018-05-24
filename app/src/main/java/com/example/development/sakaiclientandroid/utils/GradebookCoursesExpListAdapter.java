package com.example.development.sakaiclientandroid.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.development.sakaiclientandroid.R;
import com.example.development.sakaiclientandroid.api_models.gradebook.AssignmentObject;
import com.example.development.sakaiclientandroid.models.Course;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GradebookCoursesExpListAdapter extends BaseExpandableListAdapter {

    private final Context context;
    private final List<String> courseTitles;
    private final List<Integer> courseSubjectCodes;
    private final HashMap<String, List<AssignmentObject>> mapCourseToGrades;


    public GradebookCoursesExpListAdapter(Context c,
                                          List<String> courseTitles,
                                          List<Integer> courseSubjectCodes,
                                          HashMap<String, List<AssignmentObject>> mapCourseToGrades) {

        this.context = c;
        this.courseTitles = courseTitles;
        this.courseSubjectCodes = courseSubjectCodes;
        this.mapCourseToGrades = mapCourseToGrades;
    }


    @Override
    public int getGroupCount() {
        return this.courseTitles.size();
    }

    /**
     * gets the number of assignment objects that a course has
     *
     * @param groupPos position of group
     * @return number of children in that group
     */
    @Override
    public int getChildrenCount(int groupPos) {
        try {
            String courseTitle = courseTitles.get(groupPos);
            return this.mapCourseToGrades.get(courseTitle).size();
        }
        catch(Exception e) {
            return 1;
        }
    }

    @Override
    public Object getGroup(int i) {
        return this.courseTitles.get(i);
    }

    @Override
    public Object getChild(int groupPos, int childPos) {

        try {
            String courseTitle = courseTitles.get(groupPos);
            return this.mapCourseToGrades.get(courseTitle).get(childPos);
        }
        catch(Exception e) {
            return null;
        }
    }

    @Override
    public long getGroupId(int i) {
        return 0;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }


    @Override
    public View getGroupView(int groupPos, boolean isExpandable, View convertView, ViewGroup parent) {

        final String groupText = (String) getGroup(groupPos);

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.course_list_item_gradebook, parent, false);
        }


        TextView headerText = convertView.findViewById(R.id.course_name_text);
        headerText.setText(groupText);


        ImageView image = convertView.findViewById(R.id.course_icon);

        int subjectCode = this.courseSubjectCodes.get(groupPos);
        DataHandler.setSiteIcon(image, subjectCode);


        return convertView;

    }

    @Override
    public View getChildView(int groupPos, int childPos, boolean isLastChild, View convertView, ViewGroup parent) {

        final String assignmentName;
        final String grade;
        final AssignmentObject child = (AssignmentObject) getChild(groupPos, childPos);

        if(child == null) {
            assignmentName = "No grades";
            grade = "";
        }
        else {
            assignmentName = child.getItemName();
            grade = child.getGrade();
        }


        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.grade_list_item_gradebook, parent, false);
        }

        TextView assignmentText = convertView.findViewById(R.id.txt_assignment_name);
        assignmentText.setText(assignmentName);

        TextView gradeText = convertView.findViewById(R.id.txt_grade);
        gradeText.setText(grade);
        return convertView;

    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
