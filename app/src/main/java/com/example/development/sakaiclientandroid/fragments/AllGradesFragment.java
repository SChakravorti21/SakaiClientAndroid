package com.example.development.sakaiclientandroid.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.development.sakaiclientandroid.NavActivity;
import com.example.development.sakaiclientandroid.R;
import com.example.development.sakaiclientandroid.api_models.gradebook.GradebookObject;
import com.example.development.sakaiclientandroid.models.Course;
import com.example.development.sakaiclientandroid.models.Term;
import com.example.development.sakaiclientandroid.utils.RutgersSubjectCodes;
import com.example.development.sakaiclientandroid.utils.custom.TreeViewItemClickListener;
import com.example.development.sakaiclientandroid.utils.holders.CourseHeaderViewHolder;
import com.example.development.sakaiclientandroid.utils.holders.GradeNodeViewHolder;
import com.example.development.sakaiclientandroid.utils.holders.TermHeaderViewHolder;
import com.example.development.sakaiclientandroid.utils.requests.RequestCallback;
import com.example.development.sakaiclientandroid.utils.requests.SharedPrefsUtil;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.ArrayList;
import java.util.List;

import static com.example.development.sakaiclientandroid.NavActivity.ALL_GRADES_TAG;


public class AllGradesFragment extends BaseFragment {

    SwipeRefreshLayout swipeRefreshLayout;

    private AndroidTreeView treeView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Bundle bun = getArguments();
        try
        {
            ArrayList<ArrayList<Course>> courses = (ArrayList<ArrayList<Course>>) bun.getSerializable(ALL_GRADES_TAG);

            createTreeView(courses);
        }
        catch (ClassCastException exception) {
            // Unable to create the tree, create a dummy tree
            //TODO: Needs better error handling
            treeView = new AndroidTreeView(getActivity(), TreeNode.root());
        }


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_all_grades, null);
        this.swipeRefreshLayout = view.findViewById(R.id.swiperefresh);


        swipeRefreshLayout.addView(treeView.getView());

        //temporarily disable animations so the animations don't play when the state
        //is being restored
        treeView.setDefaultAnimation(false);

        //state must be restored after the view is added to the layout
        String state = SharedPrefsUtil.getTreeState(mContext, SharedPrefsUtil.ALL_GRADES_TREE_TYPE);
        treeView.restoreState(state);

        treeView.setDefaultAnimation(true);


        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                FragmentActivity parentActivity = getActivity();

                //checking if instance to prevent casting errors
                if(parentActivity instanceof NavActivity)
                {
                    //set refresh boolean to true so that the request is made again forcefully
                    //reloads the current fragment, (which also remakes the request for grades)
                    ((NavActivity) parentActivity).loadAllGradesFragment(true);
                }

            }
        });


        return view;
    }


    @Override
    public void onDetach() {
        super.onDetach();

        //here we should save tree state when user navigates away from fragment
        SharedPrefsUtil.saveTreeState(mContext, treeView, SharedPrefsUtil.ALL_GRADES_TREE_TYPE);
    }


    /**
     * Creates a treeview structure using a list of terms, and a hashmap mapping
     * term to list of courses for that term, which is gotten from DataHandler
     */
    public void createTreeView(ArrayList<ArrayList<Course>> coursesSorted)
    {
        TreeNode root = TreeNode.root();


        for(ArrayList<Course> coursesInTerm : coursesSorted)
        {
            Term courseTerm = (coursesSorted.size() > 0) ? coursesInTerm.get(0).getTerm() : null;
            String termString = (courseTerm != null) ?
                    courseTerm.getTermString() + " " + courseTerm.getYear() : "General";


            //used to determine  if this term has any grades,
            //if the term has no grades, we do not add this term to the tree
            boolean termHasAnyGrades = false;

            //make a term header item, and make a treenode using it
            TermHeaderViewHolder.TermHeaderItem termNodeItem = new TermHeaderViewHolder.TermHeaderItem(termString);
            TreeNode termNode = new TreeNode(termNodeItem).setViewHolder(new TermHeaderViewHolder(mContext));

            //for each course, get its grades
            for(Course currCourse : coursesInTerm)
            {
                //create a course header item and make a treenode using it
                String courseIconCode = RutgersSubjectCodes.mapCourseCodeToIcon.get(currCourse.getSubjectCode());
                CourseHeaderViewHolder.CourseHeaderItem courseNodeItem = new CourseHeaderViewHolder.CourseHeaderItem(
                        currCourse.getTitle(),
                        currCourse.getId(),
                        courseIconCode
                );

                //set the custom view holder
                TreeNode courseNode = new TreeNode(courseNodeItem).setViewHolder(new CourseHeaderViewHolder(mContext, true));


                List<GradebookObject> gradebookObjectList = currCourse.getGradebookObjectList();
                //only continue if the course has grades
                if (gradebookObjectList != null && gradebookObjectList.size() > 0)
                {
                    termHasAnyGrades = true;

                    //for each grade item in the current course, create a node
                    for (GradebookObject gradebookObject : gradebookObjectList)
                    {

                        GradeNodeViewHolder.GradeTreeItem gradeNodeItem = new GradeNodeViewHolder.GradeTreeItem(
                                gradebookObject.getItemName(),
                                gradebookObject.getGrade(),
                                gradebookObject.getPoints()
                        );


                        //set the custom view holder
                        TreeNode gradeNode = new TreeNode(gradeNodeItem).setViewHolder(new GradeNodeViewHolder(mContext));

                        courseNode.addChild(gradeNode);
                    }

                    termNode.addChild(courseNode);

                }



            }

            //only add the term to the tree only if at least one course has one grade item
            if(termHasAnyGrades)
                root.addChild(termNode);

        }


        treeView = new AndroidTreeView(getActivity(), root);
        treeView.setDefaultAnimation(true);
        treeView.setDefaultNodeClickListener(new TreeViewItemClickListener(treeView, root));
    }



}
