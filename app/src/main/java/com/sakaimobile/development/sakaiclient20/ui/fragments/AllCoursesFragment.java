package com.sakaimobile.development.sakaiclient20.ui.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.sakaimobile.development.sakaiclient20.R;
import com.sakaimobile.development.sakaiclient20.models.Term;
import com.sakaimobile.development.sakaiclient20.networking.utilities.SharedPrefsUtil;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Course;
import com.sakaimobile.development.sakaiclient20.ui.helpers.AssignmentSortingUtils;
import com.sakaimobile.development.sakaiclient20.ui.helpers.RutgersSubjectCodes;
import com.sakaimobile.development.sakaiclient20.ui.listeners.TreeViewItemClickListener;
import com.sakaimobile.development.sakaiclient20.ui.viewholders.CourseHeaderViewHolder;
import com.sakaimobile.development.sakaiclient20.ui.viewholders.TermHeaderViewHolder;
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.CourseViewModel;
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.ViewModelFactory;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

public class AllCoursesFragment extends Fragment {

    public static final String SHOULD_REFRESH = "SHOULD_REFRESH";

    @Inject ViewModelFactory viewModelFactory;
    private CourseViewModel courseViewModel;
    private AndroidTreeView treeView;
    private boolean shouldRefresh;

    private FrameLayout treeContainer;
    private ProgressBar progressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        this.shouldRefresh = getArguments().getBoolean(SHOULD_REFRESH);
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
        this.courseViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(CourseViewModel.class);
    }

    /**
     * When the fragment view is created, we want to get the responseBody from the bundle so
     * it can be displayed. This raw data is parsed, sorted, and then given to the
     * expandable list viewer for display.
     *
     * @param inflater           used to inflate our layout
     * @param container          .
     * @param savedInstanceState used to get the arguments that were passed to this fragment
     * @return created view
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.refreshable_treeview_fragment, null);

        // Set up refresh layout to make a new network request and re-instantiate the
        // assignments fragment
        this.treeContainer = view.findViewById(R.id.treeview_container);
        this.progressBar = view.findViewById(R.id.progressbar);
        this.progressBar.setIndeterminate(true);

        // View to ultimately be added to the screen
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        courseViewModel.getCoursesByTerm(shouldRefresh)
                .observe(getViewLifecycleOwner(), courses -> {
                    // If we are refreshing, there will be one initial false emission
                    if(shouldRefresh) {
                        shouldRefresh = false;
                        return;
                    }

                    // Construct the tree view based on th
                    // Make the TreeView visible inside the parent layout
                    if(this.treeView == null) {
                        this.treeView = new AndroidTreeView(getContext());
                        this.treeView.setDefaultAnimation(true);

                        TreeViewItemClickListener nodeClickListener = new TreeViewItemClickListener(this.treeView);
                        this.treeView.setDefaultNodeClickListener(nodeClickListener);
                    }

                    this.renderTree(courses);
                    this.progressBar.setVisibility(View.GONE);
                });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.saveTreeState();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.courses_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_refresh:
                this.progressBar.setVisibility(View.VISIBLE);
                this.saveTreeState();
                this.courseViewModel.refreshAllData();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void renderTree(List<List<Course>> courses) {
        TreeNode root = createTreeView(courses);

        this.treeContainer.removeAllViews();
        this.treeView.setRoot(root);

        String state = SharedPrefsUtil.getTreeState(getContext(), SharedPrefsUtil.ALL_COURSES_TREE_TYPE);;
        this.treeView.restoreState(state);

        this.treeContainer.addView(this.treeView.getView());
    }

    /**
     * Creates a TreeView structure using a list of terms, and a HashMap mapping
     * term to list of courses for that term, which is gotten from DataHandler
     *
     * @param coursesSorted courses sorted by term (gotten from data handler)
     */
    public TreeNode createTreeView(List<List<Course>> coursesSorted) {
        TreeNode root = TreeNode.root();
        Context mContext = getContext();

        for (List<Course> coursesInTerm : coursesSorted) {
            Term courseTerm = (coursesSorted.size() > 0) ? coursesInTerm.get(0).term : null;
            String termString = courseTerm.toString();

            //make a term header item, and make a TreeNode using it
            TermHeaderViewHolder.TermHeaderItem termNodeItem =
                    new TermHeaderViewHolder.TermHeaderItem(termString);
            TreeNode termNode =
                    new TreeNode(termNodeItem).setViewHolder(new TermHeaderViewHolder(mContext));

            //for each course, get its grades
            for (Course currCourse : coursesInTerm) {
                //create a course header item and make a TreeNode using it
                String courseIconCode = RutgersSubjectCodes.mapCourseCodeToIcon.get(currCourse.subjectCode);
                CourseHeaderViewHolder.CourseHeaderItem courseNodeItem =
                        new CourseHeaderViewHolder.CourseHeaderItem(
                                currCourse.title,
                                currCourse.siteId,
                                courseIconCode
                        );

                //set the custom view holder
                TreeNode courseNode = new TreeNode(courseNodeItem)
                        .setViewHolder(new CourseHeaderViewHolder(mContext, false));

                //when click a course Node, open the CourseSitesFragment to show
                //course specific information
                courseNode.setClickListener((node, value) -> {
                    if (value instanceof CourseHeaderViewHolder.CourseHeaderItem) {
                        //here we should save tree state
                        SharedPrefsUtil.saveTreeState(mContext, treeView, SharedPrefsUtil.ALL_COURSES_TREE_TYPE);
                        onCourseSelected(currCourse);
                    }
                });

                termNode.addChild(courseNode);
            }

            root.addChild(termNode);
        }

        return root;
    }

    private void onCourseSelected(Course course) {
        if(getActivity() == null || getActivity().getSupportFragmentManager() == null)
            return;

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                .add(R.id.fragment_container, CourseSitesFragment.newInstance(course))
                .addToBackStack(null)
                .commit();
    }

    private void saveTreeState() {
        SharedPrefsUtil.saveTreeState(getContext(), this.treeView, SharedPrefsUtil.ALL_COURSES_TREE_TYPE);
    }

}
