package com.example.development.sakaiclient20.ui.listeners;

import com.example.development.sakaiclient20.persistence.entities.Announcement;
import com.example.development.sakaiclient20.persistence.entities.Course;

import java.util.Map;

public interface OnActionPerformedListener {
    void onCourseSelected(String siteId);
    void onAnnouncementSelected(Announcement announcement, Map<String,Course> siteIdToCourse);
}
