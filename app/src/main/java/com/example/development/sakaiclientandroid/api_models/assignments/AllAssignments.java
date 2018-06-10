package com.example.development.sakaiclientandroid.api_models.assignments;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class AllAssignments implements Serializable
{

    @SerializedName("entityPrefix")
    @Expose
    private String entityPrefix;
    @SerializedName("assignment_collection")
    @Expose
    private List<AssignmentObject> assignmentObject = new ArrayList<AssignmentObject>();
    private final static long serialVersionUID = 6209006925278274013L;

    public String getEntityPrefix() {
        return entityPrefix;
    }

    public void setEntityPrefix(String entityPrefix) {
        this.entityPrefix = entityPrefix;
    }

    public List<AssignmentObject> getAssignmentObject() {
        return assignmentObject;
    }

    public void setAssignmentObject(List<AssignmentObject> assignmentObject) {
        this.assignmentObject = assignmentObject;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("entityPrefix", entityPrefix).append("assignmentObject", assignmentObject).toString();
    }

}
