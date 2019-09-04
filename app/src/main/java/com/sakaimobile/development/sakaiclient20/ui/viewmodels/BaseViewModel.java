package com.sakaimobile.development.sakaiclient20.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sakaimobile.development.sakaiclient20.networking.utilities.HeaderInterceptor;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Course;
import com.sakaimobile.development.sakaiclient20.repositories.CourseRepository;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public abstract class BaseViewModel extends ViewModel {

    CourseRepository courseRepository;
    CompositeDisposable compositeDisposable;
    private MutableLiveData<List<List<Course>>> coursesByTerm;
    private MutableLiveData<SakaiErrorState> errorState;

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
        SakaiErrorState error =
                throwable.getMessage().equals(HeaderInterceptor.SESSION_EXPIRED_ERROR)
                        ? SakaiErrorState.SESSION_EXPIRED
                        : SakaiErrorState.FAILURE;
        this.errorState.postValue(error);
    }

    public LiveData<List<List<Course>>> getCoursesByTerm() {
        if(this.coursesByTerm == null) {
            this.coursesByTerm = new MutableLiveData<>();
            loadCourses();
        }

        return this.coursesByTerm;
    }

    LiveData<List<Course>> getUnsortedCourses() {
        MutableLiveData<List<Course>> allCourses = new MutableLiveData<>();
        this.compositeDisposable.add(
            this.courseRepository.getAllCourses()
                .subscribeOn(Schedulers.io())
                .subscribe(
                        allCourses::postValue,
                        Throwable::printStackTrace
                )
        );

        return allCourses;
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
