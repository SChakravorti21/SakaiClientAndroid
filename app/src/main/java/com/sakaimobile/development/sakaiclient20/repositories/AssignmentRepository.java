package com.sakaimobile.development.sakaiclient20.repositories;

import com.sakaimobile.development.sakaiclient20.models.sakai.assignments.AssignmentsResponse;
import com.sakaimobile.development.sakaiclient20.networking.services.AssignmentsService;
import com.sakaimobile.development.sakaiclient20.persistence.access.AssignmentDao;
import com.sakaimobile.development.sakaiclient20.persistence.access.AttachmentDao;
import com.sakaimobile.development.sakaiclient20.persistence.access.CourseDao;
import com.sakaimobile.development.sakaiclient20.persistence.composites.AssignmentWithAttachments;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Assignment;
import com.sakaimobile.development.sakaiclient20.ui.helpers.AssignmentSortingUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Development on 8/8/18.
 */

public class AssignmentRepository {

    private CourseDao courseDao;
    private AssignmentDao assignmentDao;
    private AttachmentDao attachmentDao;
    private AssignmentsService assignmentsService;

    @Inject
    public AssignmentRepository(
            CourseDao courseDao,
            AssignmentDao assignmentDao,
            AttachmentDao attachmentDao,
            AssignmentsService service
    ) {
        this.courseDao = courseDao;
        this.assignmentDao = assignmentDao;
        this.attachmentDao = attachmentDao;
        this.assignmentsService = service;
    }

    public Flowable<List<Assignment>> getSiteAssignments(List<String> siteIds) {
        return assignmentDao.getSiteAssignments(siteIds)
                .debounce(100, TimeUnit.MILLISECONDS)
                .map(AssignmentRepository::flattenCompositesToEntities)
                .map(assignments -> {
                    AssignmentSortingUtils.sortAssignmentsByDate(assignments);
                    return assignments;
                });
    }

    public Completable refreshAllAssignments() {
        // We need to update all assignments, but requesting directly
        // from the endpoint for all assignments is too slow. Instead,
        // we construct requests to update assignments for each site (i.e. course),
        // and run them in parallel. This reduces the request/persistence time significantly
        // (slashes it in half).
        return courseDao.getAllSiteIds()
            .toObservable()
            .switchMapCompletable(this::refreshMultipleSiteAssignments);
    }

    public Completable refreshMultipleSiteAssignments(List<String> siteIds) {
        return Observable.fromIterable(siteIds)
                .map(this::refreshSiteAssignments)
                // Observe each network call on its own thread, running them in parallel
                .flatMap(task -> task.toObservable().subscribeOn(Schedulers.newThread()))
                // Wait for ALL calls to complete
                .toList()
                // Make sure to persist all assignments so they get picked up by @Relation
                .map(allAssignments -> {
                    for(List<Assignment> courseAssignments : allAssignments)
                        this.persistAssignments(courseAssignments);
                    return allAssignments;
                })
                .ignoreElement();
    }

    private Single<List<Assignment>> refreshSiteAssignments(String siteId) {
        return assignmentsService
                .getSiteAssignments(siteId)
                .map(AssignmentsResponse::getAssignments);
    }

    private List<Assignment> persistAssignments(List<Assignment> assignments) {
        // Insert all assignments and their attachments into the database
        assignmentDao.insert(assignments);
        for(Assignment assignment : assignments)
            attachmentDao.insert(assignment.attachments);

        return assignments;
    }

    static List<Assignment> flattenCompositesToEntities(List<AssignmentWithAttachments> assignmentComposites) {
        List<Assignment> assignmentEntities = new ArrayList<>(assignmentComposites.size());

        for(AssignmentWithAttachments composite : assignmentComposites) {
            Assignment assignment = composite.assignment;
            assignment.attachments = composite.attachments;
            assignmentEntities.add(assignment);
        }

        return assignmentEntities;
    }

}
