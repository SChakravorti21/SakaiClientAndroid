package com.example.development.sakaiclient20.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.example.development.sakaiclient20.R;
import com.example.development.sakaiclient20.utils.requests.CASWebViewClient;

public class WebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        Toolbar toolbar = findViewById(R.id.webview_toolbar);
        setSupportActionBar(toolbar);


        // Get the WebView from the main view and attach the custom client
        // to it for keeping track of cookies and login completion
        final WebView loginWebView = findViewById(R.id.login_web_view);

        // Create a custom WebView client that will listen for when
        // authentication is complete and the main activity can be started
        CASWebViewClient webViewClient = new CASWebViewClient(
                getString(R.string.COOKIE_URL_2),
                savedHeaders -> {
                    // Once the main page loads, we should have all the cookies and
                    // headers necessary to make requests. These headers just
                    // need to be saved for the custom OkHttpClients to be able to access.
                    Intent intent = new Intent(WebViewActivity.this, MainActivity.class);
                    startActivity(intent);
                }
        );
        loginWebView.setWebViewClient(webViewClient);

        //The CAS system requires Javascript for the login to even load
        WebSettings loginSettings = loginWebView.getSettings();
        loginSettings.setJavaScriptEnabled(true);
        loginSettings.setSaveFormData(true);
        loginSettings.setSavePassword(true);

        // Load the login page once all configurations are complete
        loginWebView.loadUrl(getString(R.string.CAS_BASE_URL));
    }
}
