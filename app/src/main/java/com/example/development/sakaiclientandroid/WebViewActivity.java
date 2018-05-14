package com.example.development.sakaiclientandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.example.development.sakaiclientandroid.utils.CASWebViewClient;
import com.example.development.sakaiclientandroid.utils.SharedPrefsUtil;

import okhttp3.Headers;

public class WebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        // Get the WebView from the main view and attach the custom client
        // to it for keeping track of cookies and login completion
        final WebView loginWebView = findViewById(R.id.login_web_view);

        CASWebViewClient webViewClient = new CASWebViewClient(
                getString(R.string.COOKIE_URL_2),
                new CASWebViewClient.SakaiLoadedListener() {
                    @Override
                    public void onSakaiMainPageLoaded(Headers savedHeaders) {

                        // Once the main page loads, we should have all the cookies and
                        // headers necessary to make requests. These headers just
                        // need to be saved for the custom OkHttpClients to be able to access.
                        SharedPrefsUtil.saveHeaders(getApplicationContext(),
                                "Headers", savedHeaders);

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
        loginWebView.loadUrl(getString(R.string.CAS_BASE_URL));
    }
}
