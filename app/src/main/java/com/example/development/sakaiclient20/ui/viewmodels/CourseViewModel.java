package com.example.development.sakaiclient20.ui.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.example.development.sakaiclient20.persistence.entities.Course;
import com.example.development.sakaiclient20.repositories.CourseRepository;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CourseViewModel extends BaseViewModel {

    public CourseViewModel(CourseRepository repo) {
        super(repo);
    }

    public LiveData<List<List<Course>>> getCoursesByTerm() {
        if(this.coursesByTerm == null) {
            this.coursesByTerm = new MutableLiveData<>();
            refreshData();
        }
        return this.coursesByTerm;
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
}
