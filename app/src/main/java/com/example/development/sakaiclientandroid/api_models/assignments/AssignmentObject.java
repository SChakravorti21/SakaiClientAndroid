package com.example.development.sakaiclientandroid.api_models.assignments;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.example.development.sakaiclientandroid.models.Term;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class AssignmentObject implements Serializable
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
    @SerializedName("groups")
    @Expose
    private List<Object> groups = new ArrayList<Object>();
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

    // Term does not come in the response, but it is used internally
    // for sorting by date
    @SerializedName("term")
    @Expose
    private Term term;

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

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public String getAuthorLastModified() {
        return authorLastModified;
    }

    public void setAuthorLastModified(String authorLastModified) {
        this.authorLastModified = authorLastModified;
    }

    public CloseTime getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(CloseTime closeTime) {
        this.closeTime = closeTime;
    }

    public String getCloseTimeString() {
        return closeTimeString;
    }

    public void setCloseTimeString(String closeTimeString) {
        this.closeTimeString = closeTimeString;
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

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public DueTime getDueTime() {
        return dueTime;
    }

    public void setDueTime(DueTime dueTime) {
        this.dueTime = dueTime;
    }

    public String getDueTimeString() {
        return dueTimeString;
    }

    public void setDueTimeString(String dueTimeString) {
        this.dueTimeString = dueTimeString;
    }

    public String getGradeScale() {
        return gradeScale;
    }

    public void setGradeScale(String gradeScale) {
        this.gradeScale = gradeScale;
    }

    public String getGradeScaleMaxPoints() {
        return gradeScaleMaxPoints;
    }

    public void setGradeScaleMaxPoints(String gradeScaleMaxPoints) {
        this.gradeScaleMaxPoints = gradeScaleMaxPoints;
    }

    public Object getGradebookItemId() {
        return gradebookItemId;
    }

    public void setGradebookItemId(Object gradebookItemId) {
        this.gradebookItemId = gradebookItemId;
    }

    public Object getGradebookItemName() {
        return gradebookItemName;
    }

    public void setGradebookItemName(Object gradebookItemName) {
        this.gradebookItemName = gradebookItemName;
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

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public Object getModelAnswerText() {
        return modelAnswerText;
    }

    public void setModelAnswerText(Object modelAnswerText) {
        this.modelAnswerText = modelAnswerText;
    }

    public OpenTime getOpenTime() {
        return openTime;
    }

    public void setOpenTime(OpenTime openTime) {
        this.openTime = openTime;
    }

    public String getOpenTimeString() {
        return openTimeString;
    }

    public void setOpenTimeString(String openTimeString) {
        this.openTimeString = openTimeString;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSubmissionType() {
        return submissionType;
    }

    public void setSubmissionType(String submissionType) {
        this.submissionType = submissionType;
    }

    public TimeLastModified getTimeLastModified() {
        return timeLastModified;
    }

    public void setTimeLastModified(TimeLastModified timeLastModified) {
        this.timeLastModified = timeLastModified;
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

    public void setAllowResubmission(Boolean allowResubmission) {
        this.allowResubmission = allowResubmission;
    }

    public String getEntityURL() {
        return entityURL;
    }

    public void setEntityURL(String entityURL) {
        this.entityURL = entityURL;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getEntityTitle() {
        return entityTitle;
    }

    public void setEntityTitle(String entityTitle) {
        this.entityTitle = entityTitle;
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
