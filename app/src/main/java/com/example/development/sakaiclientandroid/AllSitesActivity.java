package com.example.development.sakaiclientandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class AllSitesActivity extends AppCompatActivity {

    public static final String TAG = "AllSitesActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_sites);

        Log.d(TAG, "on all sites activity");

        String[] test = {"test1", "test2", "test3", "r", "r", "r", "r", "R", "r", "r", "r", "r", "R", "r"};
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, test);

        ListView listView = findViewById(R.id.all_sites_listview);
        listView.setAdapter(adapter);

    }
}
