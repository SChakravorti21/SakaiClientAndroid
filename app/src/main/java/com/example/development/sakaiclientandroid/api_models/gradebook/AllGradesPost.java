package com.example.development.sakaiclientandroid.api_models.gradebook;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class AllGradesPost {

    @SerializedName("gradebook_collection")
    @Expose
    private List<GradebookCollectionObject> gradebookCollection = null;


    public List<GradebookCollectionObject> getGradebookCollection() {
        return gradebookCollection;
    }


}
