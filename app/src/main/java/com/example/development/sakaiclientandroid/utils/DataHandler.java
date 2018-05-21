package com.example.development.sakaiclientandroid.utils;

import android.util.Log;

import com.example.development.sakaiclientandroid.models.Course;
import com.example.development.sakaiclientandroid.models.Term;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataHandler {


    private static ArrayList<ArrayList<Course>> coursesSortedByTerm;

    public static ArrayList<ArrayList<Course>> getCoursesSortedByTerm() {
        return coursesSortedByTerm;
    }


    public static void getAllSites(final RequestCallback UICallback) {
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

                UICallback.onCoursesSuccess(response);


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


        try {
            JSONObject obj = new JSONObject(responseBody);
            JSONArray courses = obj.getJSONArray("site_collection");

            for (int i = 0; i < courses.length(); i++) {

                JSONObject currCourse = courses.getJSONObject(i);
                Course c = new Course(currCourse);
                coursesList.add(c);
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
