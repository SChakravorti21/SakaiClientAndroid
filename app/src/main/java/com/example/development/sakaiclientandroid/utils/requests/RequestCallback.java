package com.example.development.sakaiclientandroid.utils.requests;

import com.example.development.sakaiclientandroid.api_models.assignments.Assignment;
import com.example.development.sakaiclientandroid.models.Course;

import java.util.ArrayList;

/**
 * Created by Development on 5/21/18.
 */

public class RequestCallback {

    public void onRequestFailure(int msgRscId, Throwable t) { }

    public void onAllCoursesSuccess(ArrayList<ArrayList<Course>> response) {
    }

    public void onAllCoursesFailure(Throwable throwable) {
    }

    public void onSiteGradesSuccess(Course course) { }

    public void onSiteGradesEmpty(int msgRscId) {
    }

    public void onAllGradesSuccess(ArrayList<ArrayList<Course>> response) {
    }

    public void onAllAssignmentsByCourseSuccess(ArrayList<ArrayList<Course>> response) { }

    public void onAllAssignmentsByDateSuccess(ArrayList<ArrayList<Assignment>> response) { }

    public void onSiteAssignmentsSuccess(ArrayList<Assignment> response) { }

    public void onSiteAssignmentsFailure(Throwable throwable) { }
    public void onAllGradesFailure(Throwable throwable) {
    }

    public void onAllAssignmentsSuccess(ArrayList<ArrayList<Course>> response) {
    }

    public void onAllAssignmentsFailure(Throwable throwable) {
    }
}
