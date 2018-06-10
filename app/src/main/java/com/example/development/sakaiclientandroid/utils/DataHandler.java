package com.example.development.sakaiclientandroid.utils;

import android.widget.ImageView;

import com.example.development.sakaiclientandroid.R;
import com.example.development.sakaiclientandroid.api_models.assignments.AllAssignments;
import com.example.development.sakaiclientandroid.api_models.gradebook.AllGradesObject;
import com.example.development.sakaiclientandroid.api_models.gradebook.AssignmentObject;
import com.example.development.sakaiclientandroid.api_models.gradebook.GradebookCollectionObject;
import com.example.development.sakaiclientandroid.models.Course;
import com.example.development.sakaiclientandroid.models.Term;
import com.example.development.sakaiclientandroid.utils.requests.RequestCallback;
import com.example.development.sakaiclientandroid.utils.requests.RequestManager;

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

    //needed so that we don't have to make an unnecessary request if all grades
    //have already been requested
    private static boolean hasRequestedAllGrades = false;



    public static ArrayList<ArrayList<Course>> getCoursesSortedByTerm() {
        return coursesSortedByTerm;
    }

    public static List<AssignmentObject> getGradesForCourse(String siteId) {
        return getCourseFromId(siteId).getAssignmentObjectList();
    }

    public static boolean gradesRequestedForSite(String siteId) {
        return getCourseFromId(siteId).getAssignmentObjectList() != null;
    }

    public static boolean gradesRequestedForAllSites() {

        return hasRequestedAllGrades;
    }



    public static Term getTermFromId(String id) {
        return mapSiteIdToCourse.get(id).getTerm();
    }

    public static String getTitleFromId(String id) {
        return mapSiteIdToCourse.get(id).getTitle();
    }

    public static void requestAllAssignments(final RequestCallback UICallback) {
        RequestManager.fetchAllAssignments(new Callback<AllAssignments>() {
            @Override
            public void onResponse(Call<AllAssignments> call, Response<AllAssignments> response) {
                AllAssignments allAssignments = response.body();

                UICallback.onAllAssignmentsSuccess(allAssignments);
            }

            @Override
            public void onFailure(Call<AllAssignments> call, Throwable t) {
                UICallback.onAllAssignmentsFailure(t);
            }
        });
    }

    public static void requestAllGrades(final RequestCallback UICallback) {

        RequestManager.fetchAllGrades(new Callback<AllGradesObject>() {
            @Override
            public void onResponse(Call<AllGradesObject> call, Response<AllGradesObject> response) {

                AllGradesObject allGradesObject = response.body();

                if(allGradesObject != null) {
                    //for each course's gradebook
                    for (GradebookCollectionObject gradebook : allGradesObject.getGradebookCollection()) {

                        //set the gradebook's list of assignments to the course's list of assignments
                        List<AssignmentObject> assignments = gradebook.getAssignments();
                        String courseId = gradebook.getSiteId();

                        Course c = getCourseFromId(courseId);
                        if (c != null)
                            c.setAssignmentObjectList(assignments);
                        int x=2;
                    }

                }

                hasRequestedAllGrades = true;
                UICallback.onAllGradesSuccess();

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




    /**
     * Sets the image resource of an image view depending on the rutgers university
     * subject code that is given. These icons are there to make the app more lively.
     *
     * @param imageView = imageView to set the resource of
     * @param subjectCode = subject code of the class, so that the correct icon can be chosen
     */
    public static void setSiteIcon(ImageView imageView, int subjectCode) {

        switch(subjectCode) {
            case 13:
                imageView.setImageResource(R.drawable.ic_language);
                break;
            case 80:
                imageView.setImageResource(R.drawable.ic_art);
                break;
            case 81:
                imageView.setImageResource(R.drawable.ic_art);
                break;
            case 160:
                imageView.setImageResource(R.drawable.ic_chemistry);
                break;
            case 198:
                imageView.setImageResource(R.drawable.ic_computer);
                break;
            case 220:
                imageView.setImageResource(R.drawable.ic_economics);
                break;
            case 420:
                imageView.setImageResource(R.drawable.ic_language);
                break;
            case 447:
                imageView.setImageResource(R.drawable.ic_genetics);
                break;
            case 640:
                imageView.setImageResource(R.drawable.ic_math);
                break;
            case 700:
                imageView.setImageResource(R.drawable.ic_music);
                break;
            case 750:
                imageView.setImageResource(R.drawable.ic_physics);
                break;

        }
    }






    /**
     * uses the courses that are already sorted by term and puts that data into the headers list
     * and the hashmaps so that it can be displays in the expandable list view
     *
<<<<<<< HEAD
     * Used in the home tab and in all grades tab
=======
     * Used in the home tab
>>>>>>> Added spinner when making requests, gradebook only shows courses/terms with grades
     *
     * @param termHeaders list of term headers
     * @param termToCourseTitles hashmap mapping term to a list of courses
     * @param termToCourseSubjectCodes hashmap mapping term to a list of course subj codes
     * @param termToCourseIds hashmap mapping term to course Ids
     */

    public static void prepareHeadersAndChildrenAll(List<String> termHeaders,
                                                    HashMap<String, List<String>> termToCourseTitles,
                                                    HashMap<String, List<Integer>> termToCourseSubjectCodes,
                                                    HashMap<String, List<String>> termToCourseIds) {

        //sets the Term as the headers for the expandable list view
        //each child is the name of the site in that term
        for(ArrayList<Course> coursesPerTerm : coursesSortedByTerm) {

            //we can just look at the first site's term, since all the terms
            //should be the same, since we already sorted
            Term currTerm = coursesPerTerm.get(0).getTerm();

            String termKey = currTerm.getTermString();

            //don't put the year if the header is just General
            if(!termKey.equals("General")) {
                termKey += (" " + currTerm.getYear());
            }

            termHeaders.add(termKey);


            List<String> tempChildList = new ArrayList<>();
            List<Integer> tempSubjectCodeList = new ArrayList<>();
            List<String> tempSiteIdList = new ArrayList<>();

            //places the title of each site and its corresponding ImgResId into 2 lists
            //which are then added to the hashmap under the current term header
            for(Course currCourse : coursesPerTerm) {

                tempChildList.add(currCourse.getTitle());
                tempSiteIdList.add(currCourse.getId());

                //TODO figure out a way to add the resource Id values directly, for more abstraction
                //adds subject code to hashmap
                int subjectCode = currCourse.getSubjectCode();
                tempSubjectCodeList.add(subjectCode);
//                int resId = RutgersSubjectCodes.getResourceIdFromSubjectCode(subjectCode, getActivity().getPackageName(), getContext());
//                tempSubjectCodeList.add(resId);
            }

            termToCourseIds.put(termKey, tempSiteIdList);
            termToCourseSubjectCodes.put(termKey, tempSubjectCodeList);
            termToCourseTitles.put(termKey, tempChildList);
        }


    }




    /**
     * uses the courses that are already sorted by term and puts that data into the headers list
     * and the hashmaps so that it can be displays in the expandable list view
     *
     * Used in the all grades tab
     *
     * @param termHeaders list of term headers
     * @param termToCourseTitles hashmap mapping term to a list of courses
     * @param termToCourseSubjectCodes hashmap mapping term to a list of course subj codes
     * @param termToCourseIds hashmap mapping term to course Ids
     */
    public static void prepareHeadersAndChildrenWithGrades(List<String> termHeaders,
                                                    HashMap<String, List<String>> termToCourseTitles,
                                                    HashMap<String, List<Integer>> termToCourseSubjectCodes,
                                                    HashMap<String, List<String>> termToCourseIds) {

        //sets the Term as the headers for the expandable list view
        //each child is the name of the site in that term
        for(ArrayList<Course> coursesPerTerm : coursesSortedByTerm) {

            //we can just look at the first site's term, since all the terms
            //should be the same, since we already sorted
            Term currTerm = coursesPerTerm.get(0).getTerm();

            String termKey = currTerm.getTermString();

            //don't put the year if the header is just General
            if(!termKey.equals("General")) {
                termKey += (" " + currTerm.getYear());
            }



            List<String> tempChildList = new ArrayList<>();
            List<Integer> tempSubjectCodeList = new ArrayList<>();
            List<String> tempSiteIdList = new ArrayList<>();


            boolean termHasGrades = false;

            //places the title of each site and its corresponding ImgResId into 2 lists
            //which are then added to the hashmap under the current term header
            for(Course currCourse : coursesPerTerm) {

                //if no grades, just don't put in hashmap
                if(currCourse.getAssignmentObjectList() == null)
                    continue;


                termHasGrades = true;
                tempChildList.add(currCourse.getTitle());
                tempSiteIdList.add(currCourse.getId());

                //TODO figure out a way to add the resource Id values directly, for more abstraction
                //adds subject code to hashmap
                int subjectCode = currCourse.getSubjectCode();
                tempSubjectCodeList.add(subjectCode);
//                int resId = RutgersSubjectCodes.getResourceIdFromSubjectCode(subjectCode, getActivity().getPackageName(), getContext());
//                tempSubjectCodeList.add(resId);
            }


            //if the term has no grades, we shouldn't show that term either
            if(termHasGrades) {

                termHeaders.add(termKey);
                termToCourseIds.put(termKey, tempSiteIdList);
                termToCourseSubjectCodes.put(termKey, tempSubjectCodeList);
                termToCourseTitles.put(termKey, tempChildList);
            }
        }


    }


}
