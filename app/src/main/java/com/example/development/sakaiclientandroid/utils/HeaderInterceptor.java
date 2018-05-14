package com.example.development.sakaiclientandroid.utils;

import java.io.IOException;
import java.net.HttpCookie;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.webkit.CookieManager;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Development on 5/12/18.
 */

public class HeaderInterceptor implements Interceptor {

    private final Context context;
    private final String cookieUrl;

    private final String cookies;

    public HeaderInterceptor(Context ctx, String url) {
        cookieUrl = url;
        context = ctx;
        cookies = getAndParseCookies();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        // Get the request from the interception
        Request request = chain.request();

        Request.Builder builder = request.newBuilder()
                .addHeader("Cookie", cookies);
        // Add headers that tell Sakai which session to use
        SharedPrefsUtil.applyHeaders(context, "Headers", builder);

        // Add the necessary cookies to the header for Sakai to acknowledge
        // the request
        request = builder.build();
        logHeaders(request);

        Response response = chain.proceed(request);
        return response;
    }

    private String getAndParseCookies() {
        // Since the CookieManager was managed by reference earlier
        // in the WebViewClient, the cookies should remain updated
        CookieManager cookieManager = CookieManager.getInstance();
        String cookie = cookieManager.getCookie(cookieUrl);

        // IMPORTANT: The cookies from both URLs have significant overlap, so this assures
        // that cookies with the same name are only included once
        Log.i("Cookie ", cookie);

        // Efficiently construct the list of cookies as name-value pairs
        StringBuilder sb = new StringBuilder();
        while(cookie.contains(";")) {
            List<HttpCookie> parsedList = HttpCookie.parse(cookie);
            HttpCookie parsed = parsedList.get(0);

            if(!parsed.getName().startsWith("___utm")) {
                sb.append(parsed.getName()).append("=").append(parsed.getValue()).append("; ");
            }

            cookie = cookie.substring(cookie.indexOf(";") + 1);
        }

        return sb.toString().substring(0, sb.length() - 2);
    }

    private void logHeaders(Request request) {
        Log.i("Intercepted request", "logging headers");
        Headers allHeaders = request.headers();
        for(String name : allHeaders.names()) {
            Log.i("Injected " + name, allHeaders.get(name));
        }
    }
}
