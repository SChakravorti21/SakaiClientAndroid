package com.example.development.sakaiclient20.ui.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.example.development.sakaiclient20.persistence.entities.Course;
import com.example.development.sakaiclient20.repositories.CourseRepository;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CourseViewModel extends BaseViewModel {

    private Map<String, MutableLiveData<Course>> siteIdToCourse;

    public CourseViewModel(CourseRepository repo) {
        super(repo);
        siteIdToCourse = new HashMap<>();
    }

    public LiveData<Course> getCourse(String siteId) {
        // (Re-)initialize the LiveData object so that it does not
        // contain stale data
        if(!siteIdToCourse.containsKey(siteId)) {
            siteIdToCourse.put(siteId, new MutableLiveData<>());
            refreshSiteData(siteId);
        }
        return siteIdToCourse.get(siteId);
    }

    private void loadCourse(String siteId) {
        this.compositeDisposable.add(
            this.courseRepository.getCourse(siteId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    siteIdToCourse.get(siteId)::setValue,
                    Throwable::printStackTrace
                )
        );
    }

    private void refreshSiteData(String siteId) {
        this.compositeDisposable.add(
            this.courseRepository.refreshCourse(siteId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    () -> this.loadCourse(siteId),
                    Throwable::printStackTrace
                )
        );
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
}
