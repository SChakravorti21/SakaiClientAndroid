package com.example.development.sakaiclientandroid.utils.requests;

import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Development on 4/26/18.
 */

public class CASWebViewClient extends WebViewClient {

    public interface SakaiLoadedListener {
        void onSakaiMainPageLoaded();
    }

    // This listener let's our WebViewActivity know that
    // login was successful, and a new activity can be started
    private SakaiLoadedListener sakaiLoadedListener;

    // The url which is associated with the relevant Sakai cookies
    private final String cookieUrl;

    // This client is used to intercept a WebView request to
    // Sakai
    private OkHttpClient httpClient;
    // Keeping track of receiving headers
    private boolean hasReceivedHeaders;

    public CASWebViewClient(String url, SakaiLoadedListener loadedListener) {
        super();

        cookieUrl = url;
        sakaiLoadedListener = loadedListener;

        httpClient = new OkHttpClient();
        hasReceivedHeaders = false;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        // Let our WebView handle page loading
        view.loadUrl(url);

        //return true indicates that the has handled the request
        return true;
    }


    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        // We only need to intercept once authentication is complete, as the
        // the cookies do not change afterwards
        if (url.startsWith("https://sakai.rutgers.edu/portal") && !hasReceivedHeaders) {
            return handleRequest(url);
        }

        return super.shouldInterceptRequest(view, url);
    }

    private WebResourceResponse handleRequest(String url) {
        try {
            // After intercepting the request, we need to handle it
            // ourselves. This is done by creating an OkHttp3 Call,
            // which we add the Sakai cookies to. Without the cookies,
            // Sakai does not acknowledge the request.
            final Call call = httpClient.newCall(new Request.Builder()
                    .url(url)
                    .addHeader("Cookie", CookieManager.getInstance().getCookie(cookieUrl))
                    .build()
            );

            // After getting the response from Sakai, we can get the
            // headers if it has what we want. Specifically, we need
            // the X-Sakai-Session cookie.
            final Response response = call.execute();
            Headers headers = response.headers();
            if(headers.get("x-sakai-session") != null && !hasReceivedHeaders) {
                Log.i("Headers", response.headers().toString());
                hasReceivedHeaders = true; // prevent calling onSakaiMainPageLoaded multiple times
                sakaiLoadedListener.onSakaiMainPageLoaded();
            }

            // We need to return a WebResourceResponse, otherwise the
            // WebView will think that the request is hanging. The WebView
            // renders this response.
            // We do not need to modify the mimeType or encoding of the response.
            return new WebResourceResponse(null, null,
                response.body().byteStream()
            );
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
            // TODO: Handle bad request/response
            // Perhaps by creating a separate method in the listener
            return null;
        }
    }
}
