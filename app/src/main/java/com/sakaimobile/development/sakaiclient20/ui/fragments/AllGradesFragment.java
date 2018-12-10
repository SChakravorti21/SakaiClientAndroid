package com.sakaimobile.development.sakaiclient20.ui.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sakaimobile.development.sakaiclient20.R;
import com.sakaimobile.development.sakaiclient20.models.Term;
import com.sakaimobile.development.sakaiclient20.networking.utilities.SharedPrefsUtil;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Course;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Grade;
import com.sakaimobile.development.sakaiclient20.ui.helpers.RutgersSubjectCodes;
import com.sakaimobile.development.sakaiclient20.ui.listeners.TreeViewItemClickListener;
import com.sakaimobile.development.sakaiclient20.ui.viewholders.CourseHeaderViewHolder;
import com.sakaimobile.development.sakaiclient20.ui.viewholders.GradeNodeViewHolder;
import com.sakaimobile.development.sakaiclient20.ui.viewholders.TermHeaderViewHolder;
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.GradeViewModel;
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.ViewModelFactory;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;


public class AllGradesFragment extends Fragment {

    @Inject
    ViewModelFactory viewModelFactory;

    private AndroidTreeView treeView;

    // list of courses to display
    private List<List<Course>> courses;


    public static AllGradesFragment newInstance(List<List<Course>> courses) {

        AllGradesFragment allGradesFragment = new AllGradesFragment();
        allGradesFragment.courses = courses;
        return allGradesFragment;
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_all_grades, null);
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swiperefresh);

        createTreeView(this.courses);
        swipeRefreshLayout.addView(treeView.getView());

        //temporarily disable animations so the animations don't play when the state
        //is being restored
        treeView.setDefaultAnimation(false);

        //state must be restored after the view is added to the layout
        String state = SharedPrefsUtil.getTreeState(getContext(), SharedPrefsUtil.ALL_GRADES_TREE_TYPE);
        treeView.restoreState(state);

        treeView.setDefaultAnimation(true);


        swipeRefreshLayout.setOnRefreshListener(() -> {
            ViewModelProviders.of(getActivity(), viewModelFactory)
                    .get(GradeViewModel.class)
                    .refreshAllData();
        });


        return view;
    }


    @Override
    public void onDetach() {
        super.onDetach();

        //here we should save tree state when user navigates away from fragment
        SharedPrefsUtil.saveTreeState(getContext(), treeView, SharedPrefsUtil.ALL_GRADES_TREE_TYPE);
    }


    /**
     * Creates a treeview structure using a list of terms, and a hashmap mapping
     * term to list of courses for that term, which is gotten from DataHandler
     */
    public void createTreeView(List<List<Course>> coursesSorted) {
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
                //create a course header item and make a treenode using it
                String courseIconCode = RutgersSubjectCodes.mapCourseCodeToIcon.get(currCourse.subjectCode);
                CourseHeaderViewHolder.CourseHeaderItem courseNodeItem = new CourseHeaderViewHolder.CourseHeaderItem(
                        currCourse.title,
                        currCourse.siteId,
                        courseIconCode
                );

                //set the custom view holder
                TreeNode courseNode = new TreeNode(courseNodeItem).setViewHolder(new CourseHeaderViewHolder(getContext(), true));


                List<Grade> gradebookObjectList = currCourse.grades;
                //only continue if the course has grades
                if (gradebookObjectList != null && gradebookObjectList.size() > 0) {
                    termHasAnyGrades = true;

                    //for each grade item in the current course, create a node
                    for (Grade grade : gradebookObjectList) {

                        GradeNodeViewHolder.GradeTreeItem gradeNodeItem = new GradeNodeViewHolder.GradeTreeItem(
                                grade.itemName,
                                grade.grade,
                                grade.points
                        );


                        //set the custom view holder
                        TreeNode gradeNode = new TreeNode(gradeNodeItem).setViewHolder(new GradeNodeViewHolder(getContext()));

                        courseNode.addChild(gradeNode);
                    }

                    termNode.addChild(courseNode);

                }


            }

            //only add the term to the tree only if at least one course has one grade item
            if (termHasAnyGrades)
                root.addChild(termNode);

        }


        treeView = new AndroidTreeView(getActivity(), root);
        treeView.setDefaultAnimation(true);
        treeView.setDefaultNodeClickListener(new TreeViewItemClickListener(treeView, root));
    }


}
