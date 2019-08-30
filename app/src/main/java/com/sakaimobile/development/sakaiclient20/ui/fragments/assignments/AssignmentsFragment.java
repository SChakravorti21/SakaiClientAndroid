package com.sakaimobile.development.sakaiclient20.ui.fragments.assignments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.sakaimobile.development.sakaiclient20.R;
import com.sakaimobile.development.sakaiclient20.networking.utilities.SharedPrefsUtil;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Assignment;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Course;
import com.sakaimobile.development.sakaiclient20.ui.adapters.TreeAssignmentAdapter;
import com.sakaimobile.development.sakaiclient20.ui.fragments.BaseFragment;
import com.sakaimobile.development.sakaiclient20.ui.helpers.AssignmentSortingUtils;
import com.sakaimobile.development.sakaiclient20.ui.helpers.CourseIconProvider;
import com.sakaimobile.development.sakaiclient20.ui.listeners.TreeViewItemClickListener;
import com.sakaimobile.development.sakaiclient20.ui.viewholders.CourseViewHolder;
import com.sakaimobile.development.sakaiclient20.ui.viewholders.AssignmentTermHeaderViewHolder;
import com.sakaimobile.development.sakaiclient20.ui.viewholders.TermHeaderViewHolder;
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.AssignmentViewModel;
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.ViewModelFactory;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import dagger.android.support.AndroidSupportInjection;

/**
 * Created by Shoumyo Chakravorti.
 *
 * The main tab ({@link Fragment}) that shows the user's
 * assignments. Assignments are always found within their respective term. If assignments
 * are sorted by date, they are not under any particular course header, but if
 * assignments are sorted by courses then they show up under their respective
 * courses (which, in turn, are visible under their respective terms).
 */

public class AssignmentsFragment extends BaseFragment {

    /**
     * The {@link AndroidTreeView} that is represented by this
     *  {@link Fragment}.
     */
    private AndroidTreeView treeView;

    /**
     * The parent layout for the assignments TreeView.
     */
    private FrameLayout treeContainer;
    private ProgressBar progressBar;

    /**
     * If the {@link Fragment} is specified to show assignments
     * sorted by their courses, this is the non-null list of courses sorted by their terms.
     */
    private List<List<Course>> courses;

    /**
     * If the {@link Fragment} is specified to show assignments
     * sorted by date, this is the list of assignments sorted by date within their repsective
     * terms.
     */
    private List<List<Assignment>> assignments;

    @Inject ViewModelFactory viewModelFactory;
    private AssignmentViewModel assignmentViewModel;

    // This boolean flag is used to determine whether the sorting menu
    // should be shown. If the tab is opened and the user immediately
    // selected to sort by a different type, the app would crash,
    // so we wait to show the sort menu group until the necessary data has loaded.
    private boolean hasFinishedInitialDataLoad = false;

    /**
     * Whether the assignments should be shown as being sorted by course. {@code False} if
     * assignments should be sorted by term instead.
     */
    private boolean sortedByCourses = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This fragment provides the option to sort assignments by course or date.
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.refreshable_treeview_fragment, container, false);

        // Set up refresh layout to make a new network request and re-instantiate the
        // assignments fragment
        this.treeContainer = view.findViewById(R.id.treeview_container);
        this.progressBar = view.findViewById(R.id.progressbar);
        this.progressBar.setIndeterminate(true);

        // View to ultimately be added to the screen
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.initRefreshFailureListener(assignmentViewModel, () -> {
            this.progressBar.setVisibility(View.GONE);
            this.treeContainer.setVisibility(View.VISIBLE);
            return null;
        });

        this.assignmentViewModel.getCoursesByTerm()
            .observe(getViewLifecycleOwner(), courses -> {
                // Since new data has arrived, the old data might not be
                // relevant any more, so we need to do both sorts (by course and by term) again.
                this.courses = courses;
                AssignmentSortingUtils.sortCourseAssignments(this.courses);
                this.assignments = AssignmentSortingUtils.sortAssignmentsByTerm(this.courses);

                // Construct the tree view based on th
                // Make the TreeView visible inside the parent layout
                if(this.treeView == null) {
                    this.treeView = new AndroidTreeView(getContext());
                    this.treeView.setDefaultAnimation(true);

                    TreeViewItemClickListener nodeClickListener = new TreeViewItemClickListener(this.treeView);
                    this.treeView.setDefaultNodeClickListener(nodeClickListener);
                }

                this.renderTree();
                this.progressBar.setVisibility(View.GONE);
                this.treeContainer.setVisibility(View.VISIBLE);

                // If this is the first time the observation is called,
                // inflate the sort menu
                if(!hasFinishedInitialDataLoad) {
                    hasFinishedInitialDataLoad = true;
                    getActivity().invalidateOptionsMenu();
                }
            }
        );
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);

        this.assignmentViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(AssignmentViewModel.class);
    }

    /**
     * On top of performing regular functions of {@link Fragment#onPause()},
     * saves the expanded state of the tree view so that returning to this tab
     * restores the same state.
     */
    @Override
    public void onPause() {
        super.onPause();
        this.saveTreeState();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.treeView = null;
        this.treeContainer = null;
        this.progressBar = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.assignments_fragment_menu, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // Do not allow the user to sort based on any type if
        // we do not have the necessary data to create that view
        if(hasFinishedInitialDataLoad) {
            menu.findItem(R.id.assignment_sort_group).setVisible(true);
        } else {
            menu.findItem(R.id.assignment_sort_group).setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_by_date: {
                // Nothing to do if tree view is already sorted by date
                if(!sortedByCourses) return true;
                break;
            }
            case R.id.action_sort_by_course: {
                if(sortedByCourses) return true;
                break;
            }
            case R.id.action_refresh: {
                this.treeContainer.setVisibility(View.INVISIBLE);
                this.progressBar.setVisibility(View.VISIBLE);
                this.saveTreeState();
                this.assignmentViewModel.refreshAllData();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }

        this.saveTreeState();
        sortedByCourses = !sortedByCourses;
        this.renderTree();
        return true;
    }

    /**
     * Constructs the {@link AndroidTreeView} with a Term -> Course -> Assignment hierarchy
     * if the {@link Fragment} is specified to sort by courses. Since the root node is handled
     * by reference, nothing needs to be returned.
     *
     */
    private TreeNode createTreeFromCourses() {
        TreeNode root =  TreeNode.root();
        Context currContext = getActivity();

        // The courses as returned by the DataHandler are already sorted by term,
        // so we just need to loop through them to create the terms with all
        // courses and their assignments
        for(List<Course> courseList : this.courses) {
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
                if(course.assignments.size() == 0)
                    continue;

                // Create a course header item, and make a tree node using it
                String courseIconCode =
                        CourseIconProvider.getCourseIcon(course.subjectCode);
                CourseViewHolder.CourseHeaderItem courseHeaderItem =
                        new CourseViewHolder.CourseHeaderItem(
                                course.title,
                                courseIconCode,
                                new TreeAssignmentAdapter(course.assignments)
                        );

                TreeNode courseNode = new TreeNode(courseHeaderItem);
                // Set the course header view holder to inflate the appropriate view
                courseNode.setViewHolder(new CourseViewHolder(currContext));

                // Add the course to the term
                termNode.addChild(courseNode);
            }

            // Add the term to the root node
            if(termNode.getChildren().size() > 0)
                root.addChild(termNode);
        }

        return root;
    }

    /**
     * Constructs the {@link AndroidTreeView} for assignments sorted within their terms
     * by placing assignments under their respective term header. Similar to
     * {@code createTreeFromCourses}, since the root node is handled by reference,
     * nothing needs to be returned.
     */
    private TreeNode createTreeFromAssignments() {
        TreeNode root =  TreeNode.root();
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

        return root;
    }

    private void renderTree() {
        TreeNode root = this.sortedByCourses
                ? createTreeFromCourses()
                : createTreeFromAssignments();

        this.treeContainer.removeAllViews();
        this.treeView.setRoot(root);

        String state = this.getTreeState();
        this.treeView.restoreState(state);

        this.treeContainer.addView(this.treeView.getView());
    }

    private void saveTreeState() {
        SharedPrefsUtil.saveTreeState(
                getContext(),
                this.treeView,
                sortedByCourses
                        ? SharedPrefsUtil.ASSIGNMENTS_BY_COURSES_TREE_TYPE
                        : SharedPrefsUtil.ASSIGNMENTS_BY_TERM_TREE_TYPE
        );
    }

    private String getTreeState() {
        return SharedPrefsUtil.getTreeState(
                getContext(),
                sortedByCourses
                        ? SharedPrefsUtil.ASSIGNMENTS_BY_COURSES_TREE_TYPE
                        : SharedPrefsUtil.ASSIGNMENTS_BY_TERM_TREE_TYPE
        );
    }
}
