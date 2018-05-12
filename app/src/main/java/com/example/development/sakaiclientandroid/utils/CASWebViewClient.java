package com.example.development.sakaiclientandroid.utils;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.CookieHandler;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.util.ArrayList;

/**
 * Created by Development on 4/26/18.
 */

public class CASWebViewClient extends WebViewClient {

    CookieManager cookieManager;

    ArrayList<String> urls;

    public CASWebViewClient(CookieSyncManager syncManager) {
        super();

        urls = new ArrayList<>(5);

        cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.i("URL", url);

        view.loadUrl(url);
        //return true indicates that the system does not have to handle it
        return true;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        String cookies = cookieManager.getCookie(url);
        Log.i("All cookies", cookies);
        urls.add(url);
        cookieManager.setCookie(url, cookies);

        for(String savedUrl : urls) {
            String cookie = cookieManager.getCookie(savedUrl);
            Log.i(savedUrl, cookies);
        }
    }
}
