package com.sakaimobile.development.sakaiclient20.repositories;

import com.sakaimobile.development.sakaiclient20.models.sakai.gradebook.GradesResponse;
import com.sakaimobile.development.sakaiclient20.models.sakai.gradebook.SiteGrades;
import com.sakaimobile.development.sakaiclient20.networking.services.GradeService;
import com.sakaimobile.development.sakaiclient20.persistence.access.GradeDao;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Grade;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public class GradeRepository {

    private GradeDao gradeDao;
    private GradeService gradeService;

    public GradeRepository(GradeDao gradeDao, GradeService service) {
        this.gradeDao = gradeDao;
        this.gradeService = service;
    }

    public Flowable<List<Grade>> getGradesForSite(String siteId) {
        return gradeDao.getGradesForSite(siteId);
    }

    public Single<List<Grade>> refreshSiteGrades(String siteId) {
        return this.gradeService
                .getGradeForSite(siteId)
                .map(SiteGrades::getGradesList)
                .map(this::persistGrades);
    }

    public Completable refreshAllGrades() {
        // get the list of site grades
        // for each sitegrades obj, get its list of grades
        // persist each list of grades
        // collect the results, then mark as complete
        return this.gradeService
                .getAllGrades()
                .map(GradesResponse::getSiteGrades)
                .toObservable()
                .flatMapIterable(siteGrades -> siteGrades)
                .map(SiteGrades::getGradesList)
                .map(this::persistGrades)
                .ignoreElements();
    }

    private List<Grade> persistGrades(List<Grade> grades) {
        if(grades.size() == 0)
            return grades;

        // all of the grades in the given list are of the same course (Same siteId)
        String siteId = grades.get(0).siteId;
        // insert the grades
        gradeDao.insertGradesForSite(siteId, grades);

        return grades;
    }

}
