package com.example.development.sakaiclientandroid.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.example.development.sakaiclientandroid.NavActivity;
import com.example.development.sakaiclientandroid.R;
import com.example.development.sakaiclientandroid.models.Course;
import com.example.development.sakaiclientandroid.utils.DataHandler;
import com.example.development.sakaiclientandroid.utils.custom.HomeFragmentExpandableListAdapter;
import com.example.development.sakaiclientandroid.utils.requests.RequestCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AllCoursesFragment extends BaseFragment{


    private List<String> termHeaders;
    private HashMap<String, List<Course>> headerToCourses;

    private SwipeRefreshLayout swipeRefreshLayout;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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


        //gets courses from data handler and feeds to list view
        ExpandableListView sitesListView = view.findViewById(R.id.all_courses_explistview);
        feedExpandableListData(sitesListView);


        //expands latest semester's list view by default
        sitesListView.expandGroup(0);

        //reset header
        ((NavActivity)getActivity()).setActionBarTitle(getString(R.string.app_name));


        this.swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                DataHandler.requestAllSites(new RequestCallback() {

                    @Override
                    public void onCoursesSuccess() {
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onCoursesFailure(Throwable throwable) {
                        //TODO failure
                    }
                });
            }
        });


        return view;


    }


    /**
     * List of courses sorted and organized by term is fed into the expandable list view for display
     * @param expListView = List view to feed data into
     */
    private void feedExpandableListData(ExpandableListView expListView) {

        this.termHeaders = new ArrayList<>();
        this.headerToCourses = new HashMap<>();

        DataHandler.prepareTermHeadersToCourses(
                this.termHeaders,
                this.headerToCourses
        );

        HomeFragmentExpandableListAdapter listAdapter = new HomeFragmentExpandableListAdapter(getActivity(), termHeaders, headerToCourses);
        expListView.setAdapter(listAdapter);

        expListView.setOnChildClickListener(new ExpandableListViewOnChildClickListener());
    }




    /**
     * when click a child, serialize the site collection and then send it to the SitePageActivity class
     */
    //TODO also make this a fragment.
    private class ExpandableListViewOnChildClickListener implements ExpandableListView.OnChildClickListener {

        @Override
        public boolean onChildClick(ExpandableListView parent, View v,
                                    int groupPosition, int childPosition, long id) {


            String courseSiteId =  headerToCourses.get(termHeaders.get(groupPosition)).get(childPosition).getId();


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

            return true;
        }
    }
}
