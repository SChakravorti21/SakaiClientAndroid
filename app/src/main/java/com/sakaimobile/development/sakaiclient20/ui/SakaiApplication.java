package com.sakaimobile.development.sakaiclient20.ui;

import android.app.Activity;
import android.app.Application;
import android.app.DownloadManager;
import android.content.IntentFilter;
import android.os.Build;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.webkit.CookieManager;

import com.crashlytics.android.Crashlytics;
import com.sakaimobile.development.sakaiclient20.BuildConfig;
import com.sakaimobile.development.sakaiclient20.dependency_injection.DaggerSakaiApplicationComponent;
import com.sakaimobile.development.sakaiclient20.networking.services.SessionService;
import com.sakaimobile.development.sakaiclient20.ui.activities.WebViewActivity;
import com.sakaimobile.development.sakaiclient20.ui.custom_components.DownloadCompleteReceiver;
import com.squareup.leakcanary.LeakCanary;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.support.HasSupportFragmentInjector;
import io.fabric.sdk.android.Fabric;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class SakaiApplication extends Application
        implements HasActivityInjector, HasSupportFragmentInjector {

    @Inject SessionService sessionService;
    @Inject DispatchingAndroidInjector<Activity> activityInjector;
    @Inject DispatchingAndroidInjector<Fragment> supportFragmentInjector;

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return activityInjector;
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return supportFragmentInjector;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }

        LeakCanary.install(this);
        registerDownloadReceiver();

        if(!BuildConfig.DEBUG)
            Fabric.with(this, new Crashlytics());

        DaggerSakaiApplicationComponent.builder()
                .applicationContext(this)
                .build()
                .inject(this);

        // Session persistence cannot be achieved below version Lollipop
        // anyways, so do not bother with the moveToForeground observation
        // defined below.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            return;

        // We want to ensure that the user is still logged in if the application
        // is opened from the background. This might not be the case if WorkManager
        // fails to keep cookies alive or the OS decides to kill the background tasks
        // created by WorkManager in idle mode. The user needs to be prompter to
        // login again in this scenario.
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new LifecycleObserver() {

            private CompositeDisposable disposable = new CompositeDisposable();

            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            public void onMoveToBackGround() {
                // If app is opened and immediately closed, be sure not to have mem leaks
                disposable.dispose();
                disposable = new CompositeDisposable();
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            public void onMoveToForeground() {
                // If there are no cookies, then either we cleared them out ourselves
                // (user manually logged out) or this is a fresh app start. In either case,
                // the user will be prompted to log in anyways.
                if(!CookieManager.getInstance().hasCookies())
                    return;

                disposable.add(
                    sessionService.getLoggedInUser()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(user -> {
                            // If the user ID is not null, the session is still active.
                            if(user.userId != null) return;

                            // Otherwise, session has become inactive and user must be prompted
                            // to log in
                            Intent intent = new Intent(SakaiApplication.this, WebViewActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }, Throwable::printStackTrace)
                );
            }
        });
    }

    public void registerDownloadReceiver() {
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        DownloadCompleteReceiver receiver = new DownloadCompleteReceiver();
        registerReceiver(receiver, filter);
    }

}
