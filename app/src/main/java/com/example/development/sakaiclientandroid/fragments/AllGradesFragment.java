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
import com.example.development.sakaiclientandroid.utils.holders.CourseHeaderViewHolder;
import com.example.development.sakaiclientandroid.utils.holders.GradeNodeViewHolder;
import com.example.development.sakaiclientandroid.utils.holders.TermHeaderViewHolder;
import com.example.development.sakaiclientandroid.utils.requests.RequestCallback;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AllGradesFragment extends BaseFragment {

    private List<String> termHeaders;
    private HashMap<String, List<Course>> termToCourses;
    SwipeRefreshLayout swipeRefreshLayout;

    private AndroidTreeView treeView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //reset header
        ((NavActivity)getActivity()).setActionBarTitle(getString(R.string.app_name));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_all_grades, null);
        final ViewGroup containerView = view.findViewById(R.id.swiperefresh);


        this.termHeaders = new ArrayList<>();
        this.termToCourses = new HashMap<>();


        final ProgressBar spinner = getActivity().findViewById(R.id.nav_activity_progressbar);


        //if we already have grades for all sites cached, then no need to make another request
        if(DataHandler.gradesRequestedForAllSites()) {

            //prepare the headers and children
            DataHandler.prepareTermHeadersToCourses(
                    this.termHeaders,
                    this.termToCourses
            );

            spinner.setVisibility(View.GONE);
            createTreeView();
            containerView.addView(treeView.getView());
        }
        //if we have not, then must make request
        else {

            //start spinner
            spinner.setVisibility(View.VISIBLE);

            DataHandler.requestAllGrades(new RequestCallback() {

                @Override
                public void onAllGradesSuccess() {

                    //now prepare headers and children
                    DataHandler.prepareTermHeadersToCourses(
                            termHeaders,
                            termToCourses
                    );

                    spinner.setVisibility(View.GONE);

                    //create tree view
                    createTreeView();
                    containerView.addView(treeView.getView());

                }


                @Override
                public void onAllGradesFailure(Throwable throwable) {

                    //TODO failure message

                }

            });
        }



        this.swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                DataHandler.requestAllGrades(new RequestCallback() {

                    @Override
                    public void onAllGradesSuccess() {

                        swipeRefreshLayout.setRefreshing(false);
                        createTreeView();

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
    public void createTreeView()
    {
        TreeNode root = TreeNode.root();

        for(String termString : termHeaders)
        {

            //used to determine  if this term has any grades,
            //if the term has no grades, we do not add this term to the tree
            boolean termHasAnyGrades = false;

            TermHeaderViewHolder.TermHeaderItem termNodeItem = new TermHeaderViewHolder.TermHeaderItem(termString);
            TreeNode termNode = new TreeNode(termNodeItem).setViewHolder(new TermHeaderViewHolder(mContext));

            for(Course currCourse : this.termToCourses.get(termString))
            {

                //TODO give correct img to the course header
                CourseHeaderViewHolder.CourseHeaderItem courseNodeItem = new CourseHeaderViewHolder.CourseHeaderItem(
                        currCourse.getTitle()
                );

                TreeNode courseNode = new TreeNode(courseNodeItem).setViewHolder(new CourseHeaderViewHolder(mContext));

                List<GradebookObject> gradebookObjectList = currCourse.getGradebookObjectList();

                //only continue if the course has grades
                if (gradebookObjectList != null)
                {
                    termHasAnyGrades = true;

                    for (GradebookObject gradebookObject : gradebookObjectList)
                    {
                        GradeNodeViewHolder.GradeTreeItem gradeNodeItem = new GradeNodeViewHolder.GradeTreeItem(
                                gradebookObject.getItemName(),
                                gradebookObject.getGrade() + "/" + gradebookObject.getPoints()
                        );

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
    }



}
