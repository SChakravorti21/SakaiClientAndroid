package com.example.development.sakaiclientandroid.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.example.development.sakaiclientandroid.utils.GradeItemAdapter;
import com.example.development.sakaiclientandroid.utils.RequestCallback;

import java.util.List;

public class SiteGradesFragment extends Fragment {

    private ListView siteGradesListView;
    private ProgressBar spinner;

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


        DataHandler.requestGradesForSite(siteId, new RequestCallback() {

            @Override
            public void onSiteGradesSuccess() {


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

            @Override
            public void onSiteGradesFailure(Throwable t) {
                Log.d("fail", "fail");
            }
        });

        return view;

    }


}
