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

public class CourseViewModel extends ViewModel {
    private CourseRepository courseRepository;
    private MutableLiveData<List<List<Course>>> coursesByTerm;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public CourseViewModel(CourseRepository repo) {
        this.courseRepository = repo;
    }

    public LiveData<List<List<Course>>> getCoursesByTerm() {
        if(coursesByTerm == null) {
            coursesByTerm = new MutableLiveData<>();
            refreshData();
        }
        return coursesByTerm;
    }

    public void refreshData() {
        compositeDisposable.add(
            courseRepository.refreshAllCourses()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                this::loadData,
                Throwable::printStackTrace
            )
        );
    }

    private void loadData() {
        compositeDisposable.add(
            courseRepository.getCoursesSortedByTerm()
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
