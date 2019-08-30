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

    public static final String SESSION_EXPIRED_ERROR = "Sakai session has expired";
    private final String cookies;

    public HeaderInterceptor(String cookieUrl) {
        cookies = getCookies(cookieUrl);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        // Get the request from the interception and add
        // the necessary cookies to the header for Sakai to acknowledge the request
        Request request = chain.request()
                .newBuilder()
                .addHeader("Cookie", cookies)
                .build();

        // Check if the Sakai session is active, because if not
        // then the request is considered to have failed.
        // Sakai returns a status code of 200 OK with empty responses,
        // necessitating this manual check.
        Response response = chain.proceed(request);
        String sakaiSessionHeader = response.header("X-Sakai-Session");
        if(sakaiSessionHeader == null || sakaiSessionHeader.isEmpty()) {
            throw new IOException(SESSION_EXPIRED_ERROR);
        }

        return response;
    }

    private String getCookies(String cookieUrl) {
        // Since the CookieManager was managed by reference earlier
        // in the WebViewClient, the cookies should remain updated
        // We only need one set of cookies, the Sakai cookies,
        // so this method does not need to parse any extra cookies.
        CookieManager cookieManager = CookieManager.getInstance();
        return cookieManager.getCookie(cookieUrl);
    }
}
