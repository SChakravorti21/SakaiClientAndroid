package com.example.development.sakaiclientandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.development.sakaiclientandroid.models.SiteCollection;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class AllSitesActivity extends AppCompatActivity {

    public static final String TAG = "AllSitesActivity";

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

        Log.d("testing", siteCollections.toString());



        String[] test = {"test1", "test2", "test3", "r", "r", "r", "r", "R", "r", "r", "r", "r", "R", "r"};
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, test);

        ListView listView = findViewById(R.id.all_sites_listview);
        listView.setAdapter(adapter);

    }
}
