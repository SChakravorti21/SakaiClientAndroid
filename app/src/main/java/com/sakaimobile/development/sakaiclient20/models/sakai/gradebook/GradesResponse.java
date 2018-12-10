package com.sakaimobile.development.sakaiclient20.models.sakai.gradebook;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class GradesResponse {

    @SerializedName("gradebook_collection")
    @Expose
    private List<SiteGrades> siteGrades = new ArrayList<>();

    public List<SiteGrades> getSiteGrades() {
        return siteGrades;
    }


}
