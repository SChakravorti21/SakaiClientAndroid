package com.example.development.sakaiclientandroid.utils;

import android.util.Log;
import android.widget.ArrayAdapter;

import com.example.development.sakaiclientandroid.api_models.gradebook.AllGradesObject;
import com.example.development.sakaiclientandroid.api_models.gradebook.AssignmentObject;
import com.example.development.sakaiclientandroid.api_models.gradebook.GradebookCollectionObject;
import com.example.development.sakaiclientandroid.models.Course;
import com.example.development.sakaiclientandroid.models.Term;

import org.json.JSONArray;
import org.json.JSONObject;

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



    public static ArrayList<ArrayList<Course>> getCoursesSortedByTerm() {
        return coursesSortedByTerm;
    }

    public static List<AssignmentObject> getGradesForCourse(String siteId) {
        return getCourseFromId(siteId).getAssignmentObjectList();
    }




    public static Term getTermFromId(String id) {
        return mapSiteIdToCourse.get(id).getTerm();
    }

    public static String getTitleFromId(String id) {
        return mapSiteIdToCourse.get(id).getTitle();
    }


    public static void requestAllGrades(final RequestCallback UICallback) {

        RequestManager.fetchAllGrades(new Callback<AllGradesObject>() {
            @Override
            public void onResponse(Call<AllGradesObject> call, Response<AllGradesObject> response) {

                AllGradesObject allGradesObject = response.body();

                //for each course's gradebook
                for(GradebookCollectionObject gradebook : allGradesObject.getGradebookCollection()) {

                    //set the gradebook's list of assignments to the course's list of assignments
                    List<AssignmentObject> assignments = gradebook.getAssignments();
                    String courseId = gradebook.getSiteId();

                    getCourseFromId(courseId).setAssignmentObjectList(assignments);

                    UICallback.onAllGradesSuccess();
                }
            }

            @Override
            public void onFailure(Call<AllGradesObject> call, Throwable t) {
                UICallback.onAllGradesFailure(t);
            }
        });
    }


    public static void requestGradesForSite(final String siteId, final RequestCallback UICallback) {

        //pass in site id and callback
        RequestManager.fetchGradesForSite(siteId, new Callback<GradebookCollectionObject>() {
            @Override
            public void onResponse(Call<GradebookCollectionObject> call, Response<GradebookCollectionObject> response) {

                GradebookCollectionObject gradebookCollectionObject = response.body();

                if(gradebookCollectionObject != null) {
                    Course currCourse = DataHandler.getCourseFromId(siteId);
                    currCourse.setAssignmentObjectList(gradebookCollectionObject.getAssignments());
                }

                UICallback.onSiteGradesSuccess();
            }


            @Override
            public void onFailure(Call<GradebookCollectionObject> call, Throwable t) {
                UICallback.onSiteGradesFailure(t);
            }
        });
    }


    public static void requestAllSites(final RequestCallback UICallback) {
        RequestManager.fetchAllSites(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    String responseBody = response.body().string();

                    ArrayList<Course> allCourses = jsonToCourseObj(responseBody);
                    organizeByTerm(allCourses);
                }
                catch(Exception e) {
                    e.printStackTrace();
                }

                UICallback.onCoursesSuccess();


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                UICallback.onCoursesFailure(throwable);
            }
        });
    }


    //get sorted time grades for one class

    //get all grades sorted by term, and then by time.

    // **repeat for assignments, announcements**

    private static ArrayList<Course> jsonToCourseObj(String responseBody) {


        ArrayList<Course> coursesList = new ArrayList<>();
        mapSiteIdToCourse = new HashMap<>();


        try {
            JSONObject obj = new JSONObject(responseBody);
            JSONArray courses = obj.getJSONArray("site_collection");

            for (int i = 0; i < courses.length(); i++) {

                JSONObject currCourse = courses.getJSONObject(i);
                Course c = new Course(currCourse);
                coursesList.add(c);

                mapSiteIdToCourse.put(c.getId(), c);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }


        return coursesList;
    }


    public static Course getCourseFromId(String siteId) {

        for(ArrayList<Course> courses : coursesSortedByTerm) {
            for(Course c : courses) {
                if(c.getId().equals(siteId)) {
                    return c;
                }
            }
        }

        return null;
    }


    /**
     * Organizes the Course objects by term. Makes a seperate ArrayList for sites collections
     * in the same term; terms are sorted chronologically.
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

        for(Course course : courses) {

            //if terms are the same, just add to current array list
            if(course.getTerm().compareTo(currTerm) == 0) {
                currSites.add(course);
            }
            //otherwise finalize the current arraylist of terms and make a new arraylist
            //to hold site collections of a different term
            else {
                sorted.add(currSites);

                currSites = new ArrayList<Course>();
                currSites.add(course);

                currTerm = course.getTerm();
            }

        }

        //add the final current sites
        sorted.add(currSites);

        coursesSortedByTerm = sorted;
    }

}
