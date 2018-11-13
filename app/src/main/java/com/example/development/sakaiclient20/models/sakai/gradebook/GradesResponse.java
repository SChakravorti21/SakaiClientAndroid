package com.example.development.sakaiclient20.models.sakai.gradebook;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GradesResponse {

    @SerializedName("gradebook_collection")
    @Expose
    private List<GradeCollection> gradeCollection = null;

    public List<GradeCollection> getGradeCollection() {
        return gradeCollection;
    }


}
