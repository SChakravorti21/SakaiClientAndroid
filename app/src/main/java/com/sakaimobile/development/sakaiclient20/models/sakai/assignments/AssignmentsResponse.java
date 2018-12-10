package com.sakaimobile.development.sakaiclient20.models.sakai.assignments;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Assignment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AssignmentsResponse implements Serializable
{

    @SerializedName("assignment_collection")
    @Expose
    private List<Assignment> assignments = new ArrayList<>();

    public List<Assignment> getAssignments() {
        return assignments;
    }
}
