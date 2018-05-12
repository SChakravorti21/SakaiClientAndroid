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
import java.util.HashMap;

/**
 * Created by Development on 4/26/18.
 */

public class CASWebViewClient extends WebViewClient {

    public interface SakaiLoadedListener {
        public void onSakaiMainPageLoaded();
    }

    private final String COOKIE_URL_1;
    private final String COOKIE_URL_2;

    private SakaiLoadedListener sakaiLoadedListener;
    private CookieManager cookieManager;
    private HashMap<String, String> savedCookies;

    public CASWebViewClient(String URL_1, String URL_2, SakaiLoadedListener loadedListener) {
        super();

        COOKIE_URL_1 = URL_1;
        COOKIE_URL_2 = URL_2;
        sakaiLoadedListener = loadedListener;
        savedCookies = new HashMap<>();

        cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeSessionCookie();
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.i("URL", url);

        view.loadUrl(url);
        //return true indicates that the has handled the request
        return true;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        if(url.equals(COOKIE_URL_1) || url.equals(COOKIE_URL_2)) {
            String cookies = cookieManager.getCookie(url);
            Log.d("All cookies for url: " + url, cookies);
            savedCookies.put(url, cookies);
            cookieManager.setCookie(url, cookies);

            for (String savedUrl : savedCookies.keySet()) {
                String cookie = cookieManager.getCookie(savedUrl);
                Log.d("In the Cookie jar: " + savedUrl + " ", cookies);
                Log.d("In the hashmap: " + savedUrl + " ", savedCookies.get(savedUrl));
            }
        }

        if(url.equals(COOKIE_URL_2) && sakaiLoadedListener != null) {
            sakaiLoadedListener.onSakaiMainPageLoaded();
        }
    }
}
