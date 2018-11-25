package com.example.development.sakaiclient20.ui.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.example.development.sakaiclient20.persistence.entities.Course;
import com.example.development.sakaiclient20.repositories.CourseRepository;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CourseViewModel extends BaseViewModel {

    private MutableLiveData<Course> singleCourse;

    public CourseViewModel(CourseRepository repo) {
        super(repo);
    }

    @Override
    public void refreshData() {
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

    public LiveData<Course> getCourse(String siteId) {
        // (Re-)initialize the LiveData object so that it does not
        // contain stale data
        singleCourse = new MutableLiveData<>();
        refreshCourse(siteId);
        return singleCourse;
    }

    private void refreshCourse(String siteId) {
        this.compositeDisposable.add(
            this.courseRepository.getCourse(siteId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    singleCourse::setValue,
                    Throwable::printStackTrace
                )
        );
    }
}
