package com.example.development.sakaiclient20.ui.listeners;

import com.example.development.sakaiclient20.persistence.entities.Announcement;
import com.example.development.sakaiclient20.persistence.entities.Course;

import java.util.HashMap;

public interface OnActionPerformedListener {
    void onCourseSelected(String siteId);
    void onSiteAnnouncementsSelected(Course course);
    void onAnnouncementSelected(Announcement announcement, HashMap<String,Course> siteIdToCourse);
}
