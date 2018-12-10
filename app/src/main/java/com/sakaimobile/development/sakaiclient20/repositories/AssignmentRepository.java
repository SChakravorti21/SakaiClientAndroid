package com.sakaimobile.development.sakaiclient20.repositories;

import com.sakaimobile.development.sakaiclient20.models.sakai.assignments.AssignmentsResponse;
import com.sakaimobile.development.sakaiclient20.networking.services.AssignmentsService;
import com.sakaimobile.development.sakaiclient20.persistence.access.AssignmentDao;
import com.sakaimobile.development.sakaiclient20.persistence.access.AttachmentDao;
import com.sakaimobile.development.sakaiclient20.persistence.composites.AssignmentWithAttachments;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Assignment;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Created by Development on 8/8/18.
 */

public class AssignmentRepository {

    private AssignmentDao assignmentDao;
    private AttachmentDao attachmentDao;
    private AssignmentsService assignmentsService;

    public AssignmentRepository(
            AssignmentDao assignmentDao,
            AttachmentDao attachmentDao,
            AssignmentsService service
    ) {
        this.assignmentDao = assignmentDao;
        this.attachmentDao = attachmentDao;
        this.assignmentsService = service;
    }

    public Single<List<Assignment>> getSiteAssignments(String siteId) {
        return assignmentDao
                .getSiteAssignments(siteId)
                .firstOrError()
                .map(AssignmentRepository::flattenCompositesToEntities);
    }

    public Single<List<Assignment>> getAllAssignments() {
        return assignmentDao
                .getAllAssignments()
                .firstOrError()
                .map(AssignmentRepository::flattenCompositesToEntities);
    }

    public Completable refreshAllAssignments() {
        return assignmentsService
                .getAllAssignments()
                .map(AssignmentsResponse::getAssignments)
                .map(this::persistAssignments)
                .ignoreElement();
    }

    public Completable refreshSiteAssignments(String siteId) {
        return assignmentsService
                .getSiteAssignments(siteId)
                .map(AssignmentsResponse::getAssignments)
                .map(this::persistAssignments)
                .ignoreElement();
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
