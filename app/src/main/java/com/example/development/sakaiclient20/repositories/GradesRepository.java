package com.example.development.sakaiclient20.repositories;

import android.os.AsyncTask;
import com.example.development.sakaiclient20.models.sakai.gradebook.GradesResponse;
import com.example.development.sakaiclient20.models.sakai.gradebook.SiteGrades;
import com.example.development.sakaiclient20.networking.services.GradesService;
import com.example.development.sakaiclient20.persistence.access.GradeDao;
import com.example.development.sakaiclient20.persistence.entities.Grade;

import java.lang.ref.WeakReference;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public class GradesRepository {

    private GradeDao gradesDao;
    private GradesService gradesService;

    public GradesRepository(GradeDao gradeDao, GradesService service) {
        this.gradesDao = gradeDao;
        this.gradesService = service;
    }

    public Single<List<Grade>> getGradesForSite(String siteId) {
        return gradesDao
                .getGradesForSite(siteId)
                .firstOrError();
    }

    public Completable refreshSiteGrades(String siteId) {
        return this.gradesService
                .getGradeForSite(siteId)
                .map(SiteGrades::getGradesList)
                .map(this::persistGrades)
                .toObservable()
                .ignoreElements();
    }

    public Completable refreshAllGrades() {
        // get the list of site grades
        // for each sitegrades obj, get its list of grades
        // persist each list of grades
        // collect the results, then mark as complete
        return this.gradesService
                .getAllGrades()
                .map(GradesResponse::getSiteGrades)
                .toObservable()
                .flatMapIterable(siteGrades -> siteGrades)
                .map(SiteGrades::getGradesList)
                .map(this::persistGrades)
                .ignoreElements();
    }

    private List<Grade> persistGrades(List<Grade> grades) {

        // all of the grades in the given list are of the same course (Same siteId)
        String siteId = grades.get(0).siteId;
        // async task to insert the grades
        InsertGradesTask task = new InsertGradesTask(this.gradesDao, siteId);
        task.execute(grades.toArray(new Grade[0]));

        return grades;
    }


    private static class InsertGradesTask extends AsyncTask<Grade, Void, Void> {

        private WeakReference<GradeDao> gradeDao;
        private String siteId;

        InsertGradesTask(GradeDao dao, String siteId) {
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
