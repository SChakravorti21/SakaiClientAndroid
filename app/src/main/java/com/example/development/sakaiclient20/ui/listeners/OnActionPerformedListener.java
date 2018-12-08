package com.example.development.sakaiclient20.ui.listeners;

import com.example.development.sakaiclient20.persistence.entities.Announcement;

public interface OnActionPerformedListener {
    void onCourseSelected(String siteId);
    void onAnnouncementSelected(Announcement announcement);
}
