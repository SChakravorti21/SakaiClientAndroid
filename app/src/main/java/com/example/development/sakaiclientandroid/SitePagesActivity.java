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

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SitePagesActivity extends AppCompatActivity {

    private ListView sitePagesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_collection);

        Intent intent = getIntent();
        String serialized = intent.getStringExtra(getString(R.string.AllSitesActivity));

        Log.d("seri", serialized);

//        Type type = new TypeToken<SiteCollection>(){}.getType();
        Gson gS = new Gson();
        SiteCollection siteCollection = gS.fromJson(serialized, SiteCollection.class);

        Log.d("obj", siteCollection.getSitePages().size() + "");

        this.sitePagesListView = findViewById(R.id.site_page_list_view);

        String[] listItems = new String[siteCollection.getSitePages().size()];

        for(int i = 0; i < listItems.length; i++) {
            listItems[i] = siteCollection.getSitePages().get(i).getTitle();
        }


        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, listItems);
        this.sitePagesListView.setAdapter(adapter);

    }
}
