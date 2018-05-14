package com.example.development.sakaiclientandroid.utils;

import java.io.IOException;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import android.content.SharedPreferences;
import android.util.Log;
import android.webkit.CookieManager;

import com.example.development.sakaiclientandroid.R;

import org.json.JSONStringer;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.http.HTTP;

/**
 * Created by Development on 5/12/18.
 */

public class HeaderInterceptor implements Interceptor {

    private final String COOKIE_URL_1;
    private final String COOKIE_URL_2;
    private final String COOKIE_URL_3;

    private final String cookies;

    public HeaderInterceptor(String URL_1, String URL_2, String URL_3) {
        COOKIE_URL_1 = URL_1;
        COOKIE_URL_2 = URL_2;
        COOKIE_URL_3 = URL_3;

        cookies = getAndParseCookies();
        Log.i("Fully parsed cookies", cookies);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        // Get the request from the interception
        Request request = chain.request();

        Request.Builder builder = request.newBuilder()
                .addHeader("Cookie", cookies);
        // Add headers that tell Sakai which session to use
        //SharedPrefsUtil.applyHeaders("Headers", builder);

        // Add the necessary cookies to the header for Sakai to acknowledge
        // the request
        request = builder.build();
        logHeaders(request);

        Response response = chain.proceed(request);
        return response;
    }

    public String getAndParseCookies() {
        // Since the CookieManager was managed by reference earlier
        // in the WebViewClient, the cookies should remain updated
        CookieManager cookieManager = CookieManager.getInstance();
        String cookie1 = cookieManager.getCookie(COOKIE_URL_1);
        String cookie2 = cookieManager.getCookie(COOKIE_URL_2);
        String cookie3 = cookieManager.getCookie(COOKIE_URL_3);

        String[] allCookies = new String[]{
                cookie3,
                cookie1,
                cookie2,
        };

        // IMPORTANT: The cookies from both URLs have significant overlap, so this assures
        // that cookies with the same name are only included once
        HashMap<String, String> uniqueCookies = new HashMap<>();

        for(String cookie : allCookies) {
            Log.i("Cookie ", cookie);

            while(cookie.contains(";")) {
                List<HttpCookie> parsedList = HttpCookie.parse(cookie);
                HttpCookie parsed = parsedList.get(0);
                if(!parsed.getName().startsWith("___utm"))
                    uniqueCookies.put(parsed.getName(), parsed.getValue());

                cookie = cookie.substring(cookie.indexOf(";") + 1);
            }
        }

        // Efficiently construct the list of cookies as name-value pairs
        StringBuilder sb = new StringBuilder();
        for(String name : uniqueCookies.keySet()) {
            sb.append(name).append("=").append(uniqueCookies.get(name)).append("; ");
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
