package com.example.development.sakaiclientandroid.utils;

import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.http.HEAD;

/**
 * Created by Development on 4/26/18.
 */

public class CASWebViewClient extends WebViewClient {

    public interface SakaiLoadedListener {
        public void onSakaiMainPageLoaded(Headers headers);
    }

    private final String COOKIE_URL;

    private SakaiLoadedListener sakaiLoadedListener;
    private CookieManager cookieManager;
    private Headers savedHeaders;
    private OkHttpClient httpClient;
    private boolean gotHeaders;

    public CASWebViewClient(String url, SakaiLoadedListener loadedListener) {
        super();

        COOKIE_URL = url;
        sakaiLoadedListener = loadedListener;
        savedHeaders = null;

        httpClient = new OkHttpClient();
        gotHeaders = false;

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
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        if(url.startsWith("https://sakai.rutgers.edu/portal") && !gotHeaders) {
            return handleRequest(url);
        }

        return super.shouldInterceptRequest(view, url);
    }

    private WebResourceResponse handleRequest(String url) {
        try {
            // On Android API >= 21 you can get request method and headers
            // As I said, we need to only display "simple" page with resources
            // So it's GET without special headers
            final Call call = httpClient.newCall(new Request.Builder()
                    .url(url)
                    .addHeader("Cookie", cookieManager.getCookie(COOKIE_URL))
                    .build()
            );

            final Response response = call.execute();
            Headers temp = response.headers();
            if(temp.get("x-sakai-session") != null && !gotHeaders) {
                Log.i("Headers", response.headers().toString());
                savedHeaders = temp;
                gotHeaders = true;
            }

            return new WebResourceResponse(
                    null, // You can set something other as default content-type
                    null,  // Again, you can set another encoding as default
                    response.body().byteStream()
            );
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
            return null; // return response for bad request
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        if(url.equals(COOKIE_URL) && gotHeaders
                && sakaiLoadedListener != null ) {
            sakaiLoadedListener.onSakaiMainPageLoaded(savedHeaders);
        }
    }
}
