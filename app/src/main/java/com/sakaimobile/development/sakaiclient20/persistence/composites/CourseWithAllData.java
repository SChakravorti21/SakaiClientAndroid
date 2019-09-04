package com.sakaimobile.development.sakaiclient20.persistence.composites;

import com.sakaimobile.development.sakaiclient20.persistence.entities.Announcement;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Assignment;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Course;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Grade;
import com.sakaimobile.development.sakaiclient20.persistence.entities.SitePage;

import java.util.List;

import androidx.room.Embedded;
import androidx.room.Relation;

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
