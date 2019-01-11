package com.sakaimobile.development.sakaiclient20.dependency_injection;

import android.content.Context;

import com.sakaimobile.development.sakaiclient20.ui.SakaiApplication;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.AndroidInjector;

@Singleton
@Component(modules = {
        AndroidInjectionModule.class,
        SakaiApplicationModule.class,
        ViewModelModule.class
})
public interface SakaiApplicationComponent extends AndroidInjector<SakaiApplication> {

    @Component.Builder
    interface Builder {
        @BindsInstance Builder applicationContext(Context context);
        SakaiApplicationComponent build();
    }

}
