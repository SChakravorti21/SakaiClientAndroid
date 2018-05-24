package com.example.development.sakaiclientandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ExpandableListView;

import com.example.development.sakaiclientandroid.utils.ParentExpListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        List<String> grandparentHeaders = new ArrayList<>(Arrays.asList("grand1", "grand2", "grand3"));

        final ExpandableListView expandableListView = findViewById(R.id.grandparent_exp_listview);
        ParentExpListAdapter parentExpListAdapter = new ParentExpListAdapter(getApplicationContext(), grandparentHeaders);
        expandableListView.setAdapter(parentExpListAdapter);

    }


}
