package com.sakaimobile.development.sakaiclient20.ui.viewmodels;

import android.annotation.SuppressLint;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sakaimobile.development.sakaiclient20.persistence.entities.Grade;
import com.sakaimobile.development.sakaiclient20.repositories.CourseRepository;
import com.sakaimobile.development.sakaiclient20.repositories.GradeRepository;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class GradeViewModel extends BaseViewModel {

    private GradeRepository gradeRepository;
    private MutableLiveData<List<Grade>> siteGrades;

    /**
     * Grades view model constructor
     *
     * @param courseRepository course repository dependency needed for superclass
     * @param gradeRepository grades repository dependency needed to refresh and get grades
     */
    @Inject
    GradeViewModel(CourseRepository courseRepository, GradeRepository gradeRepository) {
        super(courseRepository);
        this.gradeRepository = gradeRepository;
        this.siteGrades = new MutableLiveData<>();
    }

    /**
     * Called by UI controller to get grades for a site
     * if hashmap already has the grades, return that, otherwise
     * refresh the grades and put into hashmap
     *
     * @param siteId site to get grades for
     * @return live data containing grades list
     */
    public LiveData<List<Grade>> getSiteGrades(String siteId) {
        loadSiteGrades(siteId);
        return this.siteGrades;
    }

    /**
     * Refreshes all grades by telling the grades repository to make
     * a network request and then persist them in the database
     * <p>
     * Then it calls load courses (now that the new grades are in the database)
     */
    @SuppressLint("CheckResult")
    @Override
    public void refreshAllData() {
        this.gradeRepository.refreshAllGrades()
                .doOnSubscribe(compositeDisposable::add)
                .doOnError(this::emitError)
                .subscribeOn(Schedulers.io())
                .subscribe(() -> {}, Throwable::printStackTrace);
    }

    /**
     * Loads grades for a site from the grades repository into
     * a hashmap mapping from the siteId to the grades list
     *
     * @param siteId site to load the grades for
     */
    private void loadSiteGrades(String siteId) {
        this.compositeDisposable.add(
            this.gradeRepository.getGradesForSite(siteId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    grades -> {
                        if(grades.isEmpty())
                            this.refreshSiteData(siteId);
                        else
                            this.siteGrades.setValue(grades);
                    }, Throwable::printStackTrace
                )
        );
    }

    /**
     * Refreshes the grades for a given site
     * <p>
     * Loads the site grades given that the grades for that site are updated in the database
     *
     * @param siteId
     */
    @SuppressLint("CheckResult")
    @Override
    public void refreshSiteData(String siteId) {
        this.gradeRepository.refreshSiteGrades(siteId)
            .doOnSubscribe(compositeDisposable::add)
            .doOnError(this::emitError)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                grades -> {
                    if(grades.isEmpty())
                        this.siteGrades.setValue(null);
                }, Throwable::printStackTrace
            );
    }


}
