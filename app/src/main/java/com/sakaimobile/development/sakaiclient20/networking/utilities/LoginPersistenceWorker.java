package com.sakaimobile.development.sakaiclient20.networking.utilities;

import android.content.Context;
import android.support.annotation.NonNull;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.sakaimobile.development.sakaiclient20.R;
import com.sakaimobile.development.sakaiclient20.models.sakai.User.UserResponse;
import com.sakaimobile.development.sakaiclient20.networking.services.UserService;

import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginPersistenceWorker extends Worker {

    private UserService userService;

    // This constructor is used above by the PeriodicWorkRequest builder
    @SuppressWarnings("unused")
    public LoginPersistenceWorker(
            Context context,
            WorkerParameters workerParams) {
        super(context, workerParams);

        // There is (currently) no Dagger injection support for Worker
        // classes, and the class is generated by the Work API
        // using just the worker class name. Shoehorning DI just for this class
        // is needlessly complicated, so here we do it the good 'ol way ourselves.
        this.userService = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.BASE_URL))
                .client(new OkHttpClient.Builder()
                        .addInterceptor(new HeaderInterceptor(context.getString(R.string.COOKIE_URL_1)))
                        .build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(UserService.class);
    }

    public static void startLoginPersistenceTask() {
        Constraints workConstraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        // We will make requests roughly every ~15 minutes,
        // max window between requests will be 30 minutes,
        // which should suffice for keeping the cookies alive
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                LoginPersistenceWorker.class,
                15,
                TimeUnit.MINUTES)
                .setConstraints(workConstraints)
                .build();

        WorkManager.getInstance().enqueueUniquePeriodicWork(
                "Login Persistence",
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Work requests already run in background threads, so we will not
        // get a NetworkOnMainThread exception here
        UserResponse user = this.userService.getLoggedInUser().blockingGet();
        // If the display ID (i.e. NetID) is non-null, the cookies are still active
        return user.displayId == null ? Result.failure() : Result.success();
    }

}
