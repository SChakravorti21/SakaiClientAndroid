package com.sakaimobile.development.sakaiclient20.ui.fragments;

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
import com.sakaimobile.development.sakaiclient20.models.Term;
import com.sakaimobile.development.sakaiclient20.networking.utilities.SharedPrefsUtil;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Course;
import com.sakaimobile.development.sakaiclient20.ui.adapters.TreeGradeAdapter;
import com.sakaimobile.development.sakaiclient20.ui.helpers.CourseIconProvider;
import com.sakaimobile.development.sakaiclient20.ui.listeners.TreeViewItemClickListener;
import com.sakaimobile.development.sakaiclient20.ui.viewholders.CourseViewHolder;
import com.sakaimobile.development.sakaiclient20.ui.viewholders.TermHeaderViewHolder;
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.GradeViewModel;
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.ViewModelFactory;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import dagger.android.support.AndroidSupportInjection;


public class AllGradesFragment extends BaseFragment {

    @Inject ViewModelFactory viewModelFactory;
    private GradeViewModel gradeViewModel;

    private ProgressBar progressBar;
    private AndroidTreeView treeView;
    private FrameLayout treeContainer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
        this.gradeViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(GradeViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
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

        this.initRefreshFailureListener(gradeViewModel, () -> {
            this.progressBar.setVisibility(View.GONE);
            this.treeContainer.setVisibility(View.VISIBLE);
            return null;
        });

        gradeViewModel.getCoursesByTerm().observe(getViewLifecycleOwner(), courses -> {
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
            this.treeContainer.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        this.saveTreeState();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.treeView = null;
        this.progressBar = null;
        this.treeContainer = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.grades_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_refresh:
                this.treeContainer.setVisibility(View.INVISIBLE);
                this.progressBar.setVisibility(View.VISIBLE);
                this.saveTreeState();
                this.gradeViewModel.refreshAllData();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void renderTree(List<List<Course>> courses) {
        TreeNode root = createTreeView(courses);

        this.treeContainer.removeAllViews();
        this.treeView.setRoot(root);

        String state = SharedPrefsUtil.getTreeState(getContext(), SharedPrefsUtil.ALL_GRADES_TREE_TYPE);
        this.treeView.restoreState(state);

        this.treeContainer.addView(this.treeView.getView());
    }

    /**
     * Creates a treeview structure using a list of terms, and a hashmap mapping
     * term to list of courses for that term, which is gotten from DataHandler
     */
    public TreeNode createTreeView(List<List<Course>> coursesSorted) {
        TreeNode root = TreeNode.root();

        for (List<Course> coursesInTerm : coursesSorted) {
            Term courseTerm = (coursesSorted.size() > 0) ? coursesInTerm.get(0).term : null;
            String termString = (courseTerm != null) ?
                    courseTerm.getTermString() + " " + courseTerm.getYear() : "General";

            //used to determine  if this term has any grades,
            //if the term has no grades, we do not add this term to the tree
            boolean termHasAnyGrades = false;

            //make a term header item, and make a treenode using it
            TermHeaderViewHolder.TermHeaderItem termNodeItem = new TermHeaderViewHolder.TermHeaderItem(termString);
            TreeNode termNode = new TreeNode(termNodeItem).setViewHolder(new TermHeaderViewHolder(getContext()));

            //for each course, get its grades
            for (Course currCourse : coursesInTerm) {
                if (currCourse.grades == null || currCourse.grades.size() == 0)
                    continue;

                termHasAnyGrades = true;
                //create a course header item and make a treenode using it
                String courseIconCode = CourseIconProvider.getCourseIcon(currCourse.subjectCode);
                CourseViewHolder.CourseHeaderItem courseNodeItem = new CourseViewHolder.CourseHeaderItem(
                        currCourse.title,
                        courseIconCode,
                        new TreeGradeAdapter(currCourse.grades)
                );

                //set the custom view holder
                TreeNode courseNode = new TreeNode(courseNodeItem).setViewHolder(new CourseViewHolder(getContext()));
                termNode.addChild(courseNode);
            }

            //only add the term to the tree only if at least one course has one grade item
            if (termHasAnyGrades)
                root.addChild(termNode);
        }

        return root;
    }

    private void saveTreeState() {
        SharedPrefsUtil.saveTreeState(getContext(), this.treeView, SharedPrefsUtil.ALL_GRADES_TREE_TYPE);
    }

}
