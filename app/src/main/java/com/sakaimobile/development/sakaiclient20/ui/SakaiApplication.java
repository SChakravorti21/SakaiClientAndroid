package com.sakaimobile.development.sakaiclient20.ui;

import android.app.Activity;
import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.sakaimobile.development.sakaiclient20.dependency_injection.DaggerSakaiApplicationComponent;

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
//        Fabric.with(this, new Crashlytics());
        DaggerSakaiApplicationComponent.builder()
                .applicationContext(this)
                .build()
                .inject(this);
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return activityInjector;
    }

}
