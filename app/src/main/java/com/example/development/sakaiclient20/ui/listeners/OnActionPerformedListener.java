package com.example.development.sakaiclient20.ui.listeners;

import com.example.development.sakaiclient20.persistence.entities.Announcement;
import com.example.development.sakaiclient20.persistence.entities.Course;

import java.io.Serializable;
import java.util.Map;

public interface OnActionPerformedListener {
    void onCourseSelected(String siteId);
    void onSiteAnnouncementsSelected(Course course);
    void onAnnouncementSelected(Announcement announcement, Map<String,Course> siteIdToCourse);
    void onCourseSelected(String siteId);
    void onSiteGradesSelected(Course course);
}
