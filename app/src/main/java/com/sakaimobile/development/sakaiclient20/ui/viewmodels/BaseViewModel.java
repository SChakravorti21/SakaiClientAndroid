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

    protected enum SakaiErrorState {
        NO_ERROR,
        FAILURE
    }

    CourseRepository courseRepository;
    CompositeDisposable compositeDisposable;
    private MutableLiveData<List<List<Course>>> coursesByTerm;
    protected MutableLiveData<SakaiErrorState> errorState;

    BaseViewModel() {
        this.errorState = new MutableLiveData<>();
        this.compositeDisposable = new CompositeDisposable();
    }

    BaseViewModel(CourseRepository repo) {
        this();
        this.courseRepository = repo;
    }

    abstract void refreshAllData();
    abstract void refreshSiteData(String siteId);

    public LiveData<SakaiErrorState> getErrorState() {
        return errorState;
    }

    void emitError(Throwable throwable) {
        this.errorState.setValue(SakaiErrorState.FAILURE);
    }

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
                .subscribe(
                        this.coursesByTerm::postValue,
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
