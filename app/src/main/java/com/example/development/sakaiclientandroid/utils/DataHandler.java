package com.example.development.sakaiclientandroid.utils;

import android.support.annotation.NonNull;

import com.example.development.sakaiclientandroid.R;

import com.example.development.sakaiclientandroid.api_models.assignments.AllAssignments;
import com.example.development.sakaiclientandroid.api_models.assignments.AssignmentObject;
import com.example.development.sakaiclientandroid.api_models.gradebook.AllGradesPost;
import com.example.development.sakaiclientandroid.api_models.gradebook.GradebookObject;
import com.example.development.sakaiclientandroid.api_models.gradebook.GradebookCollectionObject;
import com.example.development.sakaiclientandroid.models.Course;
import com.example.development.sakaiclientandroid.models.Term;
import com.example.development.sakaiclientandroid.utils.requests.RequestCallback;
import com.example.development.sakaiclientandroid.utils.requests.RequestManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataHandler {


    private static ArrayList<ArrayList<Course>> coursesSortedByTerm;
    private static HashMap<String, Course> mapSiteIdToCourse;

    //needed so that we don't have to make an unnecessary request if all grades
    //have already been requested
    private static boolean hasRequestedAllGrades = false;
    private static boolean hasRequestedAllAssignments = false;


    public static ArrayList<ArrayList<Course>> getCoursesSortedByTerm() {
        return coursesSortedByTerm;
    }

    public static List<GradebookObject> getGradesForCourse(String siteId) {
        return getCourseFromId(siteId).getGradebookObjectList();
    }

    public static boolean gradesRequestedForSite(String siteId) {
        return getCourseFromId(siteId).getGradebookObjectList() != null;
    }


    public static Term getTermFromId(String id) {
        return mapSiteIdToCourse.get(id).getTerm();
    }

    public static String getTitleFromId(String id) {
        return mapSiteIdToCourse.get(id).getTitle();
    }


    public static void requestAllAssignments(final RequestCallback UICallback) {
        if (hasRequestedAllAssignments) {
            UICallback.onAllAssignmentsSuccess(coursesSortedByTerm);
            return;
        }

        RequestManager.fetchAllAssignments(new Callback<AllAssignments>() {
            @Override
            public void onResponse(@NonNull Call<AllAssignments> call,
                                   @NonNull Response<AllAssignments> response) {
                AllAssignments allAssignments = response.body();

                if (allAssignments == null || allAssignments.getAssignmentObject().size() == 0) {
                    UICallback.onAllAssignmentsFailure(new Throwable("Assignments is empty!"));
                    return;
                }

                for (AssignmentObject assignment : allAssignments.getAssignmentObject()) {
                    Course course = mapSiteIdToCourse.get(assignment.getContext());
                    course.addAssignment(assignment);
                }

                hasRequestedAllAssignments = true;
                UICallback.onAllAssignmentsSuccess(coursesSortedByTerm);
            }

            @Override
            public void onFailure(@NonNull Call<AllAssignments> call, @NonNull Throwable t) {
                UICallback.onAllAssignmentsFailure(t);
            }
        });
    }


    /**
     * Requests all the grades for all the courses
     * @param refreshGrades whether or not we want to refresh all grades
     * @param UICallback callback to execute after request is done
     */
    public static void requestAllGrades(boolean refreshGrades, final RequestCallback UICallback) {

        if (hasRequestedAllGrades && !refreshGrades) {
            UICallback.onAllGradesSuccess(coursesSortedByTerm);
            return;
        }

        RequestManager.fetchAllGrades(new Callback<AllGradesPost>() {
            @Override
            public void onResponse(@NonNull Call<AllGradesPost> call, @NonNull Response<AllGradesPost> response) {

                AllGradesPost allGradesPost = response.body();

                if (allGradesPost != null && allGradesPost.getGradebookCollection() != null && allGradesPost.getGradebookCollection().size() > 0) {
                    //for each course's gradebook
                    for (GradebookCollectionObject gradebook : allGradesPost.getGradebookCollection()) {

                        //set the gradebook's list of assignments to the course's list of assignments
                        List<GradebookObject> assignments = gradebook.getAssignments();
                        String courseId = gradebook.getSiteId();

                        Course c = getCourseFromId(courseId);
                        if (c != null)
                            c.setGradebookObjectList(assignments);

                    }

                    hasRequestedAllGrades = true;
                    UICallback.onAllGradesSuccess(coursesSortedByTerm);
                }
                //if no grades...
                else {
                    UICallback.onAllGradesEmpty(R.string.no_grades);
                }

            }

            @Override
            public void onFailure(@NonNull Call<AllGradesPost> call, @NonNull Throwable t) {
                UICallback.onRequestFailure(R.string.network_error, t);
            }
        });
    }


    /**
     * Requests all the grades for a specific course
     * @param siteId id of course
     * @param refresh whether or not we want to refresh
     * @param UICallback callback to execute after request is complete
     */
    public static void requestGradesForSite(final String siteId, boolean refresh, final RequestCallback UICallback) {

        //if we don't want to refresh and we have the grades
        //just use the already cached course
        if (!refresh && gradesRequestedForSite(siteId)) {
            Course course = mapSiteIdToCourse.get(siteId);
            UICallback.onSiteGradesSuccess(course);
            return;
        }


        //pass in site id and callback
        RequestManager.fetchGradesForSite(siteId, new Callback<GradebookCollectionObject>() {
            @Override
            public void onResponse(@NonNull Call<GradebookCollectionObject> call, Response<GradebookCollectionObject> response) {

                GradebookCollectionObject gradebookCollectionObject = response.body();

                if (gradebookCollectionObject != null && gradebookCollectionObject.getAssignments() != null && gradebookCollectionObject.getAssignments().size() > 0) {
                    Course currCourse = DataHandler.getCourseFromId(siteId);
                    currCourse.setGradebookObjectList(gradebookCollectionObject.getAssignments());
                    UICallback.onSiteGradesSuccess(currCourse);
                } else {

                    UICallback.onSiteGradesEmpty(R.string.no_grades);
                }
            }


            @Override
            public void onFailure(@NonNull Call<GradebookCollectionObject> call, Throwable t) {
                UICallback.onRequestFailure(R.string.network_error, t);
            }
        });
    }

    /**
     * Requests all the sites and their site pages
     * @param refresh    whether or not we want to refresh the sites that are cached
     * @param UICallback call back to be run after the request is done
     */
    public static void requestAllCourses(boolean refresh, final RequestCallback UICallback) {

        //dont do anything if we aren't refreshing
        if (!refresh) {
            UICallback.onAllCoursesSuccess(coursesSortedByTerm);
            return;
        }

        RequestManager.fetchAllCourses(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    if (response.body() != null) {
                        String responseBody = response.body().string();

                        ArrayList<Course> allCourses = jsonToCourseObj(responseBody);
                        organizeByTerm(allCourses);
                    } else {
                        UICallback.onAllCoursesEmpty(R.string.no_courses);
                    }
                }
                //if we failed parsing the JSON, say the request failed,
                //since there is a problem with the server
                catch (JSONException e) {
                    e.printStackTrace();
                    UICallback.onRequestFailure(R.string.network_error, e);
                }
                //possible IOException with parsing the response body to a string
                catch (IOException e) {
                    e.printStackTrace();
                    UICallback.onRequestFailure(R.string.network_error, e);
                }

                //reset the has requested grades booleans so we know to
                //refresh them next time we want to see the grades or assignments
                hasRequestedAllGrades = false;
                hasRequestedAllAssignments = false;
                UICallback.onAllCoursesSuccess(coursesSortedByTerm);


            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                UICallback.onRequestFailure(R.string.network_error, throwable);
            }
        });
    }


    /**
     * Parses the raw response body into an array list of courses
     *
     * @param responseBody raw json response from sakai api
     * @return array list of parsed courses
     * @throws JSONException if the response was unable to be parsed
     */
    private static ArrayList<Course> jsonToCourseObj(String responseBody) throws JSONException {


        ArrayList<Course> coursesList = new ArrayList<>();
        mapSiteIdToCourse = new HashMap<>();


        JSONObject obj = new JSONObject(responseBody);
        JSONArray courses = obj.getJSONArray("site_collection");

        for (int i = 0; i < courses.length(); i++) {

            JSONObject currCourse = courses.getJSONObject(i);
            Course c = new Course(currCourse);
            coursesList.add(c);

            mapSiteIdToCourse.put(c.getId(), c);
        }


        return coursesList;
    }


    /**
     * Helper method to get the course from a siteID
     * @param siteId id of course
     * @return the corresponding course
     */
    public static Course getCourseFromId(String siteId) {

        return mapSiteIdToCourse.get(siteId);
    }


    /**
     * Organizes the Course objects by term. Makes a seperate ArrayList for sites collections
     * in the same term; terms are sorted chronologically.
     *
     * @param courses = ArrayList of course objects
     */
    private static void organizeByTerm(ArrayList<Course> courses) {


        //term objects extends comparator
        //sorted chronologically (most recent at the top)
        Collections.sort(courses, new Comparator<Course>() {
            @Override
            public int compare(Course o1, Course o2) {
                return -1 * o1.getTerm().compareTo(o2.getTerm());
            }
        });


        ArrayList<ArrayList<Course>> sorted = new ArrayList<>();

        Term currTerm = courses.get(0).getTerm();
        ArrayList<Course> currSites = new ArrayList<>();

        for (Course course : courses) {

            //if terms are the same, just add to current array list
            if (course.getTerm().compareTo(currTerm) == 0) {
                currSites.add(course);
            }
            //otherwise finalize the current arraylist of terms and make a new arraylist
            //to hold site collections of a different term
            else {
                sorted.add(currSites);

                currSites = new ArrayList<>();
                currSites.add(course);

                currTerm = course.getTerm();
            }

        }

        //add the final current sites
        sorted.add(currSites);

        coursesSortedByTerm = sorted;
    }


}
