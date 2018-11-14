package com.example.development.sakaiclient20.repositories;

import android.os.AsyncTask;
import android.provider.MediaStore;

import com.example.development.sakaiclient20.networking.services.GradesService;
import com.example.development.sakaiclient20.persistence.access.GradeDao;
import com.example.development.sakaiclient20.persistence.entities.Grade;

import java.lang.ref.WeakReference;
import java.util.List;

import io.reactivex.Single;

public class GradesRepository {

    private GradeDao gradesDao;
    private GradesService gradesService;

    public GradesRepository(GradeDao dao, GradesService service) {
        this.gradesDao = dao;
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



    private List<Grade> persistGrades(List<Grade> grades) {

        // this is valid, since we know all the grades coming in are from
        // the same site
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

            if(gradeDao == null || gradeDao.get() == null)
                return null;

            gradeDao.get().insertGradesForSite(siteId, grades);

            return null;
        }
    }

}
