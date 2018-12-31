package com.sakaimobile.development.sakaiclient20.ui.fragments.assignments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sakaimobile.development.sakaiclient20.R;
import com.sakaimobile.development.sakaiclient20.models.Term;
import com.sakaimobile.development.sakaiclient20.networking.utilities.SharedPrefsUtil;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Assignment;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Course;
import com.sakaimobile.development.sakaiclient20.ui.MainActivity;
import com.sakaimobile.development.sakaiclient20.ui.helpers.RutgersSubjectCodes;
import com.sakaimobile.development.sakaiclient20.ui.listeners.OnActionPerformedListener;
import com.sakaimobile.development.sakaiclient20.ui.listeners.TreeViewItemClickListener;
import com.sakaimobile.development.sakaiclient20.ui.viewholders.AssignmentCourseViewHolder;
import com.sakaimobile.development.sakaiclient20.ui.viewholders.AssignmentTermHeaderViewHolder;
import com.sakaimobile.development.sakaiclient20.ui.viewholders.TermHeaderViewHolder;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.List;

import static com.sakaimobile.development.sakaiclient20.ui.MainActivity.ASSIGNMENTS_TAG;

/**
 * Created by Shoumyo Chakravorti.
 *
 * The main tab ({@link android.support.v4.app.Fragment}) that shows the user's
 * assignments. Assignments are always found within their respective term. If assignments
 * are sorted by date, they are not under any particular course header, but if
 * assignments are sorted by courses then they show up under their respective
 * courses (which, in turn, are visible under their respective terms).
 */

public class AssignmentsFragment extends Fragment {

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
    private List<List<Course>> courses;

    /**
     * If the {@link android.support.v4.app.Fragment} is specified to show assignments
     * sorted by date, this is the list of assignments sorted by date within their repsective
     * terms.
     */
    private List<List<Assignment>> assignments;

    /**
     * Whether the assignments should be shown as being sorted by course. {@code False} if
     * assignments should be sorted by term instead.
     */
    private boolean sortedByCourses;

    private OnActionPerformedListener actionPerformedListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This fragment provides the option to sort assignments by course or date.
        setHasOptionsMenu(true);
        this.actionPerformedListener = (OnActionPerformedListener) getActivity();

        // Using the bundle arguments, construct the tree to be displayed
        Bundle arguments = getArguments();
        sortedByCourses = arguments.getBoolean(ASSIGNMENTS_SORTED_BY_COURSES);
        try {
            if(sortedByCourses) {
                courses = (List<List<Course>>) arguments.getSerializable(ASSIGNMENTS_TAG);
            } else {
                assignments = (List<List<Assignment>>) arguments.getSerializable(ASSIGNMENTS_TAG);
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
        this.treeView.setDefaultAnimation(false);
        String state = SharedPrefsUtil.getTreeState(getContext(), SharedPrefsUtil.ASSIGNMENTS_TREE_TYPE);
        this.treeView.restoreState(state);
        this.treeView.setDefaultAnimation(true);

        // Set up refresh layout to make a new network request and re-instantiate the
        // assignments fragment
        SwipeRefreshLayout refreshLayout = view.findViewById(R.id.assignments_container);
        refreshLayout.addView(this.treeView.getView());
        refreshLayout.setOnRefreshListener(() -> {
            this.actionPerformedListener.loadAssignmentsFragment(sortedByCourses, true);
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

        //TODO implement this
        switch (item.getItemId()) {
            case R.id.action_sort_by_date: {
                MainActivity activity = (MainActivity) getActivity();
                // Sort by date (i.e. do not sort by courses) but don't refresh
                activity.loadAssignmentsFragment(false, false);
                return true;
            }
            case R.id.action_sort_by_course: {
                MainActivity activity = (MainActivity) getActivity();
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
        for(List<Course> courseList : courses) {
            // If there are no courses in the term, skip it (the Sakai
            // API shouldn't return a term if there are no courses for it,
            // so nothing special needs to be done here)
            if(courseList.size() == 0)
                continue;

            // Get the term name
            String termName = courseList.get(0).term.toString();

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
                if(course.assignments.size() == 0) {
                    continue;
                }

                // Get the course name for the view
                String courseName = course.title;

                // Create a course header item, and make a tree node using it
                String courseIconCode = RutgersSubjectCodes.mapCourseCodeToIcon
                        .get(course.subjectCode);
                AssignmentCourseViewHolder.CourseHeaderItem courseHeaderItem =
                        new AssignmentCourseViewHolder.CourseHeaderItem(
                                courseName,
                                courseIconCode,
                                course.assignments);

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
        for(List<Assignment> termAssignments : assignments) {
            // If no assignments, ignore this term/semester
            if(termAssignments.size() == 0)
                continue;

            // Get the term name
            String termName = termAssignments.get(0).term.toString();

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
