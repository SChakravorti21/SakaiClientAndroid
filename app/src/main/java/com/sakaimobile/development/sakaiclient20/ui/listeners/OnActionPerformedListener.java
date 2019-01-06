package com.sakaimobile.development.sakaiclient20.ui.listeners;

public interface OnActionPerformedListener {
    void onCourseSelected(String siteId);

    void loadCoursesFragment(boolean refresh);
    void loadAssignmentsFragment(boolean sortByCourses, boolean refresh);
}
