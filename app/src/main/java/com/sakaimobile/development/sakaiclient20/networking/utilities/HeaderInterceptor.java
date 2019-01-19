package com.sakaimobile.development.sakaiclient20.networking.utilities;

import android.util.Log;
import android.webkit.CookieManager;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Development on 5/12/18.
 */

public class HeaderInterceptor implements Interceptor {

    private final String cookieUrl;
    private final String cookies;

    public HeaderInterceptor(String url) {
        cookieUrl = url;
        cookies = getCookies();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        // Get the request from the interception and add
        // the necessary cookies to the header for Sakai to acknowledge the request
        Request request = chain.request()
                .newBuilder()
                .addHeader("Cookie", cookies)
                .build();
        return chain.proceed(request);
    }

    private String getCookies() {
        // Since the CookieManager was managed by reference earlier
        // in the WebViewClient, the cookies should remain updated
        // We only need one set of cookies, the Sakai cookies,
        // so this method does not need to parse any extra cookies.
        CookieManager cookieManager = CookieManager.getInstance();
        return cookieManager.getCookie(cookieUrl);
    }
}
