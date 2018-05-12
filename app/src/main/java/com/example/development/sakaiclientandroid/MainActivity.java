package com.example.development.sakaiclientandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;


public class MainActivity extends AppCompatActivity {

    private final String COOKIE_URL_1;
    private final String COOKIE_URL_2;

    public MainActivity() {
        super();

        COOKIE_URL_1 = getString(R.string.COOKIE_URL_1);
        COOKIE_URL_2 = getString(R.string.COOKIE_URL_2);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CookieManager cookieManager = CookieManager.getInstance();
        Log.i("Cookie 1", cookieManager.getCookie(COOKIE_URL_1));
        Log.i("Cookie 2", cookieManager.getCookie(COOKIE_URL_2));
    }
}
