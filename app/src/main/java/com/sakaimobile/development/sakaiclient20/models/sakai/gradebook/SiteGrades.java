package com.sakaimobile.development.sakaiclient20.models.sakai.gradebook;

import com.sakaimobile.development.sakaiclient20.persistence.entities.Grade;

import java.util.List;

public class SiteGrades {

    public String siteId;
    private String siteName;
    private List<Grade> gradesList;

    public SiteGrades(String siteId, String siteName, List<Grade> gradesList) {
        this.siteId = siteId;
        this.siteName = siteName;
        this.gradesList = gradesList;
    }

    public List<Grade> getGradesList() { return this.gradesList; }

}
