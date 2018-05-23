package com.example.development.sakaiclientandroid.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_site_grades, null);

        this.siteGradesListView = view.findViewById(R.id.site_grades_list_view);

        //gets site Id from arguments
        Bundle bun = this.getArguments();
        String siteId = bun.getString(getString(R.string.site_id));
        Course currCourse = DataHandler.getCourseFromId(siteId);

        //changes app title
        ((NavActivity)getActivity()).setActionBarTitle("Gradebook: " + currCourse.getTitle()
        );


        //makes request for grades for this site and gets them
        List<AssignmentObject> gradesList = DataHandler.getTempGradesPerSite();

        //puts grades into custom adapter
        GradeItemAdapter adapter = new GradeItemAdapter(getActivity(), gradesList);
        this.siteGradesListView.setAdapter(adapter);



        return view;
    }


}
