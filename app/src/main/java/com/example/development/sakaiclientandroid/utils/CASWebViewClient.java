package com.example.development.sakaiclientandroid.utils;

import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.ArrayList;

/**
 * Created by Development on 4/26/18.
 */

public class CASWebViewClient extends WebViewClient {

    ArrayList<String> cookies;

    public CASWebViewClient() {
        super();

        //There won't be a ton of cookies, so 5 should be enough
        cookies = new ArrayList<>(5);
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
        String cookies = CookieManager.getInstance().getCookie(url);
        Log.i("All cookies", cookies);

        this.cookies.add(cookies);
    }
}
