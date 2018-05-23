package com.example.development.sakaiclientandroid.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.example.development.sakaiclientandroid.NavActivity;
import com.example.development.sakaiclientandroid.R;
import com.example.development.sakaiclientandroid.models.Course;
import com.example.development.sakaiclientandroid.models.Term;
import com.example.development.sakaiclientandroid.utils.DataHandler;
import com.example.development.sakaiclientandroid.utils.HomeFragmentExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AllCoursesFragment extends Fragment {


    private HomeFragmentExpandableListAdapter listAdapter;

    private List<String> headersList;
    private HashMap<String, List<String>> headerToClassTitle;
    private HashMap<String, List<String>> headerToClassSiteId;

    private HashMap<String, List<Integer>> headerToClassSubjectCode;
    private String showHomeOrGrades;




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


        Bundle bun = this.getArguments();
        this.showHomeOrGrades = bun.getString("showHomeOrGrades");



        View view = inflater.inflate(R.layout.fragment_all_courses, null);

        //gets courses from data handler and feeds to list view
        ArrayList<ArrayList<Course>> sortedCourses = DataHandler.getCoursesSortedByTerm();
        ExpandableListView sitesListView = view.findViewById(R.id.lvExp);

        feedExpandableListData(sortedCourses, sitesListView);


        //expands latest semester's list view by default
        sitesListView.expandGroup(0);

        //reset header
        ((NavActivity)getActivity()).setActionBarTitle(getString(R.string.app_name));


        return view;

    }


    /**
     * List of courses sorted and organized by term is fed into the expandable list view for display
     * @param sortedCourses = sorted and organized courses from DataHandler
     * @param expListView = List view to feed data into
     */
    private void feedExpandableListData(ArrayList<ArrayList<Course>> sortedCourses, ExpandableListView expListView) {


        prepareHeadersAndChildren(sortedCourses);

        listAdapter = new HomeFragmentExpandableListAdapter(getActivity(), headersList, headerToClassTitle, headerToClassSubjectCode);
        expListView.setAdapter(listAdapter);

        expListView.setOnChildClickListener(new ExpandableListViewOnChildClickListener());
    }






    /**
     * takes an ArrayList of SiteCollections already organized and sorted by term
     * puts that data into the headersList and child HashMap to be displayed in the
     * expandable list view in the current activity
     *
     * @param sorted ArrayList of ArrayList of Course Objects
     */
    private void prepareHeadersAndChildren(ArrayList<ArrayList<Course>> sorted) {

        this.headersList = new ArrayList<>();
        this.headerToClassTitle = new HashMap<>();
        this.headerToClassSubjectCode = new HashMap<>();
        this.headerToClassSiteId = new HashMap<>();

        //sets the Term as the headers for the expandable list view
        //each child is the name of the site in that term
        for(ArrayList<Course> coursesPerTerm : sorted) {

            //we can just look at the first site's term, since all the terms
            //should be the same, since we already sorted
            Term currTerm = coursesPerTerm.get(0).getTerm();

            String termKey = currTerm.getTermString();

            //don't put the year if the header is just General
            if(!termKey.equals("General")) {
                termKey += (" " + currTerm.getYear());
            }

            this.headersList.add(termKey);


            List<String> tempChildList = new ArrayList<>();
            List<Integer> tempSubjectCodeList = new ArrayList<>();
            List<String> tempSiteIdList = new ArrayList<>();

            //places the title of each site and its corresponding ImgResId into 2 lists
            //which are then added to the hashmap under the current term header
            for(Course currCourse : coursesPerTerm) {

                tempChildList.add(currCourse.getTitle());
                tempSiteIdList.add(currCourse.getId());

                //TODO figure out a way to add the resource Id values directly, for more abstraction
                //adds subject code to hashmap
                int subjectCode = currCourse.getSubjectCode();
                tempSubjectCodeList.add(subjectCode);
//                int resId = RutgersSubjectCodes.getResourceIdFromSubjectCode(subjectCode, getActivity().getPackageName(), getContext());
//                tempSubjectCodeList.add(resId);
            }

            this.headerToClassSiteId.put(termKey, tempSiteIdList);
            this.headerToClassSubjectCode.put(termKey, tempSubjectCodeList);
            this.headerToClassTitle.put(termKey, tempChildList);
        }


    }


    /**
     * when click a child, serialize the site collection and then send it to the SitePageActivity class
     */
    //TODO also make this a fragment.
    private class ExpandableListViewOnChildClickListener implements ExpandableListView.OnChildClickListener {

        @Override
        public boolean onChildClick(ExpandableListView parent, View v,
                                    int groupPosition, int childPosition, long id) {

            String courseSiteId =  headerToClassSiteId.get(headersList.get(groupPosition)).get(childPosition);


            Bundle bun = new Bundle();
            bun.putString(getString(R.string.site_id), courseSiteId);
            Fragment fragment = null;

            if(showHomeOrGrades.equals("Home")) {

                fragment = new CourseFragment();
                fragment.setArguments(bun);

            }

            else if (showHomeOrGrades.equals("Grades")) {
                fragment = new SiteGradesFragment();
                fragment.setArguments(bun);
            }

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
