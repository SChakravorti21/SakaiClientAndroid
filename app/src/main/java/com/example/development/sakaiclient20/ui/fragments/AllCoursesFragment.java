package com.example.development.sakaiclient20.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.development.sakaiclient20.R;
import com.example.development.sakaiclient20.models.Term;
import com.example.development.sakaiclient20.networking.utilities.SharedPrefsUtil;
import com.example.development.sakaiclient20.persistence.entities.Course;
import com.example.development.sakaiclient20.ui.MainActivity;
import com.example.development.sakaiclient20.ui.helpers.RutgersSubjectCodes;
import com.example.development.sakaiclient20.ui.listeners.TreeViewItemClickListener;
import com.example.development.sakaiclient20.ui.viewholders.CourseHeaderViewHolder;
import com.example.development.sakaiclient20.ui.viewholders.TermHeaderViewHolder;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.ArrayList;
import java.util.List;

public class AllCoursesFragment extends BaseFragment {

    public interface OnCourseSelectedListener {
        void onCourseSelected(String siteId);
    }

    List<List<Course>> courses;
    private AndroidTreeView treeView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private OnCourseSelectedListener courseSelectedListener;

    public static AllCoursesFragment newInstance(
            List<List<Course>> courses,
            OnCourseSelectedListener courseSelectedListener
    ) {
        AllCoursesFragment fragment = new AllCoursesFragment();
        fragment.courses = courses;
        fragment.courseSelectedListener = courseSelectedListener;
        return fragment;
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
        this.swipeRefreshLayout = view.findViewById(R.id.swiperefresh);

        //add the treeview to the layout
        createTreeView(this.courses);
        this.swipeRefreshLayout.addView(treeView.getView());

        //temporarily disable animations so the animations don't play when the state
        //is being restored
        treeView.setDefaultAnimation(false);

        //state must be restored after the view is added to the layout
        String state = SharedPrefsUtil.getTreeState(mContext, SharedPrefsUtil.ALL_COURSES_TREE_TYPE);
        treeView.restoreState(state);

        //re-enable animations
        treeView.setDefaultAnimation(true);

        this.swipeRefreshLayout.setOnRefreshListener(() -> {
            // TODO: Implement dependency injection so that refreshing is not a mess
        });

        return view;


    }

    @Override
    public void onDetach() {
        super.onDetach();

        //here we should save tree state
        SharedPrefsUtil.saveTreeState(mContext, treeView, SharedPrefsUtil.ALL_COURSES_TREE_TYPE);
    }


    /**
     * Creates a treeview structure using a list of terms, and a hashmap mapping
     * term to list of courses for that term, which is gotten from DataHandler
     *
     * @param coursesSorted courses sorted by term (gotten from data handler)
     */
    public void createTreeView(List<List<Course>> coursesSorted) {
        TreeNode root = TreeNode.root();

        for (List<Course> coursesInTerm : coursesSorted) {
            Term courseTerm = (coursesSorted.size() > 0) ? coursesInTerm.get(0).term : null;

            String termString = (courseTerm != null) ?
                    courseTerm.getTermString() + " " + courseTerm.getYear() : "General";

            if (termString.contains("General"))
                termString = "General";

            //make a term header item, and make a treenode using it
            TermHeaderViewHolder.TermHeaderItem termNodeItem = new TermHeaderViewHolder.TermHeaderItem(termString);
            TreeNode termNode = new TreeNode(termNodeItem).setViewHolder(new TermHeaderViewHolder(mContext));


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
                TreeNode courseNode = new TreeNode(courseNodeItem).setViewHolder(new CourseHeaderViewHolder(mContext, false));

                courseNode.setClickListener(new TreeNode.TreeNodeClickListener() {

                    //when click a course Node, open the CourseSitesFragment to show
                    //course specific information
                    @Override
                    public void onClick(TreeNode node, Object value) {
                        if (value instanceof CourseHeaderViewHolder.CourseHeaderItem) {
                            String courseSiteId = ((CourseHeaderViewHolder.CourseHeaderItem) value).siteId;

                            //here we should save tree state
                            SharedPrefsUtil.saveTreeState(mContext, treeView, SharedPrefsUtil.ALL_COURSES_TREE_TYPE);
                            courseSelectedListener.onCourseSelected(courseSiteId);
                        }
                    }
                });
                termNode.addChild(courseNode);
            }

            root.addChild(termNode);
        }

        treeView = new AndroidTreeView(getActivity(), root);
        treeView.setDefaultAnimation(true);
        treeView.setDefaultNodeClickListener(new TreeViewItemClickListener(treeView, root));
    }

}
