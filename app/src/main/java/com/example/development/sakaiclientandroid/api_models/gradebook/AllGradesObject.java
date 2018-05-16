package com.example.development.sakaiclientandroid.api_models.gradebook;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class AllGradesObject {

    @SerializedName("entityPrefix")
    @Expose
    private String entityPrefix;
    @SerializedName("gradebook_collection")
    @Expose
    private List<GradebookCollectionObject> gradebookCollection = null;

    public String getEntityPrefix() {
        return entityPrefix;
    }

    public void setEntityPrefix(String entityPrefix) {
        this.entityPrefix = entityPrefix;
    }

    public List<GradebookCollectionObject> getGradebookCollection() {
        return gradebookCollection;
    }

    public void setGradebookCollection(List<GradebookCollectionObject> gradebookCollection) {
        this.gradebookCollection = gradebookCollection;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("entityPrefix", entityPrefix).append("gradebookCollection", gradebookCollection).toString();
    }

}
