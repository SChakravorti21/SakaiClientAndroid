package com.example.development.sakaiclient20.repositories;

import com.example.development.sakaiclient20.models.sakai.gradebook.GradesResponse;
import com.example.development.sakaiclient20.networking.services.GradesService;
import com.example.development.sakaiclient20.persistence.access.GradeDao;
import com.example.development.sakaiclient20.persistence.entities.Grade;

import java.util.List;

import io.reactivex.Single;

public class GradesRepository {

    private GradeDao gradesDao;
    private GradesService gradesService;

    public GradesRepository(GradeDao dao, GradesService service) {
        this.gradesDao = dao;
        this.gradesService = service;
    }

//    public Single<List<Grade>> getGradesForSite(String siteId, boolean refresh) {
//        // if refreshing, request the grades again from the service and then store in db
//        if(refresh) {
//            return this.gradesService.getGradeForSite(siteId)
//        }
//        // if not refreshing, get grades from db
//
//    }

//    private List<Grade> persistGrades(GradesResponse response) {
//
//    }

}
