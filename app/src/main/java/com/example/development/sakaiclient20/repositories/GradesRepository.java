package com.example.development.sakaiclient20.repositories;

import android.os.AsyncTask;
import android.provider.MediaStore;

import com.example.development.sakaiclient20.models.Term;
import com.example.development.sakaiclient20.models.sakai.gradebook.GradesResponse;
import com.example.development.sakaiclient20.models.sakai.gradebook.SiteGrades;
import com.example.development.sakaiclient20.networking.services.GradesService;
import com.example.development.sakaiclient20.persistence.access.CourseDao;
import com.example.development.sakaiclient20.persistence.access.GradeDao;
import com.example.development.sakaiclient20.persistence.entities.Grade;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import io.reactivex.Single;

public class GradesRepository {

    private GradeDao gradesDao;
    private CourseDao courseDao;
    private GradesService gradesService;

    public GradesRepository(GradeDao gradeDao, CourseDao courseDao, GradesService service) {
        this.gradesDao = gradeDao;
        this.courseDao = courseDao;
        this.gradesService = service;
    }

    public Single<List<Grade>> getGradesForSite(String siteId, boolean refresh) {
        // if refreshing, request the grades again from the service and then store in db
        if (refresh) {
            // request the grades from the grades service
            // extract the grade objects from the response
            // store the newly requested grades in db
            return this.gradesService
                    .getGradeForSite(siteId)
                    .map(siteGrades -> siteGrades.gradesList)
                    .map(this::persistGrades);
        }
        // if not refreshing, get grades from db
        else {
            return gradesDao
                    .getGradesForSite(siteId)
                    .firstOrError();
        }
    }

//    term -> (course -> grade list)

    public Single<TreeMap<Term, HashMap<String, List<Grade>>>> getAllGradesSortedByTerm(boolean refresh) {

        if (refresh) {
            return this.gradesService
                    .getAllGrades()
                    .map(GradesResponse::getSiteGrades)
                    .map(this::sortSiteGradesByTerm);
        } else {
            return this.gradesDao
                    .getAllGrades()
                    .firstOrError()
                    .map(this::sortGradesByTerm);
        }

    }


    /**
     * Sorts the grades by term into a tree map
     * See sortSiteGradesByTerm
     *
     * @param allGrades list of grade objects
     * @return tree map of term -> hashmap of siteID -> list of grades for that course
     */
    private TreeMap<Term, HashMap<String, List<Grade>>> sortGradesByTerm(List<Grade> allGrades) {

        TreeMap<Term, HashMap<String, List<Grade>>> gradesSortedByTerm = new TreeMap<>();

        for(Grade grade : allGrades) {

            String siteId = grade.siteId;
            Term term = courseDao.getTermForCourse(siteId);

            // if the term exists in the tree map...
            if(gradesSortedByTerm.containsKey(term)) {
                HashMap<String, List<Grade>> siteIdToGrades = gradesSortedByTerm.get(term);

                // if the siteId exists in the hashmap, add the grade to the list in there
                if(siteIdToGrades.containsKey(siteId)) {
                    siteIdToGrades.get(siteId).add(grade);
                }
                // if siteid not in hashmap, make a new list and add the grade there
                else {
                    ArrayList<Grade> grades = new ArrayList<>();
                    grades.add(grade);
                    siteIdToGrades.put(siteId, grades);
                }
            }
            // if term not exist in tree map, make a new hashmap and add a new list of grades there
            else {
                HashMap<String, List<Grade>> siteIdToGrades = new HashMap<>();
                ArrayList<Grade> grades = new ArrayList<>();
                grades.add(grade);

                siteIdToGrades.put(siteId, grades);
                gradesSortedByTerm.put(term, siteIdToGrades);
            }

        }

        return gradesSortedByTerm;
    }


    /**
     * Sorts the grades by term
     *
     * @param siteGradesList site grades list, unordered
     * @return Treemap which maps from term to a hashmap
     *          The hashmap maps from siteId to a list of grades
     *          The return value is sorted by term
     *          tree map of term -> hashmap of siteID -> list of grades for that course
     */
    private TreeMap<Term, HashMap<String, List<Grade>>> sortSiteGradesByTerm(List<SiteGrades> siteGradesList) {

        // term maps to hashmap
        // then the site id of the course maps to its list of grades
        TreeMap<Term, HashMap<String, List<Grade>>> siteGradesByTerm = new TreeMap<>();

        // add all sitegrade objects to the treemap, organized by term
        for (SiteGrades siteGrades : siteGradesList) {
            String siteId = siteGrades.siteId;
            Term term = courseDao.getTermForCourse(siteId);

            // persist grades for this site
            persistGrades(siteGrades.gradesList);

            if (siteGradesByTerm.containsKey(term)) {
                // for that term, map the siteID to its grade list
                siteGradesByTerm.get(term).put(siteId, siteGrades.gradesList);

            } else {
                // map from siteID to grades list
                HashMap<String, List<Grade>> siteIdToGrades = new HashMap<>();
                siteIdToGrades.put(siteId, siteGrades.gradesList);

                // map from the term to the siteID
                siteGradesByTerm.put(term, siteIdToGrades);
            }

        }

        return siteGradesByTerm;
    }


    private List<Grade> persistGrades(List<Grade> grades) {

        // all of the grades in the given list are of the same course (Same siteId)
        String siteId = grades.get(0).siteId;
        // async task to insert the grades
        InsertGradesTask task = new InsertGradesTask(this.gradesDao, siteId);
        task.execute(grades.toArray(new Grade[grades.size()]));

        return grades;
    }


    private static class InsertGradesTask extends AsyncTask<Grade, Void, Void> {

        private WeakReference<GradeDao> gradeDao;
        private String siteId;

        public InsertGradesTask(GradeDao dao, String siteId) {
            this.gradeDao = new WeakReference<>(dao);
            this.siteId = siteId;
        }

        @Override
        protected Void doInBackground(Grade... grades) {

            if (gradeDao == null || gradeDao.get() == null)
                return null;

            gradeDao.get().insertGradesForSite(siteId, grades);

            return null;
        }
    }

}
