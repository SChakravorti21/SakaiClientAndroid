package com.example.development.sakaiclient20.models.sakai.gradebook;

import com.example.development.sakaiclient20.persistence.entities.Grade;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GradeCollection {

    @SerializedName("assignments")
    @Expose
    private List<Grade> assignments = null;
    @SerializedName("siteId")
    @Expose
    private String siteId;
    @SerializedName("siteName")
    @Expose
    private String siteName;


    public List<Grade> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<Grade> assignments) {
        this.assignments = assignments;
    }

    public String getSiteId() {
        return siteId;
    }

    public String getSiteName() { return siteName; }


}
