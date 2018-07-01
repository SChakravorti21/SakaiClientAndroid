package com.example.development.sakaiclientandroid.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.development.sakaiclientandroid.NavActivity;
import com.example.development.sakaiclientandroid.R;
import com.example.development.sakaiclientandroid.api_models.gradebook.GradebookObject;
import com.example.development.sakaiclientandroid.models.Course;
import com.example.development.sakaiclientandroid.utils.DataHandler;
import com.example.development.sakaiclientandroid.utils.custom.GradeItemAdapter;

import java.util.List;

public class SiteGradesFragment extends BaseFragment {

    private ListView siteGradesListView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String siteID;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        //gets side Id from bundle
        Bundle bun = this.getArguments();
        final Course course = (Course) bun.getSerializable(NavActivity.SITE_GRADES_TAG);


        //inflates the view
        final View view = inflater.inflate(R.layout.fragment_site_grades, null);
        this.siteGradesListView = view.findViewById(R.id.site_grades_list_view);

        this.siteID = course.getId();


        refreshGradesForSite();



        this.swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                FragmentActivity parentActivity = getActivity();

                //checking if instance to prevent casting errors
                if(parentActivity instanceof NavActivity)
                {
                    ((NavActivity) parentActivity).refreshSiteGrades(siteID, swipeRefreshLayout);
                }
            }
        });


        return view;

    }

    /**
     * Puts the grades of the current course into an adapter and adds the adapter to the
     * list view
     */
    public void refreshGradesForSite()
    {
        List<GradebookObject> gradesList = DataHandler.getGradesForCourse(this.siteID);

        if(gradesList != null) {

            //puts grades into custom adapter
            GradeItemAdapter adapter = new GradeItemAdapter(getActivity(), gradesList);
            siteGradesListView.setAdapter(adapter);
        }
        else {
            Toast.makeText(mContext, "No grades found", Toast.LENGTH_SHORT).show();
        }
    }


}
