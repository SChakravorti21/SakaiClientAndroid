package com.example.development.sakaiclientandroid.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.development.sakaiclientandroid.R;
import com.example.development.sakaiclientandroid.api_models.gradebook.AssignmentObject;
import com.example.development.sakaiclientandroid.models.Course;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GradebookTermsExpListAdapter extends BaseExpandableListAdapter {

    private final Context context;

    private final List<String> termHeaders;
    private final HashMap<String, List<String>> mapTermToCourseTitles;
    private final HashMap<String, List<String>> mapTermToCourseIds;
    private final HashMap<String, List<Integer>> mapTermToCourseSubjectCodes;


    //each item in the list represents a hashmap for each term, mapping the course to its grades
    //this is necessary because people can retake the same course in different terms.
    private final List<HashMap<String, List<AssignmentObject>>> mapCourseToGrades;



//    //by now, all courses should have their grades
    public GradebookTermsExpListAdapter(Context context,
                                        List<String> termHeaders,
                                        HashMap<String, List<String>> mapTermToCourseTitles,
                                        HashMap<String, List<String>> mapTermToCourseIds,
                                        HashMap<String, List<Integer>> mapTermToCourseSubjectCodes) {

        this.context = context;
        this.termHeaders = termHeaders;
        this.mapTermToCourseTitles = mapTermToCourseTitles;
        this.mapTermToCourseIds = mapTermToCourseIds;
        this.mapTermToCourseSubjectCodes = mapTermToCourseSubjectCodes;


        this.mapCourseToGrades = new ArrayList<>();
        mapCoursesToGradesList();

    }


    //for each term, get its courses, and create a hashmap mapping course to grades for each course
    private void mapCoursesToGradesList() {

        for(String termString : this.termHeaders) {

            HashMap<String, List<AssignmentObject>> tempMapCourseToGrades = new HashMap<>();


            List<String> courseIds = this.mapTermToCourseIds.get(termString);

            for(String id : courseIds) {
                Course c = DataHandler.getCourseFromId(id);

                List<AssignmentObject> assignments = c.getAssignmentObjectList();
                tempMapCourseToGrades.put(c.getTitle(), assignments);
            }

            this.mapCourseToGrades.add(tempMapCourseToGrades);

        }
    }


    @Override
    public int getGroupCount() {
        return this.termHeaders.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return 1;
    }

    @Override
    public Object getGroup(int i) {
        return this.termHeaders.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return i1;
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    /**
     *
     * @param groupPos position of group
     * @param isExpanded whether or not the group is expanded
     * @param convertView view to reuse if possible
     * @param parent view that the group will be attached to
     * @return view of group
     */
    @Override
    public View getGroupView(int groupPos, boolean isExpanded, View convertView, ViewGroup parent) {

        String header = (String) getGroup(groupPos);
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.term_list_group, parent, false);
        }

        TextView headerTxtView = convertView.findViewById(R.id.term_header_group);
        headerTxtView.setText(header);
        return convertView;
    }


    /**
     * The child in terms of the current TermsAdapter refers to the courses
     * Each course must also be expandable, so when we get the child view, we set it
     * to our custom parent list view which override onMeasure so it can correctly display all children
     *
     * We give this listview an adapter which contains all the course headers and also its list of grades
     * so that it can be displayed
     *
     * @param groupPos group number of the parent of the child
     * @param childPos child number in group
     * @param isLastChild whether or not it is last child in the group
     * @param convertView old view to reuse if possible
     * @param parent the parent that this view will be attached to
     * @return child view
     */
    @Override
    public View getChildView(int groupPos, int childPos, boolean isLastChild, View convertView, ViewGroup parent) {

        final CustomParentListView parentListView = new CustomParentListView(this.context);
        String termName = (String) getGroup(groupPos);

        List<String> courseTitles = mapTermToCourseTitles.get(termName);
        List<Integer> courseSubjectCodes = mapTermToCourseSubjectCodes.get(termName);

        parentListView.setAdapter(new GradebookCoursesExpListAdapter(this.context, courseTitles, courseSubjectCodes, this.mapCourseToGrades.get(groupPos)));

        return parentListView;

    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
