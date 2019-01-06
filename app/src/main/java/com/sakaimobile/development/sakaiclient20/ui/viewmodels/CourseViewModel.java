package com.sakaimobile.development.sakaiclient20.ui.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.sakaimobile.development.sakaiclient20.persistence.entities.Course;
import com.sakaimobile.development.sakaiclient20.repositories.CourseRepository;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CourseViewModel extends BaseViewModel {

    private Map<String, MutableLiveData<Course>> siteIdToCourse;

    @Inject
    public CourseViewModel(CourseRepository repo) {
        super(repo);
        siteIdToCourse = new HashMap<>();
    }

    @Override
    public void refreshAllData() {
        this.courseRepository.refreshAllCourses();
    }

    @Override
    public void refreshSiteData(String siteId) {
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

    public LiveData<Course> getCourse(String siteId) {
        // (Re-)initialize the LiveData object so that it does not
        // contain stale data
        if(!siteIdToCourse.containsKey(siteId)) {
            siteIdToCourse.put(siteId, new MutableLiveData<>());
            // Load course from DB (no need for API call since `getCourse()`
            // could not be called unless all courses were already loaded into
            // the database).
            loadCourse(siteId);
        }
        return siteIdToCourse.get(siteId);
    }

    void loadCourse(String siteId) {
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


}
