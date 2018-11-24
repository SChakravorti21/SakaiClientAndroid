package com.example.development.sakaiclient20.ui.viewmodels;

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
    MutableLiveData<List<List<Course>>> coursesByTerm;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    BaseViewModel(CourseRepository repo) {
        this.courseRepository = repo;
    }

    abstract void refreshData();

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
