package com.example.development.sakaiclient20.persistence.composites;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import com.example.development.sakaiclient20.persistence.entities.Assignment;
import com.example.development.sakaiclient20.persistence.entities.Attachment;

import java.util.List;

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
