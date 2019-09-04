package com.sakaimobile.development.sakaiclient20.ui.viewmodels;

import android.annotation.SuppressLint;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sakaimobile.development.sakaiclient20.persistence.entities.Assignment;
import com.sakaimobile.development.sakaiclient20.repositories.AssignmentRepository;
import com.sakaimobile.development.sakaiclient20.repositories.CourseRepository;

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
            AssignmentRepository assignmentRepository) {
        super(courseRepository);
        this.assignmentRepository = assignmentRepository;
        this.siteAssignments = new MutableLiveData<>();
    }

    @SuppressLint("CheckResult")
    @Override
    public void refreshAllData() {
        this.assignmentRepository.refreshAllAssignments()
                .doOnSubscribe(compositeDisposable::add)
                .doOnError(this::emitError)
                .subscribeOn(Schedulers.io())
                .subscribe(() -> {}, Throwable::printStackTrace);
    }

    /**
     * Can be used to refresh assignments for a single site, but
     * <c>refreshSiteData(List<String> siteIds)</c> should be used
     * in favor of this since it supports refreshing multiple sits' assignments.
     */
    @SuppressLint("CheckResult")
    @Deprecated
    @Override
    public void refreshSiteData(String siteId) {
        this.refreshSiteData(Collections.singletonList(siteId));
    }

    /**
     * Refreshes the assignments for multiple sites,
     * used in {@see SiteAssignmentFragment} for refreshing course/term assignments.
     */
    @SuppressLint("CheckResult")
    public void refreshSiteData(List<String> siteIds) {
        this.assignmentRepository
                .refreshMultipleSiteAssignments(siteIds)
                .doOnSubscribe(compositeDisposable::add)
                .doOnError(this::emitError)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        assignments -> {
                            // If no assignments were received in API call,
                            // set `null` to indicate no assignments were found
                            if(assignments.isEmpty() || containsEmptyLists(assignments))
                                this.siteAssignments.setValue(null);
                        }, Throwable::printStackTrace
                );
    }

    /**
     * Returns a <c>LiveData</c> for observing on assignments from either a single
     * or multiple courses (just pass in the site IDs for which you want to observe
     * assignments). Used in {@see SiteAssignmentsFragment}
     */
    @SuppressLint("CheckResult")
    public LiveData<List<Assignment>> getSiteAssignments(List<String> siteIds) {
        this.compositeDisposable.add(
            this.assignmentRepository
                .getSiteAssignments(siteIds)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        assignments -> {
                            // If no assignments are found, try refreshing
                            // Potentially nothing exists in DB on app install
                            if(assignments.isEmpty())
                                this.refreshSiteData(siteIds);
                            else
                                this.siteAssignments.setValue(assignments);
                        }, Throwable::printStackTrace
                )
        );

        return this.siteAssignments;
    }

    /**
     * Used to check if refreshing site grades returns nothing
     * ({@see AssignmentRepository#refreshMultipleSiteAssignments} returns a
     * list of list of assignments since multiple sites might be refreshed
     * and not just one)
     * @return Whether all inner lists of <c>assignments</c> are empty.
     */
    private boolean containsEmptyLists(List<List<Assignment>> assignments) {
        for(List<Assignment> siteAssignments : assignments)
            if(!siteAssignments.isEmpty())
                return false;

        return true;
    }
}
