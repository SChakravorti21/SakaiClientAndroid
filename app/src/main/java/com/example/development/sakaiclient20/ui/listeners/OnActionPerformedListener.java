package com.example.development.sakaiclient20.ui.listeners;

import com.example.development.sakaiclient20.persistence.entities.Course;

public interface OnActionPerformedListener {
    void onCourseSelected(String siteId);

    void onSiteGradesSelected(Course course);
}
