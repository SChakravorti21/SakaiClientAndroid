package com.example.development.sakaiclientandroid;

import android.content.Intent;
import android.database.DataSetObserver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.development.sakaiclientandroid.models.SiteCollection;
import com.example.development.sakaiclientandroid.models.SitePage;
import com.example.development.sakaiclientandroid.models.Term;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class AllSitesActivity extends AppCompatActivity {

    public static final String TAG = "AllSitesActivity";
    private myExpandableListAdapter listAdapter;
    private ExpandableListView expListView;
    private List<String> headersList;
    private HashMap<String, List<String>> childsMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_sites);


        Intent i = getIntent();
        String serialized = i.getStringExtra("siteCollections");

        Log.d("serialized", serialized);

        //gets the type of custom class that was made
        Type listType = new TypeToken<ArrayList<SiteCollection>>(){}.getType();

        //deserializes the json string
        Gson gS = new Gson();
        ArrayList<SiteCollection> siteCollections = gS.fromJson(serialized, listType);


        this.expListView = findViewById(R.id.lvExp);

        fillListData(organizeByTerm(siteCollections));

        this.listAdapter = new myExpandableListAdapter(this, headersList, childsMap);
        this.expListView.setAdapter(this.listAdapter);

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Toast.makeText(
                        getApplicationContext(),
                        headersList.get(groupPosition)
                                + " : "
                                + childsMap.get(
                                headersList.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();
                return true;
            }
        });

    }

    /**
     * Organizes the SiteCollection objects by term. Makes a seperate ArrayList for sites collections
     * in the same term, and terms are sorted chronologically.
     *
     * @param siteCollections List of SiteCollection objects
     * @return arraylist sorted and organized by term.
     */
    private ArrayList<ArrayList<SiteCollection>> organizeByTerm(ArrayList<SiteCollection> siteCollections) {


        //term objects extends comparator
        Collections.sort(siteCollections, (x, y) -> x.getTerm().compareTo(y.getTerm()));


        ArrayList<ArrayList<SiteCollection>> sorted = new ArrayList<ArrayList<SiteCollection>>();

        Term currTerm = siteCollections.get(0).getTerm();
        ArrayList<SiteCollection> currSites = new ArrayList<>();

        for(SiteCollection siteCollection : siteCollections) {

            //if terms are the same, just add to current array list
            if(siteCollection.getTerm().compareTo(currTerm) == 0) {
                currSites.add(siteCollection);
            }
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


        for(ArrayList<SiteCollection> sitesPerTerm : sorted) {
            Term currTerm = sitesPerTerm.get(0).getTerm();


            String termKey = currTerm.getTermString();

            if(!termKey.equals("None")) {
                termKey += (" " + currTerm.getYear());
            }

            this.headersList.add(termKey);


            List<String> tempChildList = new ArrayList<>();

            for(SiteCollection collection : sitesPerTerm) {
                tempChildList.add(collection.getTitle());
            }


            this.childsMap.put(termKey, tempChildList);
        }

    }
}
