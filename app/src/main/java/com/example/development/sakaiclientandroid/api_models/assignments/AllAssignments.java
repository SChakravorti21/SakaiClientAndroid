package com.example.development.sakaiclientandroid.api_models.assignments;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class AllAssignments implements Serializable
{

    @SerializedName("assignment_collection")
    @Expose
    private List<Assignment> assignment = new ArrayList<Assignment>();
    private final static long serialVersionUID = 6209006925278274013L;

    public List<Assignment> getAssignment() {
        return assignment;
    }

    public void setAssignment(List<Assignment> assignment) {
        this.assignment = assignment;
    }

}
