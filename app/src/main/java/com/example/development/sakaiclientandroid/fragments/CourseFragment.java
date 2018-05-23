package com.example.development.sakaiclientandroid.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.development.sakaiclientandroid.NavActivity;
import com.example.development.sakaiclientandroid.R;
import com.example.development.sakaiclientandroid.models.Course;
import com.example.development.sakaiclientandroid.utils.DataHandler;
import com.example.development.sakaiclientandroid.utils.RequestCallback;

public class CourseFragment extends Fragment {

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


        String courseId = bundle.getString(getString(R.string.site_id));
        this.courseToView = DataHandler.getCourseFromId(courseId);
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
                    //puts the siteId into the bundle
                    Bundle bun = new Bundle();
                    bun.putString(getString(R.string.site_id), siteId);

                    SiteGradesFragment frag = new SiteGradesFragment();
                    frag.setArguments(bun);


                    //start the siteGrades fragment
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                            .replace(R.id.fragment_container, frag, null)
                            .addToBackStack(null)
                            .commit();

                }
            }
        });


        return view;
    }
}

