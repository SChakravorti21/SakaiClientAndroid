package com.example.development.sakaiclientandroid.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.development.sakaiclientandroid.NavActivity;
import com.example.development.sakaiclientandroid.R;
import com.example.development.sakaiclientandroid.models.Course;
import com.example.development.sakaiclientandroid.utils.DataHandler;

public class CourseSitesFragment extends BaseFragment {

    private ListView sitePagesListView;
    private Course courseToView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_course, null);


        Bundle bundle = this.getArguments();
        if(bundle == null) {
            //TODO error message even though we shouldn't get an error here
            return view;
        }


        this.courseToView = (Course) bundle.getSerializable(NavActivity.COURSE_TAG);
        final String[] siteTitles = new String[this.courseToView.getSitePages().size()];
        for(int i = 0; i < siteTitles.length; i++) {
            siteTitles[i] = this.courseToView.getSitePages().get(i).getTitle();
        }


        //set activity title to current course
        ((NavActivity) getActivity()).setActionBarTitle(courseToView.getTitle());

        this.sitePagesListView = view.findViewById(R.id.sites_list_view);
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, siteTitles);
        this.sitePagesListView.setAdapter(adapter);


        this.sitePagesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {

                String siteName = (String) sitePagesListView.getItemAtPosition(pos);


                if(siteName.equals(getString(R.string.gradebook))) {

                    final String siteId = courseToView.getId();

                    ((NavActivity) getActivity()).loadSiteGradesFragment(false, siteId);

                }
            }
        });


        return view;
    }
}

