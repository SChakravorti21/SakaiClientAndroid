package com.example.development.sakaiclientandroid.utils.requests;

import com.example.development.sakaiclientandroid.api_models.assignments.Assignment;
import com.example.development.sakaiclientandroid.models.Course;

import java.util.ArrayList;

/**
 * Created by Development on 5/21/18.
 */

public class RequestCallback {

    public void onCoursesSuccess(ArrayList<ArrayList<Course>> response) { }

    public void onCoursesFailure(Throwable throwable) { }

    public void onSiteGradesSuccess() { }

    public void onSiteGradesFailure(Throwable throwable) { }

    public void onAllGradesSuccess(ArrayList<ArrayList<Course>> response) { }

    public void onAllGradesFailure(Throwable throwable) { }

    public void onAllAssignmentsByCourseSuccess(ArrayList<ArrayList<Course>> response) { }

    public void onAllAssignmentsByDateSuccess(ArrayList<ArrayList<Assignment>> response) { }

    public void onAllAssignmentsFailure(Throwable throwable) { }

    public void onSiteAssignmentsSuccess(ArrayList<Assignment> response) { }

    public void onSiteAssignmentsFailure(Throwable throwable) { }
}
