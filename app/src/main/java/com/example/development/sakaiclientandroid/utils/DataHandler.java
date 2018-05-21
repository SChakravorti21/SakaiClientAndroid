package com.example.development.sakaiclientandroid.utils;

import com.example.development.sakaiclientandroid.models.Course;
import com.example.development.sakaiclientandroid.models.Term;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DataHandler {


    private static ArrayList<ArrayList<Course>> coursesSortedByTerm;



    public static ArrayList<ArrayList<Course>> getCoursesSortedByTerm() {
        return coursesSortedByTerm;
    }



    //get sorted time grades for one class

    //get all grades sorted by term, and then by time.

    // **repeat for assignments, announcements**


    public static Course getCourseFromId(String siteId) {

        for(ArrayList<Course> courses : coursesSortedByTerm) {
            for(Course c : courses) {
                if(c.getId() == siteId) {
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
