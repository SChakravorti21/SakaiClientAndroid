package com.sakaimobile.development.sakaiclient20.ui;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.webkit.CookieManager;

import com.crashlytics.android.Crashlytics;
import com.sakaimobile.development.sakaiclient20.R;
import com.sakaimobile.development.sakaiclient20.dependency_injection.DaggerSakaiApplicationComponent;
import com.sakaimobile.development.sakaiclient20.networking.utilities.AuthenticationUtils;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import io.fabric.sdk.android.Fabric;

public class SakaiApplication extends Application
        implements HasActivityInjector {

    @Inject DispatchingAndroidInjector<Activity> activityInjector;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        DaggerSakaiApplicationComponent.builder()
                .applicationContext(this)
                .build()
                .inject(this);

        // We get a handle of the application lifecycle events so that we
        // can know when the app enters and exits the foreground. This allows
        // us to do session management and auto-logout the user if their
        // session becomes invalid.
        // These are handled here in the Application class as it serves
        // to consistently provide this functionality regardless of which
        // Activity is visible when the app state changes.
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new LifecycleObserver() {
            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            public void onMoveToForeground() {
                String cookieUrl = getString(R.string.COOKIE_URL_2);
                String cookie = AuthenticationUtils.getSessionCookie(SakaiApplication.this);
                CookieManager.getInstance().setCookie(cookieUrl, cookie);
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            public void onMoveToBackground() {
                String cookieUrl = getString(R.string.COOKIE_URL_2);
                AuthenticationUtils.setSessionCookie(
                        SakaiApplication.this,
                        CookieManager.getInstance().getCookie(cookieUrl)
                );
            }
        });
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return activityInjector;
    }

}
