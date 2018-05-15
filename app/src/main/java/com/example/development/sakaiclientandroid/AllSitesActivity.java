package com.example.development.sakaiclientandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.example.development.sakaiclientandroid.api_models.all_sites.AllSitesAPI;
import com.example.development.sakaiclientandroid.api_models.all_sites.SiteCollectionObject;
import com.example.development.sakaiclientandroid.models.SiteCollection;
import com.example.development.sakaiclientandroid.models.Term;
import com.example.development.sakaiclientandroid.services.SakaiService;
import com.example.development.sakaiclientandroid.utils.HeaderInterceptor;
import com.example.development.sakaiclientandroid.utils.myExpandableListAdapter;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AllSitesActivity extends AppCompatActivity {

    public static final String TAG = "AllSitesActivity";

    private ArrayList<SiteCollection> siteCollections;

    private myExpandableListAdapter listAdapter;
    private ExpandableListView expListView;
    private List<String> headersList;
    private HashMap<String, List<String>> childsMap;

    private String baseUrl;
    private String cookieUrl;

    OkHttpClient httpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_sites);




        // Get the base url for the Sakai API
        baseUrl = getString(R.string.BASE_URL);
        // Get the url which has the relevant cookies for Sakai
        cookieUrl = getString(R.string.COOKIE_URL_1);

        // Create the custom OkHttpClient with the interceptor to inject
        // cookies into every request
        HeaderInterceptor interceptor = new HeaderInterceptor(this, cookieUrl);
        httpClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        // The Retrofit instance allows us to construct our own services
        // that will make network requests
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();

        // Making a test request using a retrofit client
        // and the getAllSites endpoint (which fetches "site.json")
        SakaiService sakaiService = retrofit.create(SakaiService.class);
        Call<AllSitesAPI> fetchSitesCall = sakaiService.getAllSites();
        fetchSitesCall.enqueue(new Callback<AllSitesAPI>() {
            @Override
            public void onResponse(Call<AllSitesAPI> call, Response<AllSitesAPI> response) {
                Log.i("Response", "SUCCESS!");
                Log.i("Status Code", "" + response.code());

                AllSitesAPI allSitesAPI = response.body();

                if(allSitesAPI.getSiteCollectionObject().size() == 0) {
                    Log.i("List size", "no sites");
                    Toast.makeText(getApplicationContext(), "Error: No sites", Toast.LENGTH_SHORT).show();
                } else {
                    // Log each site that was fetched
                    for(SiteCollectionObject site : allSitesAPI.getSiteCollectionObject()) {
                        Log.i("SiteCollectionObject", site.toString());
                    }
                }

                //on a success, we give the returned sites to the feedExpandableListData so that
                //we can display the data correctly
                feedExpandableListData(allSitesAPI);
            }

            @Override
            public void onFailure(Call<AllSitesAPI> call, Throwable t) {
                // TODO: Handle errors give proper error message
                Log.i("Response", "failure");
                Log.e("Response error", t.getMessage());

                Toast.makeText(getApplicationContext(), "Authentication Error", Toast.LENGTH_SHORT).show();
            }
        });




    }

    /**
     * API object from retrofit is converted into usable siteCollections object. This data is then
     * organized in the fillListData method and then the organized data is given to the listAdapter
     * which then displays the data.
     *
     * @param allSitesAPI API object that contains data received from retrofit.
     */
    private void feedExpandableListData(AllSitesAPI allSitesAPI) {
        siteCollections = SiteCollection.convertApiToSiteCollection(allSitesAPI.getSiteCollectionObject());
        expListView = findViewById(R.id.lvExp);

        fillListData(organizeByTerm(siteCollections));

        listAdapter = new myExpandableListAdapter(getApplicationContext(), headersList, childsMap);
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
        Collections.sort(siteCollections, (x, y) -> -1 * x.getTerm().compareTo(y.getTerm()));


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

            if(!termKey.equals("General")) {
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


    private class ExpandableListViewOnChildClickListener implements ExpandableListView.OnChildClickListener {

        @Override
        public boolean onChildClick(ExpandableListView parent, View v,
                                    int groupPosition, int childPosition, long id) {

            String classTitle =  childsMap.get(headersList.get(groupPosition)).get(childPosition);

            for(SiteCollection col : siteCollections) {
                if(col.getTitle().equals(classTitle)) {


                    Gson gS = new Gson();
                    Intent i = new Intent(AllSitesActivity.this, SitePagesActivity.class);
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
