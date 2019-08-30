package com.sakaimobile.development.sakaiclient20.ui.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.sakaimobile.development.sakaiclient20.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class CreditsActivity extends AppCompatActivity {

    private static final String[] creditsList = {
            "Rutgers Sakai",
            "Icons8",
            "RxAndroid",
            "AndroidTreeView",
            "Retrofit",
            "OkHttp"
    };

    private static final String[] creditsURLs = {
            "https://sakai.rutgers.edu/direct/describe",
            "https://icons8.com/",
            "https://github.com/ReactiveX/RxAndroid",
            "https://github.com/bmelnychuk/AndroidTreeView",
            "https://github.com/square/retrofit",
            "https://github.com/square/okhttp"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener((v) -> onBackPressed());

        ListView creditsListView = findViewById(R.id.credits_list_view);
        creditsListView.setOnItemClickListener((parent, view, position, id) -> {
            String url = creditsURLs[position];
            openURL(url);
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.credits_item, R.id.creditNameTxt, creditsList);
        creditsListView.setAdapter(adapter);
    }


    private void openURL(String url) {
        Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        viewIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        viewIntent.setPackage("com.android.chrome");

        try {
            startActivity(viewIntent);
        } catch (ActivityNotFoundException e) {
            // Chrome is probably not installed so let the user choose
            viewIntent.setPackage(null);
            startActivity(viewIntent);
        }
    }



}
