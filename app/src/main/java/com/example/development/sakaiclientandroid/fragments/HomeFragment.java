package com.example.development.sakaiclientandroid.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.example.development.sakaiclientandroid.R;
import com.example.development.sakaiclientandroid.SitePagesActivity;
import com.example.development.sakaiclientandroid.api_models.all_sites.AllSitesAPI;
import com.example.development.sakaiclientandroid.models.Course;
import com.example.development.sakaiclientandroid.models.Term;
import com.example.development.sakaiclientandroid.utils.DataHandler;
import com.example.development.sakaiclientandroid.utils.SiteCollectionsExpandableListAdapter;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeFragment extends Fragment {

    private ArrayList<Course> courses;

    private SiteCollectionsExpandableListAdapter listAdapter;

    private List<String> headersList;
    private HashMap<String, List<String>> headerToClassTitle;
    private HashMap<String, List<String>> headerToClassSiteId;

    private HashMap<String, List<Integer>> headerToClassSubjectCode;




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

        View view = inflater.inflate(R.layout.fragment_home, null);

        if(getArguments() != null) {

            String body = getArguments().getString(getString(R.string.title_activity_nav));

            Gson gson = new Gson();
            AllSitesAPI api = gson.fromJson(body, AllSitesAPI.class);

            api.fillSitePages(body);


            ExpandableListView sitesListView = view.findViewById(R.id.lvExp);

            //give the reference of the expandable list view to this method, so it can be
            //filled
            feedExpandableListData(api, sitesListView);


            //expand the list view by default
            for(int i = 0; i < this.headersList.size(); i++) {
                sitesListView.expandGroup(i);
            }

        }

        return view;

    }



    /**
     * API object from retrofit is converted into usable courses object. This data is then
     * organized in the fillListData method and then the organized data is given to the listAdapter
     * which then displays the data.
     *
     * @param allSitesAPI API object that contains data received from retrofit.
     */
    private void feedExpandableListData(AllSitesAPI allSitesAPI, ExpandableListView expListView) {

        courses = Course.convertApiToSiteCollection(allSitesAPI.getSiteCollectionObject());

        fillListData(DataHandler.getCoursesSortedByTerm());

        listAdapter = new SiteCollectionsExpandableListAdapter(getActivity(), headersList, headerToClassTitle, headerToClassSubjectCode);
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
    private void fillListData(ArrayList<ArrayList<Course>> sorted) {

        this.headersList = new ArrayList<>();
        this.headerToClassTitle = new HashMap<>();
        this.headerToClassSubjectCode = new HashMap<>();

        //sets the Term as the headers for the expandable list view
        //each child is the name of the site in that term
        for(ArrayList<Course> sitesPerTerm : sorted) {

            //we can just look at the first site's term, since all the terms
            //should be the same, since we already sorted
            Term currTerm = sitesPerTerm.get(0).getTerm();

            String termKey = currTerm.getTermString();

            //don't put the year if the header is just General
            if(!termKey.equals("General")) {
                termKey += (" " + currTerm.getYear());
            }

            this.headersList.add(termKey);


            List<String> tempChildList = new ArrayList<>();
            List<Integer> tempSubjectCodeList = new ArrayList<>();

            //places the title of each site and its corresponding ImgResId into 2 lists
            //which are then added to the hashmap under the current term header
            for(Course collection : sitesPerTerm) {

                tempChildList.add(collection.getTitle());

                //TODO figure out a way to add the resource Id values directly, for more abstraction
                //adds subject code to hashmap
                int subjectCode = collection.getSubjectCode();
                tempSubjectCodeList.add(subjectCode);
//                int resId = RutgersSubjectCodes.getResourceIdFromSubjectCode(subjectCode, getActivity().getPackageName(), getContext());
//                tempSubjectCodeList.add(resId);
            }

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

            String classSiteId =  headerToClassSiteId.get(headersList.get(groupPosition)).get(childPosition);

            for(Course col : courses) {
                if(col.getId().equals(classSiteId)) {

                    Gson gS = new Gson();
                    Intent i = new Intent(getActivity().getApplicationContext(), SitePagesActivity.class);
                    String serialized = gS.toJson(col);

                    i.putExtra(getString(R.string.AllSitesActivity), serialized);
                    startActivity(i);
                    break;

                }
            }

            return true;
        }
    }
}
