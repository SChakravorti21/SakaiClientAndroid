package com.sakaimobile.development.sakaiclient20.persistence.entities;

import com.sakaimobile.development.sakaiclient20.models.Term;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Created by Development on 8/5/18.
 */

@Entity(tableName = "assignments",
        foreignKeys = @ForeignKey(entity = Course.class,
                parentColumns = "siteId",
                childColumns = "siteId",
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE),
        indices = {
                @Index(value = "siteId"),
                @Index(value = "assignmentId")
        })
public class Assignment implements Serializable {

    @NonNull
    @PrimaryKey
    public final String assignmentId;

    // Key assignment details
    public Term term;
    public String title;
    public String siteId;
    public String instructions;

    // Information that allows Sakai to keep track of the assignment
    public String entityURL;
    public String entityTitle;
    public String entityReference;
    public String assignmentSitePageUrl;

    // Information regarding the submission of assignment
    public String status;
    public Date dueTime;
    public String dueTimeString;
    public boolean allowResubmission;

    // Information about who created the assignment
    public String creator;
    public String authorLastModified;

    // Information regarding the grading scale
    public String gradeScale;
    public String gradeScaleMaxPoints;

    @Ignore
    public List<Attachment> attachments = new ArrayList<>();

    public Assignment(String assignmentId) {
        this.assignmentId = assignmentId;
    }

}
