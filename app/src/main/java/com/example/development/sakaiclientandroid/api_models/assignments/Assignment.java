package com.example.development.sakaiclientandroid.api_models.assignments;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.example.development.sakaiclientandroid.models.Term;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Assignment implements Serializable
{

    @SerializedName("attachments")
    @Expose
    private List<Attachment> attachments = new ArrayList<Attachment>();
    @SerializedName("authorLastModified")
    @Expose
    private String authorLastModified;
    @SerializedName("authors")
    @Expose
    private List<Object> authors = new ArrayList<Object>();
    @SerializedName("closeTime")
    @Expose
    private CloseTime closeTime;
    @SerializedName("closeTimeString")
    @Expose
    private String closeTimeString;
    @SerializedName("context")
    @Expose
    private String context;
    @SerializedName("creator")
    @Expose
    private String creator;
    @SerializedName("dueTime")
    @Expose
    private DueTime dueTime;
    @SerializedName("dueTimeString")
    @Expose
    private String dueTimeString;
    @SerializedName("gradeScale")
    @Expose
    private String gradeScale;
    @SerializedName("gradeScaleMaxPoints")
    @Expose
    private String gradeScaleMaxPoints;
    @SerializedName("gradebookItemId")
    @Expose
    private Object gradebookItemId;
    @SerializedName("gradebookItemName")
    @Expose
    private Object gradebookItemName;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("instructions")
    @Expose
    private String instructions;
    @SerializedName("modelAnswerText")
    @Expose
    private Object modelAnswerText;
    @SerializedName("openTime")
    @Expose
    private OpenTime openTime;
    @SerializedName("openTimeString")
    @Expose
    private String openTimeString;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("submissionType")
    @Expose
    private String submissionType;
    @SerializedName("timeLastModified")
    @Expose
    private TimeLastModified timeLastModified;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("allowResubmission")
    @Expose
    private Boolean allowResubmission;
    @SerializedName("entityURL")
    @Expose
    private String entityURL;
    @SerializedName("entityId")
    @Expose
    private String entityId;
    @SerializedName("entityTitle")
    @Expose
    private String entityTitle;
    @SerializedName("entityReference")
    @Expose
    private String entityReference;

    // Term does not come in the response, but it is used internally
    // for sorting by date
    @SerializedName("term")
    @Expose
    private Term term;
    @SerializedName("currentGrade")
    @Expose
    private String currentGrade;
    @SerializedName("assignmentSitePageUrl")
    @Expose
    private String assignmentSitePageUrl;

    private final static long serialVersionUID = 835944991348229740L;

    public Term getTerm() {
        return this.term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public String getAuthorLastModified() {
        return authorLastModified;
    }

    public CloseTime getCloseTime() {
        return closeTime;
    }

    public String getCloseTimeString() {
        return closeTimeString;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getCreator() {
        return creator;
    }

    public DueTime getDueTime() {
        return dueTime;
    }

    public String getDueTimeString() {
        return dueTimeString;
    }

    public String getGradeScale() {
        return gradeScale;
    }

    public String getGradeScaleMaxPoints() {
        return gradeScaleMaxPoints;
    }

    public Object getGradebookItemId() {
        return gradebookItemId;
    }

    public Object getGradebookItemName() {
        return gradebookItemName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInstructions() {
        return instructions;
    }

    public Object getModelAnswerText() {
        return modelAnswerText;
    }

    public OpenTime getOpenTime() {
        return openTime;
    }

    public String getOpenTimeString() {
        return openTimeString;
    }

    public String getStatus() {
        return status;
    }

    public String getSubmissionType() {
        return submissionType;
    }

    public TimeLastModified getTimeLastModified() {
        return timeLastModified;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getAllowResubmission() {
        return allowResubmission;
    }

    public String getEntityURL() {
        return entityURL;
    }

    public String getEntityId() {
        return entityId;
    }

    public String getEntityTitle() {
        return entityTitle;
    }

    public String getEntityReference() {
        return entityReference;
    }

    public String getCurrentGrade() {
        return currentGrade;
    }

    public void setCurrentGrade(String currentGrade) {
        this.currentGrade = currentGrade;
    }

    public String getAssignmentSitePageUrl() {
        return assignmentSitePageUrl;
    }

    public void setAssignmentSitePageUrl(String assignmentSitePageUrl) {
        this.assignmentSitePageUrl = assignmentSitePageUrl;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("attachments", attachments)
                .append("authorLastModified", authorLastModified)
                .append("authors", authors)
                .append("closeTime", closeTime)
                .append("closeTimeString", closeTimeString)
                .append("context", context)
                .append("creator", creator)
                .append("dueTime", dueTime)
                .append("dueTimeString", dueTimeString)
                .append("gradeScale", gradeScale)
                .append("gradeScaleMaxPoints", gradeScaleMaxPoints)
                .append("gradebookItemId", gradebookItemId)
                .append("gradebookItemName", gradebookItemName)
                .append("id", id)
                .append("instructions", instructions)
                .append("modelAnswerText", modelAnswerText)
                .append("openTime", openTime)
                .append("openTimeString", openTimeString)
                .append("status", status)
                .append("submissionType", submissionType)
                .append("timeLastModified", timeLastModified)
                .append("title", title)
                .append("allowResubmission", allowResubmission)
                .append("entityURL", entityURL)
                .append("entityId", entityId)
                .append("entityTitle", entityTitle).toString();
    }
}
