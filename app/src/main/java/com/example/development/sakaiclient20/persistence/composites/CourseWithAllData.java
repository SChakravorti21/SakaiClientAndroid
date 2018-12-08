package com.example.development.sakaiclient20.persistence.composites;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import com.example.development.sakaiclient20.persistence.entities.Announcement;
import com.example.development.sakaiclient20.persistence.entities.Assignment;
import com.example.development.sakaiclient20.persistence.entities.Course;
import com.example.development.sakaiclient20.persistence.entities.Grade;
import com.example.development.sakaiclient20.persistence.entities.SitePage;

import java.util.List;

/**
 * Created by Development on 8/11/18.
 */

public class CourseWithAllData {
    @Embedded
    public final Course course;

    @Relation(parentColumn = "siteId", entityColumn = "siteId")
    public List<Grade> grades;

    @Relation(parentColumn = "siteId", entityColumn = "siteId")
    public List<SitePage> sitePages;

    @Relation(parentColumn = "siteId", entityColumn = "siteId", entity = Assignment.class)
    public List<AssignmentWithAttachments> assignments;

    @Relation(parentColumn = "siteId", entityColumn = "siteId", entity = Announcement.class)
    public List<AnnouncementWithAttachments> announcements;

    public CourseWithAllData(Course course) {
        this.course = course;
    }
}
