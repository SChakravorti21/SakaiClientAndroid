package com.example.development.sakaiclient20.models.sakai.gradebook;

import com.example.development.sakaiclient20.persistence.entities.Grade;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SiteGrades {

    private List<Grade> gradesList;
    public String siteId;
    public String siteName;

    public SiteGrades(String siteId, String siteName, List<Grade> gradesList) {
        this.siteId = siteId;
        this.siteName = siteName;
        this.gradesList = gradesList;
    }

    public String getSiteId() { return this.siteId; }

    public String getSiteName() { return this.siteName; }

    public List<Grade> getGradesList() { return this.gradesList; }

    public void setGradesList(List<Grade> newGrades) {
        this.gradesList = newGrades;
    }

}
