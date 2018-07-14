package com.example.development.sakaiclientandroid.utils.requests;

import android.content.Context;

import com.example.development.sakaiclientandroid.R;
import com.example.development.sakaiclientandroid.api_models.assignments.AllAssignments;
import com.example.development.sakaiclientandroid.api_models.gradebook.AllGradesObject;
import com.example.development.sakaiclientandroid.api_models.gradebook.GradebookCollectionObject;
import com.example.development.sakaiclientandroid.services.SakaiService;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Development on 5/21/18.
 */

public class RequestManager {

    private static OkHttpClient httpClient;
    private static SakaiService sakaiService;

    public static void createRetrofitInstance(Context context) {
        // Get the base url for the Sakai API
        String baseUrl = context.getString(R.string.BASE_URL);
        // Get the url which has the relevant cookies for Sakai
        String cookieUrl = context.getString(R.string.COOKIE_URL_1);

        // Create the custom OkHttpClient with the interceptor to inject
        // cookies into every request
        HeaderInterceptor interceptor = new HeaderInterceptor(context, cookieUrl);
        httpClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        // The Retrofit instance allows us to construct our own services
        // that will make network requests
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();

        // Create the Retrofit service with all endpoints
        sakaiService = retrofit.create(SakaiService.class);
    }

    /**
     * Cancels all ongoing requests. This is called whenever a new request needs to be made.
     * If a new request needs to be made, it means that the UI elements that needed data from
     * past requests are no longer active, making requests for those UI elements obsolete.
     */
    private static void purgeRequestQueue() {
        httpClient.dispatcher().cancelAll();
    }

    public static void fetchAllSites(Callback<ResponseBody> responseCallback) {
        purgeRequestQueue();

        Call<ResponseBody> fetchSitesCall = sakaiService.getAllSites();
        fetchSitesCall.enqueue(responseCallback);
    }


    public static void fetchAllGrades(Callback<AllGradesObject> responseCallBack) {
        purgeRequestQueue();

        Call<AllGradesObject> fetchGradesCall = sakaiService.getAllGrades();
        fetchGradesCall.enqueue(responseCallBack);
    }


    public static void fetchGradesForSite(String siteId, Callback<GradebookCollectionObject> responseCallback) {
        purgeRequestQueue();

        Call<GradebookCollectionObject> fetchGradesForSiteCall = sakaiService.getGradeForSite(siteId);
        fetchGradesForSiteCall.enqueue(responseCallback);
    }

    public static void fetchAllAssignments(Callback<AllAssignments> responseCallback) {
        purgeRequestQueue();

        Call<AllAssignments> fetchAllAssignments = sakaiService.getAllAssignments();
        fetchAllAssignments.enqueue(responseCallback);
    }

}
