package com.sakaimobile.development.sakaiclient20.ui.viewmodels;

import android.annotation.SuppressLint;

import com.sakaimobile.development.sakaiclient20.repositories.CourseRepository;

import org.apache.commons.lang3.NotImplementedException;

import javax.inject.Inject;

import io.reactivex.schedulers.Schedulers;

public class CourseViewModel extends BaseViewModel {

    @Inject
    public CourseViewModel(CourseRepository repo) {
        super(repo);
    }

    @SuppressLint("CheckResult")
    @Override
    public void refreshAllData() {
        this.courseRepository.refreshAllCourses()
                .doOnSubscribe(compositeDisposable::add)
                .doOnError(this::emitError)
                .subscribeOn(Schedulers.io())
                .subscribe(() -> {}, Throwable::printStackTrace);
    }

    @Override
    public void refreshSiteData(String siteId) {
        throw new NotImplementedException("Do not need to implement refreshing single course");
    }


}
