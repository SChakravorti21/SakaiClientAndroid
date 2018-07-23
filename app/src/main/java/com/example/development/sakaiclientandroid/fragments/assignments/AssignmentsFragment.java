package com.example.development.sakaiclientandroid.fragments.assignments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.development.sakaiclientandroid.NavActivity;
import com.example.development.sakaiclientandroid.R;
import com.example.development.sakaiclientandroid.api_models.assignments.Assignment;
import com.example.development.sakaiclientandroid.fragments.BaseFragment;
import com.example.development.sakaiclientandroid.models.Course;
import com.example.development.sakaiclientandroid.models.Term;
import com.example.development.sakaiclientandroid.utils.custom.TreeViewItemClickListener;
import com.example.development.sakaiclientandroid.utils.holders.AssignmentCourseViewHolder;
import com.example.development.sakaiclientandroid.utils.holders.AssignmentTermHeaderViewHolder;
import com.example.development.sakaiclientandroid.utils.holders.TermHeaderViewHolder;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;
import java.util.ArrayList;

import static com.example.development.sakaiclientandroid.NavActivity.ASSIGNMENTS_TAG;

public class AssignmentsFragment extends BaseFragment {
    public static final String ASSIGNMENTS_SORTED_BY_COURSES = "ASSIGNMENTS_SORTED_BY_COURSES";

    private AndroidTreeView treeView;
    private ArrayList<ArrayList<Course>> courses;
    private ArrayList<ArrayList<Assignment>> assignments;
    private boolean sortedByCourses;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // Using the bundle arguments, construct the tree to be displayed
        Bundle arguments = getArguments();
        sortedByCourses = arguments.getBoolean(ASSIGNMENTS_SORTED_BY_COURSES);
        try {
            if(sortedByCourses) {
                courses = (ArrayList<ArrayList<Course>>) arguments.getSerializable(ASSIGNMENTS_TAG);
            } else {
                assignments = (ArrayList<ArrayList<Assignment>>) arguments.getSerializable(ASSIGNMENTS_TAG);
            }
        } catch (ClassCastException exception) {
            // Unable to create the tree, create a dummy tree
            //TODO: Needs better error handling
            treeView = new AndroidTreeView(getActivity(), TreeNode.root());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_assignments, container, false);
        SwipeRefreshLayout refreshLayout = view.findViewById(R.id.assignments_container);

        TreeNode root = TreeNode.root();
        if(sortedByCourses) {
            createTreeViewFromCourses(root);
        } else {
            createTreeViewFromAssignments(root);
        }

        this.treeView = new AndroidTreeView(getActivity(), root);
        this.treeView.setDefaultAnimation(true);
        this.treeView.setDefaultNodeClickListener(new TreeViewItemClickListener(treeView, root));

        refreshLayout.addView(this.treeView.getView());
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FragmentActivity parentActivity = getActivity();

                //checking if instance to prevent casting errors
                if (parentActivity instanceof NavActivity) {
                    //set refresh boolean to true so that the request is made again forcefully
                    //reloads the current fragment, (which also remakes the request for grades)
                    ((NavActivity) parentActivity).loadAssignmentsFragment(sortedByCourses, true);
                }
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.assignments_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_sort_by_date: {
                NavActivity activity = (NavActivity) getActivity();
                activity.loadAssignmentsFragment(false, false);
                return true;
            }
            case R.id.action_sort_by_course: {
                NavActivity activity = (NavActivity) getActivity();
                activity.loadAssignmentsFragment(true, false);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void createTreeViewFromCourses(TreeNode root) {
        Context currContext = getActivity();

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
                if(course.getAssignmentList().size() == 0) {
                    continue;
                }

                // Get the course name for the view
                String courseName = course.getTitle();

                // Create a course header item, and make a tree node using it
                AssignmentCourseViewHolder.CourseHeaderItem courseHeaderItem =
                        new AssignmentCourseViewHolder.CourseHeaderItem(courseName,
                                course.getAssignmentList());
                TreeNode courseNode = new TreeNode(courseHeaderItem);
                // Set the course header view holder to inflate the appropriate view
                courseNode.setViewHolder(new AssignmentCourseViewHolder(currContext));

                // Add the course to the term
                termNode.addChild(courseNode);
            }

            // Add the term to the root node
            if(termNode.getChildren().size() > 0)
                root.addChild(termNode);
        }
    }

    private void createTreeViewFromAssignments(TreeNode root) {
        Context currContext = getActivity();

        // The courses as returned by the DataHandler are already sorted by term,
        // so we just need to loop through them to create the terms with all
        // courses and their assignments
        for(ArrayList<Assignment> termAssignments : assignments) {
            // Get the term name
            Term courseTerm = (termAssignments.size() > 0) ? termAssignments.get(0).getTerm() : null;
            String termName = (courseTerm != null) ?
                    courseTerm.getTermString() + " " + courseTerm.getYear() : "General";

            // Create a term header item, and make a tree node using it
            AssignmentTermHeaderViewHolder.TermHeaderItem termHeaderItem =
                    new AssignmentTermHeaderViewHolder.TermHeaderItem(termName, termAssignments);
            TreeNode termNode = new TreeNode(termHeaderItem);
            // Set the term header view holder to inflate the appropriate view
            termNode.setViewHolder(new AssignmentTermHeaderViewHolder(currContext));

            // Add the term to the root node
            root.addChild(termNode);
        }
    }
}
