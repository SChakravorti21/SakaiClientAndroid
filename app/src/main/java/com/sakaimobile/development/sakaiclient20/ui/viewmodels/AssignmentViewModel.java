package com.sakaimobile.development.sakaiclient20.ui.viewmodels;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.sakaimobile.development.sakaiclient20.persistence.entities.Assignment;
import com.sakaimobile.development.sakaiclient20.repositories.AssignmentRepository;
import com.sakaimobile.development.sakaiclient20.repositories.CourseRepository;

import org.apache.commons.lang3.NotImplementedException;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AssignmentViewModel extends BaseViewModel {

    private AssignmentRepository assignmentRepository;
    private MutableLiveData<List<Assignment>> siteAssignments;

    @Inject
    public AssignmentViewModel(
            CourseRepository courseRepository,
            AssignmentRepository assignmentRepository
    ) {
        super(courseRepository);
        this.assignmentRepository = assignmentRepository;
        this.siteAssignments = new MutableLiveData<>();
    }

    @Override
    public void refreshAllData() {
        this.assignmentRepository.refreshAllAssignments()
                .doOnSubscribe(compositeDisposable::add)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    // The result of `subscribe` is used in doOnSubscribe
    @SuppressLint("CheckResult")
    @Override
    public void refreshSiteData(String siteId) {
        this.refreshSiteData(Collections.singletonList(siteId));
    }

    @SuppressLint("CheckResult")
    public void refreshSiteData(List<String> siteIds) {
        this.assignmentRepository
                .refreshMultipleSiteAssignments(siteIds)
                .doOnSubscribe(compositeDisposable::add)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        assignments -> {
                            if(assignments.isEmpty())
                                this.siteAssignments.setValue(null);
                        }
                );
    }

    @SuppressLint("CheckResult")
    public LiveData<List<Assignment>> getSiteAssignments(List<String> siteIds) {
        this.compositeDisposable.add(
            this.assignmentRepository
                .getSiteAssignments(siteIds)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        assignments -> {
                            if(assignments.isEmpty())
                                this.refreshSiteData(siteIds);
                            else
                                this.siteAssignments.setValue(assignments);
                        },
                        Throwable::printStackTrace
                )
        );

        return this.siteAssignments;
    }
}
