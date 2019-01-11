package com.sakaimobile.development.sakaiclient20.ui.viewmodels;

import com.sakaimobile.development.sakaiclient20.repositories.CourseRepository;

import org.apache.commons.lang3.NotImplementedException;

import javax.inject.Inject;

import io.reactivex.schedulers.Schedulers;

public class CourseViewModel extends BaseViewModel {

    @Inject
    public CourseViewModel(CourseRepository repo) {
        super(repo);
    }

    @Override
    public void refreshAllData() {
        this.courseRepository.refreshAllCourses()
                .doOnSubscribe(compositeDisposable::add)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    @Override
    public void refreshSiteData(String siteId) {
        throw new NotImplementedException("Do not need to implement refreshing single course");
    }


}
