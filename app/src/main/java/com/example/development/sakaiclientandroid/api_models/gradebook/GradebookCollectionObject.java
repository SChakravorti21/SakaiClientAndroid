package com.example.development.sakaiclientandroid.api_models.gradebook;

import java.util.List;

import com.example.development.sakaiclientandroid.api_models.gradebook.AssignmentObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class GradebookCollectionObject {

    @SerializedName("assignments")
    @Expose
    private List<AssignmentObject> assignments = null;
    @SerializedName("siteId")
    @Expose
    private String siteId;
    @SerializedName("siteName")
    @Expose
    private String siteName;
    @SerializedName("entityReference")
    @Expose
    private String entityReference;
    @SerializedName("entityURL")
    @Expose
    private String entityURL;

    public List<AssignmentObject> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<AssignmentObject> assignments) {
        this.assignments = assignments;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getEntityReference() {
        return entityReference;
    }

    public void setEntityReference(String entityReference) {
        this.entityReference = entityReference;
    }

    public String getEntityURL() {
        return entityURL;
    }

    public void setEntityURL(String entityURL) {
        this.entityURL = entityURL;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("assignments", assignments).append("siteId", siteId).append("siteName", siteName).append("entityReference", entityReference).append("entityURL", entityURL).toString();
    }

}
