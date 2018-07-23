package com.example.development.sakaiclientandroid.api_models.gradebook;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class GradebookCollectionObject {

    @SerializedName("assignments")
    @Expose
    private List<GradebookObject> assignments = null;
    @SerializedName("siteId")
    @Expose
    private String siteId;
    @SerializedName("siteName")
    @Expose
    private String siteName;


    public List<GradebookObject> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<GradebookObject> assignments) {
        this.assignments = assignments;
    }

    public String getSiteId() {
        return siteId;
    }

    public String getSiteName() { return siteName; }


}
