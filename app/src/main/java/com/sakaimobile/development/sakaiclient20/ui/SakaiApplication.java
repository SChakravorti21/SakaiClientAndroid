package com.sakaimobile.development.sakaiclient20.ui;

import android.app.DownloadManager;
import android.content.IntentFilter;

import com.crashlytics.android.Crashlytics;
import com.sakaimobile.development.sakaiclient20.BuildConfig;
import com.sakaimobile.development.sakaiclient20.dependency_injection.DaggerSakaiApplicationComponent;
import com.sakaimobile.development.sakaiclient20.networking.services.SessionService;
import com.sakaimobile.development.sakaiclient20.ui.custom_components.DownloadCompleteReceiver;
import com.sakaimobile.development.sakaiclient20.ui.helpers.CourseIconProvider;
import com.squareup.leakcanary.LeakCanary;

import javax.inject.Inject;

import androidx.multidex.MultiDexApplication;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;
import io.fabric.sdk.android.Fabric;
import io.reactivex.plugins.RxJavaPlugins;

public class SakaiApplication extends MultiDexApplication
        implements HasAndroidInjector {

    @Inject SessionService sessionService;
    @Inject DispatchingAndroidInjector<Object> androidInjector;


    @Override
    public AndroidInjector<Object> androidInjector() {
        return this.androidInjector;
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
        // The download receiver listens for completed file downloads to open them
        registerDownloadReceiver();
        // The context is needed to initialize the course icon provider since they are
        // listed in a JSON asset file
        CourseIconProvider.initializeCourseIcons(this);
        // Although we perform error-handling and let the user know of such errors,
        // sometimes RxJava is unable to deliver an exception, which crashes the app
        RxJavaPlugins.setErrorHandler(Throwable::printStackTrace);

        if(!BuildConfig.DEBUG)
            Fabric.with(this, new Crashlytics());

        DaggerSakaiApplicationComponent.builder()
                .applicationContext(this)
                .build()
                .inject(this);
    }

    public void registerDownloadReceiver() {
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        DownloadCompleteReceiver receiver = new DownloadCompleteReceiver();
        registerReceiver(receiver, filter);
    }

}
