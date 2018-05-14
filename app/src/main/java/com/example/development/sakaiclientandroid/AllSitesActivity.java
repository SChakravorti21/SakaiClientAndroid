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

import com.example.development.sakaiclientandroid.models.SiteCollection;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
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

        fillListData();

        this.listAdapter = new myExpandableListAdapter(this, headersList, childsMap);
        this.expListView.setAdapter(this.listAdapter);


    }

    private void fillListData() {
        this.headersList = new ArrayList<>();
        this.childsMap = new HashMap<>();

        this.headersList.add("1");
        this.headersList.add("2");
        this.headersList.add("3");

        List<String> x = Arrays.asList("1","2","3");
        this.childsMap.put(this.headersList.get(0), x);

        List<String> y = Arrays.asList("4","5","6");
        this.childsMap.put(this.headersList.get(1), y);

        List<String> z = Arrays.asList("7","8","9");
        this.childsMap.put(this.headersList.get(2), z);
    }
}
