package com.example.development.sakaiclientandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.example.development.sakaiclientandroid.utils.CASWebViewClient;

public class WebViewActivity extends AppCompatActivity {

    private final String CASBaseUrl = "https://cas.rutgers.edu/login?service=https%3A%2F%2Fsakai.rutgers.edu%2Fsakai-login-tool%2Fcontainer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        // Get the WebView from the main view and attach the custom client
        // to it for keeping track of cookies and login completion
        WebView loginWebView = findViewById(R.id.login_web_view);
        CASWebViewClient webViewClient = new CASWebViewClient(
                getString(R.string.COOKIE_URL_1),
                getString(R.string.COOKIE_URL_2),
                new CASWebViewClient.SakaiLoadedListener() {
                    @Override
                    public void onSakaiMainPageLoaded() {
                        // Once the main page loads, we should have all the cookie necessary
                        // to make requests
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                }
        );
        loginWebView.setWebViewClient(webViewClient);

        //The CAS system requires Javascript for the login to even load
        WebSettings loginSettings = loginWebView.getSettings();
        loginSettings.setJavaScriptEnabled(true);

        // Load the login page once all configurations are complete
        loginWebView.loadUrl(CASBaseUrl);
    }
}
