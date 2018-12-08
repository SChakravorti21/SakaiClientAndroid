package com.example.development.sakaiclient20.ui.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.development.sakaiclient20.persistence.entities.Course;
import com.example.development.sakaiclient20.repositories.CourseRepository;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

abstract class BaseViewModel extends ViewModel {
    CourseRepository courseRepository;
    private MutableLiveData<List<List<Course>>> coursesByTerm;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    BaseViewModel(CourseRepository repo) {
        this.courseRepository = repo;
    }

    // all view models must be able to refresh all data and site data
    abstract void refreshAllData();
    abstract void refreshSiteData(String siteId);

    public LiveData<List<List<Course>>> getCoursesByTerm() {
        if(this.coursesByTerm == null) {
            this.coursesByTerm = new MutableLiveData<>();
            // TODO remove this, since the subclass's refresh all data method may not necessary refresh all courses (any view model other than courseviewmodel lol)
            refreshAllData();
        }
        return this.coursesByTerm;
    }

    void loadCourses() {
        this.compositeDisposable.add(
            this.courseRepository.getCoursesSortedByTerm()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    coursesByTerm::setValue,
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
