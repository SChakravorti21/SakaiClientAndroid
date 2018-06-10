package com.example.development.sakaiclientandroid.utils.requests;

import com.example.development.sakaiclientandroid.api_models.assignments.AllAssignments;
import com.example.development.sakaiclientandroid.models.Course;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by Development on 5/21/18.
 */

public class RequestCallback {

    public void onCoursesSuccess() { }

    public void onCoursesFailure(Throwable throwable) { }

    public void onSiteGradesSuccess() { }

    public void onSiteGradesFailure(Throwable throwable) { }

    public void onAllGradesSuccess() { }

    public void onAllGradesFailure(Throwable throwable) { }

    public void onAllAssignmentsSuccess(ArrayList<ArrayList<Course>> response) { }

    public void onAllAssignmentsFailure(Throwable throwable) { }
}
