package com.sakaimobile.development.sakaiclient20.ui.listeners;

import android.view.View;

import com.sakaimobile.development.sakaiclient20.persistence.entities.Announcement;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Course;

import java.util.Map;

public interface OnAnnouncementSelected {

    void onAnnouncementSelected(Announcement announcement, Course course,
                                View cardView, int position);

}
