package com.example.development.sakaiclientandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.development.sakaiclientandroid.models.Course;
import com.google.gson.Gson;

public class SitePagesActivity extends AppCompatActivity {

    private ListView sitePagesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_collection);

        //gets the site collection that was sent to it
        Intent intent = getIntent();
        String serialized = intent.getStringExtra(getString(R.string.home_fragment));

        Gson gS = new Gson();
        Course course = gS.fromJson(serialized, Course.class);



        this.sitePagesListView = findViewById(R.id.site_page_list_view);

        String[] listItems = new String[course.getSitePages().size()];

        //gives the title of each site to the adapter
        for(int i = 0; i < listItems.length; i++) {
            listItems[i] = course.getSitePages().get(i).getTitle();
        }


        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, listItems);
        this.sitePagesListView.setAdapter(adapter);

    }
}
