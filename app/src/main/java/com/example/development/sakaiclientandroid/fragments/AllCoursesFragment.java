package com.example.development.sakaiclientandroid.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.example.development.sakaiclientandroid.NavActivity;
import com.example.development.sakaiclientandroid.R;
import com.example.development.sakaiclientandroid.api_models.gradebook.GradebookObject;
import com.example.development.sakaiclientandroid.models.Course;
import com.example.development.sakaiclientandroid.models.Term;
import com.example.development.sakaiclientandroid.utils.DataHandler;
import com.example.development.sakaiclientandroid.utils.custom.HomeFragmentExpandableListAdapter;
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

import static com.example.development.sakaiclientandroid.NavActivity.COURSES_TAG;

public class AllCoursesFragment extends BaseFragment{


    private AndroidTreeView treeView;
    private SwipeRefreshLayout swipeRefreshLayout;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bun = getArguments();
        try
        {
            ArrayList<ArrayList<Course>> courses = (ArrayList<ArrayList<Course>>) bun.getSerializable(COURSES_TAG);

            createTreeView(courses);
        }
        catch (ClassCastException exception) {
            // Unable to create the tree, create a dummy tree
            //TODO: Needs better error handling
            treeView = new AndroidTreeView(getActivity(), TreeNode.root());
        }
    }


    /**
     * When the fragment view is created, we want to get the responseBody from the bundle so
     * it can be displayed. This raw data is parsed, sorted, and then given to the
     * expandable list viewer for display.
     * @param inflater used to inflate our layout
     * @param container .
     * @param savedInstanceState used to get the arguments that were passed to this fragment
     * @return  created view
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_all_courses, null);
        this.swipeRefreshLayout = view.findViewById(R.id.swiperefresh);

        //add the treeview to the layout
        this.swipeRefreshLayout.addView(treeView.getView());

        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                FragmentActivity parentActivity = getActivity();

                //checking if instance to prevent casting errors
                if(parentActivity instanceof NavActivity)
                {
                    //reloads the current fragment, (which also remakes the request for courses)
                    ((NavActivity) parentActivity).loadAllCoursesFragment();
                }
            }
        });


        return view;


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

            if(termString.contains("General"))
                termString = "General";

            //make a term header item, and make a treenode using it
            TermHeaderViewHolder.TermHeaderItem termNodeItem = new TermHeaderViewHolder.TermHeaderItem(termString);
            TreeNode termNode = new TreeNode(termNodeItem).setViewHolder(new TermHeaderViewHolder(mContext));
            termNode.setClickListener(new TreeViewItemClickListener(treeView, root));

            //for each course, get its grades
            for(Course currCourse : coursesInTerm)
            {
                //create a course header item and make a treenode using it
                //TODO give correct img to the course header
                CourseHeaderViewHolder.CourseHeaderItem courseNodeItem = new CourseHeaderViewHolder.CourseHeaderItem(
                        currCourse.getTitle(),
                        currCourse.getId()
                );

                //set the custom view holder
                TreeNode courseNode = new TreeNode(courseNodeItem).setViewHolder(new CourseHeaderViewHolder(mContext));

                courseNode.setClickListener(new TreeNode.TreeNodeClickListener() {

                    //when click a course Node, open the CourseFragment to show
                    //course specific information
                    @Override
                    public void onClick(TreeNode node, Object value)
                    {
                        if(value instanceof CourseHeaderViewHolder.CourseHeaderItem) {
                            String courseSiteId = ((CourseHeaderViewHolder.CourseHeaderItem) value).siteId;

                            Bundle bun = new Bundle();
                            bun.putString(getString(R.string.site_id), courseSiteId);
                            CourseFragment fragment = new CourseFragment();
                            fragment.setArguments(bun);


                            //replaces current Fragment with CourseFragment
                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            ft.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                                    .replace(R.id.fragment_container, fragment, null)
                                    .addToBackStack(null)
                                    .commit();
                        }
                    }
                });
                termNode.addChild(courseNode);

            }

            root.addChild(termNode);

        }


        treeView = new AndroidTreeView(getActivity(), root);
        treeView.setDefaultAnimation(true);
    }

}
