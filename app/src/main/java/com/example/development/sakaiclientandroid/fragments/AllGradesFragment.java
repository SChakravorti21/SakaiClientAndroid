package com.example.development.sakaiclientandroid.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.development.sakaiclientandroid.NavActivity;
import com.example.development.sakaiclientandroid.R;
import com.example.development.sakaiclientandroid.api_models.gradebook.GradebookObject;
import com.example.development.sakaiclientandroid.models.Course;
import com.example.development.sakaiclientandroid.utils.DataHandler;
import com.example.development.sakaiclientandroid.utils.custom.TreeViewItemClickListener;
import com.example.development.sakaiclientandroid.utils.holders.CourseHeaderViewHolder;
import com.example.development.sakaiclientandroid.utils.holders.GradeNodeViewHolder;
import com.example.development.sakaiclientandroid.utils.holders.TermHeaderViewHolder;
import com.example.development.sakaiclientandroid.utils.requests.RequestCallback;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.development.sakaiclientandroid.NavActivity.ALL_GRADES_TAG;
import static com.example.development.sakaiclientandroid.NavActivity.COURSES_TAG;

public class AllGradesFragment extends BaseFragment {

    SwipeRefreshLayout swipeRefreshLayout;

    private List<String> termHeaders;
    private HashMap<String, List<Course>> termToCourses;

    private AndroidTreeView treeView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        this.termHeaders = new ArrayList<>();
        this.termToCourses = new HashMap<>();



        Bundle bun = getArguments();
        try
        {
//            ArrayList<ArrayList<Course>> courses = (ArrayList<ArrayList<Course>>) bun.getSerializable(ALL_GRADES_TAG);

            DataHandler.prepareTermHeadersToCourses(
                    termHeaders,
                    termToCourses
            );

            createTreeView(termHeaders, termToCourses);

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
        final ViewGroup containerView = view.findViewById(R.id.swiperefresh);

        containerView.addView(treeView.getView());


        this.swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                DataHandler.requestAllGrades(new RequestCallback() {

                    @Override
                    public void onAllGradesSuccess(ArrayList<ArrayList<Course>> response) {

                        swipeRefreshLayout.setRefreshing(false);
                        createTreeView(termHeaders, termToCourses);

                        //add the newly created tree to the viewgroup
                        containerView.addView(treeView.getView());


                        Toast.makeText(mContext, getString(R.string.fetched_grades), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onAllGradesFailure(Throwable throwable) {
                        //TODO error
                    }
                });
            }
        });


        return view;
    }


    /**
     * Creates a treeview structure using a list of terms, and a hashmap mapping
     * term to list of courses for that term, which is gotten from DataHandler
     */
    public void createTreeView(List<String> termHeaders, HashMap<String, List<Course>> termToCourses)
    {
        TreeNode root = TreeNode.root();

        // The courses as returned by the DataHandler are already sorted by term,
        // so we just need to loop through them to create the terms with all
        // courses and their grades
        for(String termString : termHeaders)
        {

            //used to determine  if this term has any grades,
            //if the term has no grades, we do not add this term to the tree
            boolean termHasAnyGrades = false;

            //make a term header item, and make a treenode using it
            TermHeaderViewHolder.TermHeaderItem termNodeItem = new TermHeaderViewHolder.TermHeaderItem(termString);
            TreeNode termNode = new TreeNode(termNodeItem).setViewHolder(new TermHeaderViewHolder(mContext));

            //for each course, get its grades
            for(Course currCourse : termToCourses.get(termString))
            {
                //create a course header item and make a treenode using it
                //TODO give correct img to the course header
                CourseHeaderViewHolder.CourseHeaderItem courseNodeItem = new CourseHeaderViewHolder.CourseHeaderItem(
                        currCourse.getTitle()
                );

                //set the custom view holder
                TreeNode courseNode = new TreeNode(courseNodeItem).setViewHolder(new CourseHeaderViewHolder(mContext));

                List<GradebookObject> gradebookObjectList = currCourse.getGradebookObjectList();
                //only continue if the course has grades
                if (gradebookObjectList != null)
                {
                    termHasAnyGrades = true;

                    //for each grade item in the current course, create a node
                    for (GradebookObject gradebookObject : gradebookObjectList)
                    {

                        String grade = gradebookObject.getGrade();
                        if(grade == null)
                        {
                            grade = "";
                        }

                        GradeNodeViewHolder.GradeTreeItem gradeNodeItem = new GradeNodeViewHolder.GradeTreeItem(
                                gradebookObject.getItemName(),
                                grade + "/" + gradebookObject.getPoints()
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
