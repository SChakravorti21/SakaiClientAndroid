package com.sakaimobile.development.sakaiclient20.ui.viewmodels;

import android.annotation.SuppressLint;

import com.sakaimobile.development.sakaiclient20.repositories.CourseRepository;

import javax.inject.Inject;

import io.reactivex.schedulers.Schedulers;
import kotlin.NotImplementedError;

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
        throw new NotImplementedError("Do not need to implement refreshing single course");
    }


}
