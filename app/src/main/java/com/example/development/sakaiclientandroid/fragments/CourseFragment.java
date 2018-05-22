package com.example.development.sakaiclientandroid.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.development.sakaiclientandroid.R;
import com.example.development.sakaiclientandroid.models.Course;
import com.example.development.sakaiclientandroid.utils.DataHandler;

public class CourseFragment extends Fragment {

    private ListView sitePagesListView;

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


        String courseId = bundle.getString("courseSiteId");
        Course courseToView = DataHandler.getCourseFromId(courseId);
        String[] siteTitles = new String[courseToView.getSitePages().size()];
        for(int i = 0; i < siteTitles.length; i++) {
            siteTitles[i] = courseToView.getSitePages().get(i).getTitle();
        }


        this.sitePagesListView = view.findViewById(R.id.sites_list_view);
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, siteTitles);
        this.sitePagesListView.setAdapter(adapter);

        return view;
    }
}
