package com.example.development.sakaiclientandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;

import com.example.development.sakaiclientandroid.utils.HeaderInterceptor;

import okhttp3.OkHttpClient;


public class MainActivity extends AppCompatActivity {

    private String COOKIE_URL_1;
    private String COOKIE_URL_2;

    OkHttpClient httpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        COOKIE_URL_1 = getString(R.string.COOKIE_URL_1);
        COOKIE_URL_2 = getString(R.string.COOKIE_URL_2);

        CookieManager cookieManager = CookieManager.getInstance();
        Log.i("Cookie 1", cookieManager.getCookie(COOKIE_URL_1));
        Log.i("Cookie 2", cookieManager.getCookie(COOKIE_URL_2));

        httpClient = new OkHttpClient.Builder()
                .addInterceptor(new HeaderInterceptor(COOKIE_URL_1, COOKIE_URL_2))
                .build();
    }
}
