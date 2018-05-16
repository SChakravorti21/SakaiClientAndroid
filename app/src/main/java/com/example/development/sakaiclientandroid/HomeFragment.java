package com.example.development.sakaiclientandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.example.development.sakaiclientandroid.api_models.all_sites.AllSitesAPI;
import com.example.development.sakaiclientandroid.models.SiteCollection;
import com.example.development.sakaiclientandroid.models.Term;
import com.example.development.sakaiclientandroid.utils.RutgersSubjectCodes;
import com.example.development.sakaiclientandroid.utils.myExpandableListAdapter;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class HomeFragment extends Fragment {

    private ArrayList<SiteCollection> siteCollections;

    private myExpandableListAdapter listAdapter;
    private ExpandableListView expListView;
    private List<String> headersList;
    private HashMap<String, List<String>> childsMap;
    private HashMap<String, List<Integer>> childImgResId;



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

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

            feedExpandableListData(api, sitesListView);


            //expand the list view by default
            for(int i = 0; i < this.headersList.size(); i++) {
                sitesListView.expandGroup(i);
            }


        }

        return view;


    }



    /**
     * API object from retrofit is converted into usable siteCollections object. This data is then
     * organized in the fillListData method and then the organized data is given to the listAdapter
     * which then displays the data.
     *
     * @param allSitesAPI API object that contains data received from retrofit.
     */
    private void feedExpandableListData(AllSitesAPI allSitesAPI, ExpandableListView expListView) {

        siteCollections = SiteCollection.convertApiToSiteCollection(allSitesAPI.getSiteCollectionObject());

        fillListData(organizeByTerm(siteCollections));

        listAdapter = new myExpandableListAdapter(getActivity(), headersList, childsMap, childImgResId);
        expListView.setAdapter(listAdapter);

        expListView.setOnChildClickListener(new ExpandableListViewOnChildClickListener());
    }


    /**
     * Organizes the SiteCollection objects by term. Makes a seperate ArrayList for sites collections
     * in the same term; terms are sorted chronologically.
     *
     * @param siteCollections List of SiteCollection objects
     * @return arraylist sorted and organized by term.
     */
    private ArrayList<ArrayList<SiteCollection>> organizeByTerm(ArrayList<SiteCollection> siteCollections) {


        //term objects extends comparator
        //sorted chronologically (most recent at the top)
        Collections.sort(siteCollections, (x, y) -> -1 * x.getTerm().compareTo(y.getTerm()));


        ArrayList<ArrayList<SiteCollection>> sorted = new ArrayList<>();

        Term currTerm = siteCollections.get(0).getTerm();
        ArrayList<SiteCollection> currSites = new ArrayList<>();

        for(SiteCollection siteCollection : siteCollections) {

            //if terms are the same, just add to current array list
            if(siteCollection.getTerm().compareTo(currTerm) == 0) {
                currSites.add(siteCollection);
            }
            //otherwise finalize the current arraylist of terms and make a new arraylist
            //to hold site collections of a different term
            else {
                sorted.add(currSites);

                currSites = new ArrayList<SiteCollection>();
                currSites.add(siteCollection);

                currTerm = siteCollection.getTerm();
            }

        }

        //add the final current sites
        sorted.add(currSites);

        return sorted;
    }

    /**
     * takes an ArrayList of SiteCollections already organized and sorted by term
     * puts that data into the headersList and child hashmap to be displayed in the
     * expandable list view in the current activity
     *
     * @param sorted ArrayList of ArrayList of SiteCollection Objects
     */
    private void fillListData(ArrayList<ArrayList<SiteCollection>> sorted) {

        this.headersList = new ArrayList<>();
        this.childsMap = new HashMap<>();
        this.childImgResId = new HashMap<>();

        //sets the Term as the headers for the expandable list view
        //each child is the name of the site in that term
        for(ArrayList<SiteCollection> sitesPerTerm : sorted) {

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
            for(SiteCollection collection : sitesPerTerm) {

                tempChildList.add(collection.getTitle());

                //adds the resource ID of that subject code to the hashmap
                int subjectCode = collection.getSubjectCode();
                int resId = RutgersSubjectCodes.getResourceIdFromSubjectCode(subjectCode, getActivity().getPackageName());

                tempSubjectCodeList.add(resId);
            }

            this.childImgResId.put(termKey, tempSubjectCodeList);
            this.childsMap.put(termKey, tempChildList);
        }


    }


    //when click a child, serialize the site collection and then send it to the SitePageActivity class
    private class ExpandableListViewOnChildClickListener implements ExpandableListView.OnChildClickListener {

        @Override
        public boolean onChildClick(ExpandableListView parent, View v,
                                    int groupPosition, int childPosition, long id) {

            String classTitle =  childsMap.get(headersList.get(groupPosition)).get(childPosition);

            for(SiteCollection col : siteCollections) {
                if(col.getTitle().equals(classTitle)) {


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
