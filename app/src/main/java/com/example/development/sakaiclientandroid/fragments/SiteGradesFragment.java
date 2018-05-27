package com.example.development.sakaiclientandroid.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.development.sakaiclientandroid.NavActivity;
import com.example.development.sakaiclientandroid.R;
import com.example.development.sakaiclientandroid.api_models.gradebook.AssignmentObject;
import com.example.development.sakaiclientandroid.models.Course;
import com.example.development.sakaiclientandroid.utils.DataHandler;
import com.example.development.sakaiclientandroid.utils.custom.GradeItemAdapter;
import com.example.development.sakaiclientandroid.utils.requests.RequestCallback;

import java.util.List;

public class SiteGradesFragment extends BaseFragment {

    private ListView siteGradesListView;
    private ProgressBar spinner;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        //gets side Id from bundle
        Bundle bun = this.getArguments();
        final String siteId = bun.getString(getString(R.string.site_id));


        //changes app title
        Course currCourse = DataHandler.getCourseFromId(siteId);
        ((NavActivity)getActivity()).setActionBarTitle("Gradebook: " + currCourse.getTitle()
        );


        //inflates the view
        final View view = inflater.inflate(R.layout.fragment_site_grades, null);
        this.siteGradesListView = view.findViewById(R.id.site_grades_list_view);

        //starts spinning a spinner
        this.spinner = view.findViewById(R.id.site_grades_progressbar);
        this.spinner.setVisibility(View.VISIBLE);

        //if the grades are already in storage, don't need to make request
        if(DataHandler.gradesRequestedForSite(siteId)) {
            fillGrades(siteId, view);
        }
        //otherwise, we must request
        else {
            DataHandler.requestGradesForSite(siteId, new RequestCallback() {

                @Override
                public void onSiteGradesSuccess() {
                    fillGrades(siteId, view);
                }

                @Override
                public void onSiteGradesFailure(Throwable t) {
                    //TODO proper failure
                    Log.d("fail", "fail");
                }
            });
        }


        this.swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                DataHandler.requestGradesForSite(siteId, new RequestCallback() {

                    @Override
                    public void onSiteGradesSuccess() {
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onSiteGradesFailure(Throwable throwable) {
                        //TODO error
                    }
                });
            }
        });


        return view;

    }

    /**
     * Does miscellaneous things such as disable the spinner, and put the grades into the list view
     * or show a No Grades Found textview
     * @param siteId = SiteId of the course to show the grades of
     * @param view = view that was inflated.
     */
    private void fillGrades(String siteId, View view) {
        //makes request for grades for this site and gets them
        List<AssignmentObject> gradesList = DataHandler.getGradesForCourse(siteId);

        //makes spinner invisible
        spinner.setVisibility(View.GONE);

        if(gradesList != null) {

            //puts grades into custom adapter
            GradeItemAdapter adapter = new GradeItemAdapter(getActivity(), gradesList);
            siteGradesListView.setAdapter(adapter);
        }
        else {
            TextView noGradesTxt = view.findViewById(R.id.txt_no_grades);
            noGradesTxt.setVisibility(View.VISIBLE);
        }
    }


}
