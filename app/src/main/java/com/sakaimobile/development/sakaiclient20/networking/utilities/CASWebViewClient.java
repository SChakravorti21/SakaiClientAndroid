package com.sakaimobile.development.sakaiclient20.networking.utilities;

import android.os.Build;
import android.webkit.CookieManager;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Development on 4/26/18.
 */

public class CASWebViewClient extends WebViewClient {

    public interface SakaiLoadedListener {
        void onLoginPageLoaded();
        void onLoginSuccess(String username, String password);
    }

    // This listener let's our LoginActivity know that
    // login was successful, and a new activity can be started
    private SakaiLoadedListener sakaiLoadedListener;
    // The login page url. We initiate auto-login (if possible)
    // once this page is loaded
    private final String baseUrl;

    // CookieManager automatically saves all cookies from requests,
    // we just need to set the acceptance policy
    private CookieManager cookieManager;
    // The url which is associated with the relevant Sakai cookies
    private final String cookieUrl;

    // This client is used to intercept a WebView request to
    // Sakai
    private OkHttpClient httpClient;
    // Keeping track of relevant headers
    private boolean gotHeaders;
    private String username, password;

    public CASWebViewClient(String baseUrl, String cookieUrl, SakaiLoadedListener loadedListener) {
        super();
        this.baseUrl = baseUrl;
        this.cookieUrl = cookieUrl;
        sakaiLoadedListener = loadedListener;
        httpClient = new OkHttpClient();
        gotHeaders = false;
        // Make sure that the CookieManager accepts all cookies
        cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
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
        if (url.startsWith("https://sakai.rutgers.edu/portal") && !gotHeaders)
            return handleRequest(view, url);

        return super.shouldInterceptRequest(view, url);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        // Attempt to auto-login when the base url loads
        if(url.equals(baseUrl)) {
            sakaiLoadedListener.onLoginPageLoaded();
        }
    }

    private WebResourceResponse handleRequest(WebView view, String url) {
        // After intercepting the request, we need to handle it
        // ourselves. This is done by creating an OkHttp3 Call,
        // which we add the Sakai cookies to. Without the cookies,
        // Sakai does not acknowledge the request.
        final Call call = httpClient.newCall(new Request.Builder()
                .url(url)
                .addHeader("Cookie", cookieManager.getCookie(cookieUrl))
                .build()
        );

        final Response response;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        // onPageStarted is not a good place to intercept credentials because
        // it is called too late in the release build variant. Instead, perform that
        // operation here. Call to post() is necessary for eval to run on the UI thread.
        view.post(() -> {
            // It is possible that we are moving away from this page,
            // so in case it is the login page, extract the username and password
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                // We get both credentials as an array to prevent synchronization issues
                // with getting username and password separately.
                view.evaluateJavascript(
                    "[document.querySelector('#username').value, document.querySelector('#password').value]",
                    credentials -> {
                        // JS-evaluated strings have double quotes surrounding them, necessitating
                        // all the null and length checks, and odd substring indices.
                        if(credentials != null && !"null".equals(credentials)
                                && !"undefined".equals(credentials) && credentials.length() > 7) {
                            final String separator = "\",\"";
                            int separatorIndex = credentials.indexOf(separator);
                            this.username = credentials.substring(2, separatorIndex);
                            this.password = credentials.substring(separatorIndex + separator.length(), credentials.length() - 2);
                        }
                        // Whether we got username and password or not, the authentication was
                        // successful and this must be indicated to the LoginActivity
                        tryCompleteLogin(response);
                    }
                );
            } else {
                // Can't evaluate JS, just login as usual
                tryCompleteLogin(response);
            }
        });

        // We need to return a WebResourceResponse, otherwise the
        // WebView will think that the request is hanging. The WebView
        // renders this response.
        // We do not need to modify the mimeType or encoding of the response.
        return new WebResourceResponse(null, null, response.body().byteStream());
    }

    private void tryCompleteLogin(Response response) {
        // After getting the response from Sakai, we can get the
        // headers if it has what we want. Specifically, we need
        // the X-Sakai-Session cookie.
        String sakaiSessionHeader = response.headers().get("x-sakai-session");
        if (sakaiSessionHeader != null && !gotHeaders) {
            gotHeaders = true;
            sakaiLoadedListener.onLoginSuccess(username, password);
        }
    }
}
