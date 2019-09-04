package com.sakaimobile.development.sakaiclient20.repositories;

import com.sakaimobile.development.sakaiclient20.models.sakai.assignments.AssignmentsResponse;
import com.sakaimobile.development.sakaiclient20.networking.services.AssignmentsService;
import com.sakaimobile.development.sakaiclient20.persistence.access.AssignmentDao;
import com.sakaimobile.development.sakaiclient20.persistence.access.AttachmentDao;
import com.sakaimobile.development.sakaiclient20.persistence.access.CourseDao;
import com.sakaimobile.development.sakaiclient20.persistence.composites.AssignmentWithAttachments;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Assignment;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Attachment;
import com.sakaimobile.development.sakaiclient20.ui.helpers.AssignmentSortingUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
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
                // `debounce` because multiple inserts will trigger multiple
                // emissions in quick succession
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
        // we request assignments for each course individually
        return courseDao.getAllSiteIds()
            .toObservable()
            .switchMapSingle(this::refreshMultipleSiteAssignments)
            .ignoreElements();
    }

    public Single<List<List<Assignment>>> refreshMultipleSiteAssignments(List<String> siteIds) {
        // We construct requests to update assignments for each site (i.e. course),
        // and run them in parallel. This reduces the request/persistence time significantly
        // (slashes it in half).
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
                });
    }

    private Single<List<Assignment>> refreshSiteAssignments(String siteId) {
        return assignmentsService
                .getSiteAssignments(siteId)
                .map(AssignmentsResponse::getAssignments);
    }

    private void persistAssignments(List<Assignment> assignments) {
        // If there is nothing to insert, we do not want to trigger
        // a false database `Flowable` emission
        if(assignments.isEmpty()) return;

        List<Attachment> allAttachments = new ArrayList<>();
        for(Assignment assignment : assignments)
            allAttachments.addAll(assignment.attachments);

        // Insert all assignments and their attachments into the database
        assignmentDao.upsert(assignments);
        attachmentDao.upsert(allAttachments);
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
