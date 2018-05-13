package com.example.development.sakaiclientandroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.example.development.sakaiclientandroid.api_models.all_sites.AllSites;
import com.example.development.sakaiclientandroid.api_models.all_sites.SiteCollection;
import com.example.development.sakaiclientandroid.services.SakaiService;
import com.example.development.sakaiclientandroid.utils.CASWebViewClient;
import com.example.development.sakaiclientandroid.utils.HeaderInterceptor;
import com.example.development.sakaiclientandroid.utils.SharedPrefsUtil;

import java.util.HashMap;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.internal.http2.Header;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebViewActivity extends AppCompatActivity {

    private final String CASBaseUrl = "https://cas.rutgers.edu/login?service=https%3A%2F%2Fsakai.rutgers.edu%2Fsakai-login-tool%2Fcontainer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        SharedPrefsUtil.setContext(getApplicationContext());

        // Get the WebView from the main view and attach the custom client
        // to it for keeping track of cookies and login completion
        final WebView loginWebView = findViewById(R.id.login_web_view);

        CASWebViewClient webViewClient = new CASWebViewClient(
                getString(R.string.COOKIE_URL_2),
                new CASWebViewClient.SakaiLoadedListener() {
                    @Override
                    public void onSakaiMainPageLoaded(Headers savedHeaders) {
                        // Once the main page loads, we should have all the cookies and
                        // headers necessary to make requests, in addition to all headers
                        SharedPrefsUtil.saveHeaders("Headers", savedHeaders);


                        String BASE_URL = getString(R.string.BASE_URL);
                        String COOKIE_URL_1 = getString(R.string.COOKIE_URL_1);
                        String COOKIE_URL_2 = getString(R.string.COOKIE_URL_2);
                        String COOKIE_URL_3 = getString(R.string.COOKIE_URL_3);

                        HeaderInterceptor interceptor = new HeaderInterceptor(COOKIE_URL_1,
                                COOKIE_URL_2, COOKIE_URL_3);
                        OkHttpClient httpClient = new OkHttpClient.Builder()
                                .addInterceptor(interceptor)
                                .build();

                        // The Retrofit instance allows us to construct our own services
                        // that will make network requests
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(BASE_URL)
                                .addConverterFactory(GsonConverterFactory.create())
                                .client(httpClient)
                                .build();

                        // Make a test request: Get the list of sites associated with a student
                        SakaiService sakaiService = retrofit.create(SakaiService.class);
                        Call<AllSites> fetchSitesCall = sakaiService.getAllSites();
                        fetchSitesCall.enqueue(new Callback<AllSites>() {
                            @Override
                            public void onResponse(Call<AllSites> call, Response<AllSites> response) {
                                Log.i("Response", "SUCCESS!");
                                Log.i("Status Code", "" + response.code());

                                AllSites allSites = response.body();
                                Log.i("Sites", allSites.toString());
                                if (allSites.getSiteCollection().size() == 0) {
                                    Log.i("List size", "no sites");
                                } else {
                                    for (SiteCollection site : allSites.getSiteCollection()) {
                                        Log.i("Site", site.toString());
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<AllSites> call, Throwable t) {
                                Log.i("Response", "failure");
                            }
                        });


//                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                        startActivity(intent);
                    }
                }
        );

        loginWebView.setWebViewClient(webViewClient);

        //The CAS system requires Javascript for the login to even load
        WebSettings loginSettings = loginWebView.getSettings();
        loginSettings.setJavaScriptEnabled(true);

        // Load the login page once all configurations are complete
        loginWebView.loadUrl(CASBaseUrl);
    }
}
