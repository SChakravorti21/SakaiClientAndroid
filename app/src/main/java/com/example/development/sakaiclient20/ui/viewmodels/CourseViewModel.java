package com.example.development.sakaiclient20.ui.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.example.development.sakaiclient20.persistence.entities.Course;
import com.example.development.sakaiclient20.repositories.CourseRepository;

import org.apache.commons.lang3.NotImplementedException;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CourseViewModel extends BaseViewModel {

    public CourseViewModel(CourseRepository repo) {
        super(repo);
    }

    @Override
    public void refreshAllData() {
      this.compositeDisposable.add(
            this.courseRepository.refreshAllCourses()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                this::loadCourses,
                Throwable::printStackTrace
            )
        );
    }

    @Override
    public void refreshSiteData(String siteId) {
        throw new NotImplementedException("Add functionality to refresh site data");
    }
}
