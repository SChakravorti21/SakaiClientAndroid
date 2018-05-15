package com.example.development.sakaiclientandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.development.sakaiclientandroid.models.SiteCollection;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SiteCollectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_collection);

        Intent i = getIntent();
        String serialized = i.getStringExtra(getString(R.string.AllSitesActivity));

        Type type = new TypeToken<SiteCollection>(){}.getType();
        Gson gS = new Gson();
        SiteCollection siteCollection = gS.fromJson(serialized, type);


    }
}
