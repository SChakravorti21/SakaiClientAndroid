package com.sakaimobile.development.sakaiclient20.ui.viewmodels;

import com.sakaimobile.development.sakaiclient20.repositories.AssignmentRepository;
import com.sakaimobile.development.sakaiclient20.repositories.CourseRepository;

import org.apache.commons.lang3.NotImplementedException;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AssignmentViewModel extends BaseViewModel {

    private AssignmentRepository assignmentRepository;

    @Inject
    public AssignmentViewModel(
            CourseRepository courseRepository,
            AssignmentRepository assignmentRepository
    ) {
        super(courseRepository);
        this.assignmentRepository = assignmentRepository;
    }

    @Override
    public void refreshAllData() {
        this.assignmentRepository.refreshAllAssignments()
                .doOnSubscribe(compositeDisposable::add)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    @Override
    public void refreshSiteData(String siteId) {
        this.compositeDisposable.add(
            this.assignmentRepository
                .refreshSiteAssignments(siteId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    (assignments) -> {
                        throw new NotImplementedException("Need to implement refreshing site assignments");
                    },
                    Throwable::printStackTrace
                )
        );
    }
}
