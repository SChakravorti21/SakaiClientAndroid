package com.example.development.sakaiclientandroid.fragments.assignments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.development.sakaiclientandroid.NavActivity;
import com.example.development.sakaiclientandroid.R;
import com.example.development.sakaiclientandroid.api_models.assignments.Assignment;
import com.example.development.sakaiclientandroid.fragments.BaseFragment;
import com.example.development.sakaiclientandroid.models.Course;
import com.example.development.sakaiclientandroid.models.Term;
import com.example.development.sakaiclientandroid.utils.RutgersSubjectCodes;
import com.example.development.sakaiclientandroid.utils.custom.TreeViewItemClickListener;
import com.example.development.sakaiclientandroid.utils.holders.AssignmentCourseViewHolder;
import com.example.development.sakaiclientandroid.utils.holders.AssignmentTermHeaderViewHolder;
import com.example.development.sakaiclientandroid.utils.holders.TermHeaderViewHolder;
import com.example.development.sakaiclientandroid.utils.requests.SharedPrefsUtil;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;
import java.util.ArrayList;
import java.util.List;

import static com.example.development.sakaiclientandroid.NavActivity.ASSIGNMENTS_TAG;

/**
 * Created by Shoumyo Chakravorti.
 *
 * The main tab ({@link android.support.v4.app.Fragment}) that shows the user's
 * assignments. Assignments are always found within their respective term. If assignments
 * are sorted by date, they are not under any particular course header, but if
 * assignments are sorted by courses then they show up under their respective
 * courses (which, in turn, are visible under their respective terms).
 */

public class AssignmentsFragment extends BaseFragment {

    /**
     * Tag used to indicate whether the assignments should be shown as being sorted
     * by date or courses.
     */
    public static final String ASSIGNMENTS_SORTED_BY_COURSES = "ASSIGNMENTS_SORTED_BY_COURSES";

    /**
     * The {@link AndroidTreeView} that is represented by this
     *  {@link android.support.v4.app.Fragment}.
     */
    private AndroidTreeView treeView;

    /**
     * If the {@link android.support.v4.app.Fragment} is specified to show assignments
     * sorted by their courses, this is the non-null list of courses sorted by their terms.
     */
    private ArrayList<ArrayList<Course>> courses;

    /**
     * If the {@link android.support.v4.app.Fragment} is specified to show assignments
     * sorted by date, this is the list of assignments sorted by date within their repsective
     * terms.
     */
    private ArrayList<ArrayList<Assignment>> assignments;

    /**
     * Whether the assignments should be shown as being sorted by course. {@code False} if
     * assignments should be sorted by term instead.
     */
    private boolean sortedByCourses;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This fragment provides the option to sort assignments by course or date.
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
            treeView = new AndroidTreeView(getActivity(), TreeNode.root());

            Toast errorToast = Toast.makeText(getContext(),
                    "An error occurred, please try refreshing.",
                    Toast.LENGTH_SHORT);
            errorToast.show();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_assignments, container, false);

        // Construct the tree view based on the current sorting preference
        TreeNode root = TreeNode.root();
        if(sortedByCourses) {
            createTreeViewFromCourses(root);
        } else {
            createTreeViewFromAssignments(root);
        }

        // Initialize the TreeView with the tree structure
        this.treeView = new AndroidTreeView(getActivity(), root);
        this.treeView.setDefaultNodeClickListener(new TreeViewItemClickListener(treeView, root));

        // Restore the tree's state from how it was before the user moved away from the
        // tab the last time, and disable the animation while the state is restored
        // (otherwise the expansion animation repeating every time the tab is visited gets annoying)
        //this.treeView.setDefaultAnimation(false);
        String state = SharedPrefsUtil.getTreeState(getContext(), SharedPrefsUtil.ASSIGNMENTS_TREE_TYPE);
        this.treeView.restoreState(state);
        //this.treeView.setDefaultAnimation(true);

        // Set up refresh layout to make a new network request and re-instantiate the
        // assignments fragment
        SwipeRefreshLayout refreshLayout = view.findViewById(R.id.assignments_container);
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

        // View to ultimately be added to the screen
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
                // Sort by date (i.e. do not sort by courses) but don't refresh
                activity.loadAssignmentsFragment(false, false);
                return true;
            }
            case R.id.action_sort_by_course: {
                NavActivity activity = (NavActivity) getActivity();
                // Sort by courses but don't refresh
                activity.loadAssignmentsFragment(true, false);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /**
     * On top of performing regular functions of {@link Fragment#onDetach()},
     * saves the expanded state of the tree view so that returning to this tab
     * restores the same state.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        SharedPrefsUtil.saveTreeState(getContext(),
                treeView,
                SharedPrefsUtil.ASSIGNMENTS_TREE_TYPE);
    }

    /**
     * Constructs the {@link AndroidTreeView} with a Term -> Course -> Assignment hierarchy
     * if the {@link Fragment} is specified to sort by courses. Since the root node is handled
     * by reference, nothing needs to be returned.
     *
     * @param root The root node for making the tree
     */
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
                String courseIconCode = RutgersSubjectCodes.mapCourseCodeToIcon
                        .get(course.getSubjectCode());
                AssignmentCourseViewHolder.CourseHeaderItem courseHeaderItem =
                        new AssignmentCourseViewHolder.CourseHeaderItem(
                                courseName,
                                courseIconCode,
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

    /**
     * Constructs the {@link AndroidTreeView} for assignments sorted within their terms
     * by placing assignments under their respective term header. Similar to
     * {@code createTreeViewFromCourses}, since the root node is handled by reference,
     * nothing needs to be returned.
     *
     * @param root The root node for making the tree
     */
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
