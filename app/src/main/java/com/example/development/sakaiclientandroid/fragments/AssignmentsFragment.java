package com.example.development.sakaiclientandroid.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.development.sakaiclientandroid.api_models.assignments.AssignmentObject;
import com.example.development.sakaiclientandroid.models.Course;
import com.example.development.sakaiclientandroid.models.Term;
import com.example.development.sakaiclientandroid.utils.custom.TreeViewItemClickListener;
import com.example.development.sakaiclientandroid.utils.holders.AssignmentNodeViewHolder;
import com.example.development.sakaiclientandroid.utils.holders.CourseHeaderViewHolder;
import com.example.development.sakaiclientandroid.utils.holders.TermHeaderViewHolder;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;
import java.util.ArrayList;

import static com.example.development.sakaiclientandroid.NavActivity.COURSES_TAG;

public class AssignmentsFragment extends BaseFragment {
    private AndroidTreeView treeView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Using the bundle arguments, construct the tree to be displayed
        Bundle arguments = getArguments();
        try {
            ArrayList<ArrayList<Course>> courses =
                    (ArrayList<ArrayList<Course>>) arguments.getSerializable(COURSES_TAG);
            createTreeView(courses);
        } catch (ClassCastException exception) {
            // Unable to create the tree, create a dummy tree
            //TODO: Needs better error handling
            treeView = new AndroidTreeView(getActivity(), TreeNode.root());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return treeView.getView();
    }

    private void createTreeView(ArrayList<ArrayList<Course>> courses) {
        Context currContext = getActivity();
        TreeNode root = TreeNode.root();

        // The courses as returned by the DataHandler are already sorted by term,
        // so we just need to loop through them to create the terms with all
        // courses and their assignments
        for(ArrayList<Course> courseList : courses) {
            // Get the term name
            Term courseTerm = (courses.size() > 0) ? courseList.get(0).getTerm() : null;
            String termName = (courseTerm != null) ?
                    courseTerm.getTermString() + " " + courseTerm.getYear() : "General";

            // Create a term header item, and make a tree node using it
            TermHeaderViewHolder.TermHeaderItem termHeaderItem =
                    new TermHeaderViewHolder.TermHeaderItem(termName);
            TreeNode termNode = new TreeNode(termHeaderItem);
            // Set the term header view holder to inflate the appropriate view
            termNode.setViewHolder(new TermHeaderViewHolder(currContext));

            // For each course, create the course node with its respective
            // assignments
            for(Course course : courseList) {
                //SKIP THE COURSE IF IT DOESN'T HAVE ANY ASSIGNMENTS
                if(course.getAssignmentObjectList().size() == 0) {
                    continue;
                }

                // Get the course name for the view
                String courseName = course.getTitle();

                // Create a course header item, and make a tree node using it
                CourseHeaderViewHolder.CourseHeaderItem courseHeaderItem =
                        new CourseHeaderViewHolder.CourseHeaderItem(courseName);
                TreeNode courseNode = new TreeNode(courseHeaderItem);
                // Set the course header view holder to inflate the appropriate view
                courseNode.setViewHolder(new CourseHeaderViewHolder(currContext));

                // For each assignment of the course, create a new node and
                // add it as a child of the course
                for(AssignmentObject assignment : course.getAssignmentObjectList()) {
                    String assignmentName = assignment.getTitle();

                    // Create an assignment header item, and make a tree node using it
                    AssignmentNodeViewHolder.AssignmentItem assignmentItem =
                            new AssignmentNodeViewHolder.AssignmentItem(assignmentName);
                    TreeNode assignmentNode = new TreeNode(assignmentItem);
                    // Set the course header view holder to inflate the appropriate view
                    assignmentNode.setViewHolder(new AssignmentNodeViewHolder(currContext));

                    // Add the assignment to the course
                    courseNode.addChild(assignmentNode);
                }

                // Add the course to the term
                termNode.addChild(courseNode);
            }

            // Add the term to the root node
            if(termNode.getChildren().size() > 0)
                root.addChild(termNode);
        }

        treeView = new AndroidTreeView(currContext, root);
        treeView.setDefaultAnimation(true);
        treeView.setDefaultNodeClickListener(new TreeViewItemClickListener(treeView, root));
    }
}
