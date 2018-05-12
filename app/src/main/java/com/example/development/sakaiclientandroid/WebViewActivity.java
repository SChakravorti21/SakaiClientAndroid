package com.example.development.sakaiclientandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.example.development.sakaiclientandroid.utils.CASWebViewClient;

public class WebViewActivity extends AppCompatActivity {

    final String CASBaseUrl = "https://cas.rutgers.edu/login?service=https%3A%2F%2Fsakai.rutgers.edu%2Fsakai-login-tool%2Fcontainer";
    WebView loginWebView;
    CASWebViewClient webViewClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        loginWebView = (WebView)findViewById(R.id.login_web_view);
        webViewClient = new CASWebViewClient(CookieSyncManager.getInstance());
        loginWebView.setWebViewClient(webViewClient);
        WebSettings loginSettings = loginWebView.getSettings();
        loginSettings.setJavaScriptEnabled(true);

        loginWebView.loadUrl(CASBaseUrl);
    }
}
