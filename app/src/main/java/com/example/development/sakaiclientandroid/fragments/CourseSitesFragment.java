package com.example.development.sakaiclientandroid.fragments;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.development.sakaiclientandroid.NavActivity;
import com.example.development.sakaiclientandroid.R;
import com.example.development.sakaiclientandroid.models.Course;
import com.example.development.sakaiclientandroid.utils.DataHandler;
import com.example.development.sakaiclientandroid.utils.requests.RequestCallback;

import static com.example.development.sakaiclientandroid.NavActivity.SITE_GRADES_TAG;

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

                    loadSiteGradesFragment(siteId);

                }
            }
        });


        return view;
    }


    /**
     * /**
     * Loads the site grade fragment
     * @param siteId site id of the course whose grades to display
     * @throws IllegalStateException if the current activity is not NavActivity
     */
    public void loadSiteGradesFragment(String siteId) throws IllegalStateException
    {

        FragmentActivity activity = getActivity();

        //check if the current activity is a nav activity
        if(activity instanceof NavActivity)
        {
            //start the progress bar spinner
            final NavActivity navActivity = (NavActivity)activity;
            navActivity.startProgressBar();


            DataHandler.requestGradesForSite(siteId, false, new RequestCallback()
            {

                @Override
                public void onSiteGradesSuccess(Course course)
                {

                    //stop the progress bar
                    navActivity.stopProgressBar();

                    if(course == null) {
                        Toast.makeText(navActivity, "Course has no grades", Toast.LENGTH_LONG).show();
                    }
                    //course has grades
                    else {
                        Bundle b = new Bundle();
                        b.putSerializable(SITE_GRADES_TAG, course);

                        SiteGradesFragment frag = new SiteGradesFragment();
                        frag.setArguments(b);


                        //show animations =  true
                        //add to the back stack = true, since we want to be able to click back from this screen
                        navActivity.loadFragment(frag, true, true);

                        navActivity.setActionBarTitle("Gradebook: " + course.getTitle());
                    }
                }

                @Override
                public void onSiteGradesFailure(Throwable t)
                {
                    //show a network error toast
                    navActivity.stopProgressBar();
                    navActivity.showNetworkErrorToast();
                }
            });
        }

        else {
            throw new IllegalStateException("Activity is not NavActivity!");
        }


    }
}

