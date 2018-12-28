package com.sakaimobile.development.sakaiclient20.ui.listeners;

import com.sakaimobile.development.sakaiclient20.persistence.entities.Announcement;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Course;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Resource;

import java.util.Map;

public interface OnActionPerformedListener {
    void onCourseSelected(String siteId);

    void loadCoursesFragment(boolean refresh);
    void loadAssignmentsFragment(boolean sortByCourses, boolean refresh);
}
