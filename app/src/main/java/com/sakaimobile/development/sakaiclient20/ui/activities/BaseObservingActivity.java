package com.sakaimobile.development.sakaiclient20.ui.activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.sakaimobile.development.sakaiclient20.R;
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.AnnouncementViewModel;
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.CourseViewModel;
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.ResourceViewModel;
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.ViewModelFactory;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class BaseObservingActivity extends AppCompatActivity {

    @Inject
    ViewModelFactory viewModelFactory;

    protected Set<LiveData> beingObserved;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidInjection.inject(this);
        beingObserved = new HashSet<>();
    }


    protected ViewModel getViewModel(Class viewModelClass) {
        return ViewModelProviders.of(this, viewModelFactory).get(viewModelClass);
    }


    @Override
    protected void onPause() {
        super.onPause();
        removeObservations();
    }

    protected void removeObservations() {
        for (LiveData liveData : beingObserved) {
            liveData.removeObservers(this);
        }
        beingObserved.clear();
    }



}
