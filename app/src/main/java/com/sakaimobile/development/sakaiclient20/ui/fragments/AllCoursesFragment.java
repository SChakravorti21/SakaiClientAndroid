package com.sakaimobile.development.sakaiclient20.ui.fragments;

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
import com.sakaimobile.development.sakaiclient20.ui.helpers.RutgersSubjectCodes;
import com.sakaimobile.development.sakaiclient20.ui.listeners.OnActionPerformedListener;
import com.sakaimobile.development.sakaiclient20.ui.listeners.TreeViewItemClickListener;
import com.sakaimobile.development.sakaiclient20.ui.viewholders.CourseHeaderViewHolder;
import com.sakaimobile.development.sakaiclient20.ui.viewholders.TermHeaderViewHolder;
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.ViewModelFactory;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

public class AllCoursesFragment extends Fragment {

    @Inject ViewModelFactory viewModelFactory;
    private List<List<Course>> courses;
    private AndroidTreeView treeView;
    private OnActionPerformedListener actionPerformedListener;

    public static AllCoursesFragment newInstance(
            List<List<Course>> courses,
            OnActionPerformedListener actionPerformedListener
    ) {
        AllCoursesFragment fragment = new AllCoursesFragment();
        fragment.courses = courses;
        fragment.actionPerformedListener = actionPerformedListener;
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_courses, null);
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swiperefresh);

        //add the treeview to the layout
        createTreeView(this.courses);
        swipeRefreshLayout.addView(treeView.getView());

        //temporarily disable animations so the animations don't play when the state
        //is being restored
        treeView.setDefaultAnimation(false);

        //state must be restored after the view is added to the layout
        String state = SharedPrefsUtil.getTreeState(getContext(), SharedPrefsUtil.ALL_COURSES_TREE_TYPE);
        treeView.restoreState(state);

        //re-enable animations
        treeView.setDefaultAnimation(true);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            this.actionPerformedListener.loadCoursesFragment(true);
        });

        return view;


    }

    @Override
    public void onDetach() {
        super.onDetach();

        //here we should save tree state
        SharedPrefsUtil.saveTreeState(getContext(), treeView, SharedPrefsUtil.ALL_COURSES_TREE_TYPE);
    }


    /**
     * Creates a treeview structure using a list of terms, and a hashmap mapping
     * term to list of courses for that term, which is gotten from DataHandler
     *
     * @param coursesSorted courses sorted by term (gotten from data handler)
     */
    public void createTreeView(List<List<Course>> coursesSorted) {
        TreeNode root = TreeNode.root();
        Context mContext = getContext();

        for (List<Course> coursesInTerm : coursesSorted) {
            Term courseTerm = (coursesSorted.size() > 0) ? coursesInTerm.get(0).term : null;
            String termString = courseTerm.toString();

            //make a term header item, and make a treenode using it
            TermHeaderViewHolder.TermHeaderItem termNodeItem =
                    new TermHeaderViewHolder.TermHeaderItem(termString);
            TreeNode termNode =
                    new TreeNode(termNodeItem).setViewHolder(new TermHeaderViewHolder(mContext));

            //for each course, get its grades
            for (Course currCourse : coursesInTerm) {
                //create a course header item and make a treenode using it
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
                        String courseSiteId = ((CourseHeaderViewHolder.CourseHeaderItem) value).siteId;

                        //here we should save tree state
                        SharedPrefsUtil.saveTreeState(mContext, treeView, SharedPrefsUtil.ALL_COURSES_TREE_TYPE);
                        actionPerformedListener.onCourseSelected(courseSiteId);
                    }
                });

                termNode.addChild(courseNode);
            }

            root.addChild(termNode);
        }

        treeView = new AndroidTreeView(getActivity(), root);
        treeView.setDefaultAnimation(true);
        treeView.setDefaultNodeClickListener(new TreeViewItemClickListener(treeView));
    }

}
