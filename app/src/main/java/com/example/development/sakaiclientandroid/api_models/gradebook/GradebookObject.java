package com.example.development.sakaiclientandroid.api_models.gradebook;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class GradebookObject {

    @SerializedName("grade")
    @Expose
    private String grade;
    @SerializedName("itemName")
    @Expose
    private String itemName;
    @SerializedName("points")
    @Expose
    private Double points;

    public String getGrade() {
        return grade;
    }

    public String getItemName() {
        return itemName;
    }

    public Double getPoints() {
        return points;
    }

}
