package com.example.development.sakaiclient20.ui.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.example.development.sakaiclient20.persistence.entities.Grade;
import com.example.development.sakaiclient20.repositories.CourseRepository;
import com.example.development.sakaiclient20.repositories.GradesRepository;

import java.util.HashMap;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class GradeViewModel extends BaseViewModel {

    private GradesRepository gradesRepository;
    private HashMap<String, MutableLiveData<List<Grade>>> siteIdToGrades;

    public GradeViewModel(CourseRepository courseRepository, GradesRepository gradesRepository) {
        super(courseRepository);
        this.gradesRepository = gradesRepository;
    }

    /**
     * Called by UI controller to get grades for a site
     *
     * if hashmap already has the grades, return that, otherwise
     * refresh the grades and put into hashmap
     *
     * @param siteId site to get grades for
     * @return live data containing grades list
     */
    public LiveData<List<Grade>> getGradesForSite(String siteId) {

        if(!this.siteIdToGrades.containsKey(siteId)) {
            this.siteIdToGrades.put(siteId, new MutableLiveData<>());
            refreshSiteGrades(siteId);
        }
        return this.siteIdToGrades.get(siteId);
    }


    @Override
    void refreshData() {
        this.compositeDisposable.add(
                this.gradesRepository.refreshAllGrades()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::loadCourses,
                        Throwable::printStackTrace
                )
        );
    }

    /**
     * Loads grades for a site from the grades repository into
     * a hashmap mapping from the siteId to the grades list
     *
     * @param siteId site to load the grades for
     */
    public void loadSiteGrades(String siteId) {
        this.compositeDisposable.add(
                this.gradesRepository.getGradesForSite(siteId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this.siteIdToGrades.get(siteId)::setValue,
                        Throwable::printStackTrace
                )
        );
    }

    /**
     * Refreshes the grades for a given site
     *
     * @param siteId
     */
    public void refreshSiteGrades(String siteId) {
        this.compositeDisposable.add(
                this.gradesRepository.refreshSiteGrades(siteId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> loadSiteGrades(siteId),
                        Throwable::printStackTrace
                )
        );
    }

}
