package com.sakaimobile.development.sakaiclient20.ui.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.sakaimobile.development.sakaiclient20.persistence.entities.Course;
import com.sakaimobile.development.sakaiclient20.repositories.CourseRepository;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

abstract class BaseViewModel extends ViewModel {
    CourseRepository courseRepository;
    CompositeDisposable compositeDisposable;
    private MutableLiveData<List<List<Course>>> coursesByTerm;

    BaseViewModel(CourseRepository repo) {
        this.courseRepository = repo;
        this.compositeDisposable = new CompositeDisposable();
    }

    abstract void refreshAllData();
    abstract void refreshSiteData(String siteId);

    public LiveData<List<List<Course>>> getCoursesByTerm(boolean refresh) {
        if(this.coursesByTerm == null) {
            this.coursesByTerm = new MutableLiveData<>();
            loadCourses();
        }

        if(refresh) {
            refreshAllData();
        }

        return this.coursesByTerm;
    }

    private void loadCourses() {
        this.compositeDisposable.add(
            this.courseRepository.getCoursesSortedByTerm()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this.coursesByTerm::setValue,
                        Throwable::printStackTrace
                )
        );
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
