package com.sakaimobile.development.sakaiclient20.persistence.composites;

import com.sakaimobile.development.sakaiclient20.persistence.entities.Assignment;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Attachment;

import java.util.List;

import androidx.room.Embedded;
import androidx.room.Relation;

/**
 * Created by Development on 8/5/18.
 */

public class AssignmentWithAttachments {
    @Embedded
    public final Assignment assignment;

    @Relation(parentColumn = "assignmentId", entityColumn = "assignmentId")
    public List<Attachment> attachments;

    public AssignmentWithAttachments(Assignment assignment) {
        this.assignment = assignment;
    }

}
