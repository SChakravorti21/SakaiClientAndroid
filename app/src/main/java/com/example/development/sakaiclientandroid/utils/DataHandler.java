package com.example.development.sakaiclientandroid.utils;

import android.support.annotation.NonNull;

import com.example.development.sakaiclientandroid.R;

import com.example.development.sakaiclientandroid.api_models.assignments.AllAssignments;
import com.example.development.sakaiclientandroid.api_models.assignments.Assignment;
import com.example.development.sakaiclientandroid.api_models.gradebook.AllGradesObject;
import com.example.development.sakaiclientandroid.api_models.gradebook.GradebookObject;
import com.example.development.sakaiclientandroid.api_models.gradebook.GradebookCollectionObject;
import com.example.development.sakaiclientandroid.models.Course;
import com.example.development.sakaiclientandroid.models.Term;
import com.example.development.sakaiclientandroid.utils.requests.RequestCallback;
import com.example.development.sakaiclientandroid.utils.requests.RequestManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataHandler {


    private static ArrayList<ArrayList<Course>> coursesSortedByTerm;
    private static ArrayList<ArrayList<Assignment>> assignmentsSortedByDate;
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


    public static void requestAllAssignments(final RequestCallback UICallback,
                                             final boolean sortByCourses) {
        if(hasRequestedAllAssignments) {
            if(sortByCourses) {
                UICallback.onAllAssignmentsByCourseSuccess(coursesSortedByTerm);
            } else {
                UICallback.onAllAssignmentsByDateSuccess(assignmentsSortedByDate);
            }

            return;
        }

        RequestManager.fetchAllAssignments(new Callback<AllAssignments>() {
            @Override
            public void onResponse(@NonNull Call<AllAssignments> call,
                                   @NonNull Response<AllAssignments> response) {
                AllAssignments allAssignments = response.body();

                if(allAssignments == null || allAssignments.getAssignment().size() == 0) {
                    UICallback.onAllAssignmentsFailure(new Throwable("Assignments is empty!"));
                    return;
                }

                for(Assignment assignment : allAssignments.getAssignment()) {
                    Course course = mapSiteIdToCourse.get(assignment.getContext());
                    assignment.setTerm(course.getTerm());
                    course.addAssignment(assignment);
                }

                hasRequestedAllAssignments = true;
                if(sortByCourses) {
                    UICallback.onAllAssignmentsByCourseSuccess(coursesSortedByTerm);
                }

                sortAssignmentsByDate();
                if(!sortByCourses) {
                    UICallback.onAllAssignmentsByDateSuccess(assignmentsSortedByDate);
                }
            }

            @Override
            public void onFailure(@NonNull Call<AllAssignments> call, @NonNull Throwable t) {
                UICallback.onAllAssignmentsFailure(t);
            }
        });
    }

    public static void requestAssignmentsForSite(String siteId,
                                                 final RequestCallback UICallback) {
        RequestManager.fetchAssignmentsForSite(siteId, new Callback<AllAssignments>() {

            @Override
            public void onResponse(Call<AllAssignments> call, Response<AllAssignments> response) {
                AllAssignments allAssignments = response.body();

                if(allAssignments != null) {
                    UICallback.onSiteAssignmentsSuccess( (ArrayList<Assignment>) allAssignments.getAssignment() );
                } else {
                    UICallback.onSiteAssignmentsFailure(new Throwable("No assignments found"));
                }
            }

            @Override
            public void onFailure(Call<AllAssignments> call, Throwable t) {
                UICallback.onSiteAssignmentsFailure(t);
            }
        });
    }

    private static void sortAssignmentsByDate() {
        // 15 terms should be more than enough for most students (3 * 4 = 12 < 15)
        assignmentsSortedByDate = new ArrayList<>(15);

        // For performance reasons, it is actually faster to do a quick scan
        // through all courses to get the number of assignments. There are usually
        // roughly 4-5 courses per term, so this will be quick and save any O(n)
        // insert operations when creating the list of assignments.
        for(ArrayList<Course> courses : coursesSortedByTerm) {
            int numAssignments = 0;
            for(Course course : courses) {
                numAssignments += course.getNumAssignments();
            }

            // Only create a new list is there are assignments that we care about
            if(numAssignments > 0) {
                assignmentsSortedByDate.add(new ArrayList<Assignment>(numAssignments));
            }
        }

        int termIndex = 0;
        for(ArrayList<Course> courses : coursesSortedByTerm) {
            // If there are no courses, there will be no assignments
            if(courses.size() == 0 || termIndex >= assignmentsSortedByDate.size())
                continue;

            ArrayList<Assignment> termAssignments = assignmentsSortedByDate.get(termIndex);
            for(Course course : coursesSortedByTerm.get(termIndex)) {
                termAssignments.addAll(course.getAssignmentList());
            }

            Collections.sort(termAssignments, new Comparator<Assignment>() {
                @Override
                public int compare(Assignment o1, Assignment o2) {
                    Date date1 = new Date(o1.getDueTime().getTime());
                    Date date2 = new Date(o2.getDueTime().getTime());
                    // We want the list sorted in reverse chronological order
                    // so that the latest assignment comes first
                    return -1 * date1.compareTo(date2);
                }
            });

            termIndex++;
        }
    }

    public static void requestAllGrades(boolean refreshGrades, final RequestCallback UICallback) {

        if (hasRequestedAllGrades && !refreshGrades) {
            UICallback.onAllGradesSuccess(coursesSortedByTerm);
            return;
        }

        RequestManager.fetchAllGrades(new Callback<AllGradesObject>() {
            @Override
            public void onResponse(@NonNull Call<AllGradesObject> call, @NonNull Response<AllGradesObject> response) {

                AllGradesObject allGradesObject = response.body();

                if (allGradesObject != null) {
                    //for each course's gradebook
                    for (GradebookCollectionObject gradebook : allGradesObject.getGradebookCollection()) {

                        //set the gradebook's list of assignments to the course's list of assignments
                        List<GradebookObject> assignments = gradebook.getAssignments();
                        String courseId = gradebook.getSiteId();

                        Course c = getCourseFromId(courseId);
                        if (c != null)
                            c.setGradebookObjectList(assignments);

                    }

                }

                hasRequestedAllGrades = true;
                UICallback.onAllGradesSuccess(coursesSortedByTerm);

            }

            @Override
            public void onFailure(@NonNull Call<AllGradesObject> call, @NonNull Throwable t) {
                UICallback.onAllGradesFailure(t);
            }
        });
    }


    public static void requestGradesForSite(final String siteId, boolean refresh, final RequestCallback UICallback) {

        //if we don't want to refresh and we have the grades
        //just use the already cached course
        if(!refresh && gradesRequestedForSite(siteId)) {
            Course course = mapSiteIdToCourse.get(siteId);
            UICallback.onSiteGradesSuccess(course);
            return;
        }


        //pass in site id and callback
        RequestManager.fetchGradesForSite(siteId, new Callback<GradebookCollectionObject>() {
            @Override
            public void onResponse(@NonNull Call<GradebookCollectionObject> call, Response<GradebookCollectionObject> response) {

                GradebookCollectionObject gradebookCollectionObject = response.body();

                if (gradebookCollectionObject != null) {
                    Course currCourse = DataHandler.getCourseFromId(siteId);
                    currCourse.setGradebookObjectList(gradebookCollectionObject.getAssignments());
                    UICallback.onSiteGradesSuccess(currCourse);
                }
                else {

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
     * @param refresh whether or not we want to refresh the sites that are cached
     * @param UICallback call back to be run after the request is done
     */
    public static void requestAllSites(boolean refresh, final RequestCallback UICallback) {

        //dont do anything if we aren't refreshing
        if(!refresh) {
            UICallback.onAllCoursesSuccess(coursesSortedByTerm);
            return;
        }

        RequestManager.fetchAllSites(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    String responseBody = response.body().string();

                    ArrayList<Course> allCourses = jsonToCourseObj(responseBody);
                    organizeByTerm(allCourses);
                } catch (JSONException e) {
                    e.printStackTrace();
                    UICallback.onAllCoursesFailure(new ParseException("Failed parsing json response", 0));
                } catch(Exception e) {
                    e.printStackTrace();
                    UICallback.onAllCoursesFailure(new ParseException("Unable to parse response", 0));
                }

                hasRequestedAllGrades = false;
                hasRequestedAllAssignments = false;
                UICallback.onAllCoursesSuccess(coursesSortedByTerm);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                UICallback.onAllCoursesFailure(throwable);
            }
        });
    }


    //get sorted time grades for one class

    //get all grades sorted by term, and then by time.

    // **repeat for assignments, announcements**

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


    public static Course getCourseFromId(String siteId) {

        for (ArrayList<Course> courses : coursesSortedByTerm) {
            for (Course c : courses) {
                if (c.getId().equals(siteId)) {
                    return c;
                }
            }
        }

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
