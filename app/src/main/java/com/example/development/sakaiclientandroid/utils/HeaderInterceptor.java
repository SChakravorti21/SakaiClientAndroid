package com.example.development.sakaiclientandroid.utils;

import java.io.IOException;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import android.util.Log;
import android.webkit.CookieManager;

import com.example.development.sakaiclientandroid.R;

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

    private final String cookies;

    public HeaderInterceptor(String URL_1, String URL_2) {
        COOKIE_URL_1 = URL_1;
        COOKIE_URL_2 = URL_2;

        cookies = getAndParseCookies();
        Log.i("Fully parsed cookies", cookies);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        request = request.newBuilder()
                .addHeader("Cookie", cookies)
                .build();

        Response response = chain.proceed(request);
        return response;
    }

    private String getAndParseCookies() {
        CookieManager cookieManager = CookieManager.getInstance();
        String cookie1 = cookieManager.getCookie(COOKIE_URL_1);
        String cookie2 = cookieManager.getCookie(COOKIE_URL_2);

        StringBuilder sb = new StringBuilder(cookie1);
        sb.append("; ").append(cookie2);

        return sb.toString();
    }
}
