package com.example.development.sakaiclientandroid.api_models.assignments;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class AssignmentObject implements Serializable
{

    @SerializedName("access")
    @Expose
    private Access access;
    @SerializedName("allPurposeItemText")
    @Expose
    private Object allPurposeItemText;
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
    @SerializedName("content")
    @Expose
    private Object content;
    @SerializedName("contentReference")
    @Expose
    private String contentReference;
    @SerializedName("context")
    @Expose
    private String context;
    @SerializedName("creator")
    @Expose
    private String creator;
    @SerializedName("dropDeadTime")
    @Expose
    private DropDeadTime dropDeadTime;
    @SerializedName("dropDeadTimeString")
    @Expose
    private String dropDeadTimeString;
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
    @SerializedName("position_order")
    @Expose
    private BigInteger positionOrder;
    @SerializedName("privateNoteText")
    @Expose
    private Object privateNoteText;
    @SerializedName("section")
    @Expose
    private String section;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("submissionType")
    @Expose
    private String submissionType;
    @SerializedName("timeCreated")
    @Expose
    private TimeCreated timeCreated;
    @SerializedName("timeLastModified")
    @Expose
    private TimeLastModified timeLastModified;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("allowResubmission")
    @Expose
    private Boolean allowResubmission;
    @SerializedName("draft")
    @Expose
    private Boolean draft;
    @SerializedName("entityReference")
    @Expose
    private String entityReference;
    @SerializedName("entityURL")
    @Expose
    private String entityURL;
    @SerializedName("entityId")
    @Expose
    private String entityId;
    @SerializedName("entityTitle")
    @Expose
    private String entityTitle;
    private final static long serialVersionUID = 835944991348229740L;

    public Access getAccess() {
        return access;
    }

    public void setAccess(Access access) {
        this.access = access;
    }

    public Object getAllPurposeItemText() {
        return allPurposeItemText;
    }

    public void setAllPurposeItemText(Object allPurposeItemText) {
        this.allPurposeItemText = allPurposeItemText;
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

    public List<Object> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Object> authors) {
        this.authors = authors;
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

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public String getContentReference() {
        return contentReference;
    }

    public void setContentReference(String contentReference) {
        this.contentReference = contentReference;
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

    public DropDeadTime getDropDeadTime() {
        return dropDeadTime;
    }

    public void setDropDeadTime(DropDeadTime dropDeadTime) {
        this.dropDeadTime = dropDeadTime;
    }

    public String getDropDeadTimeString() {
        return dropDeadTimeString;
    }

    public void setDropDeadTimeString(String dropDeadTimeString) {
        this.dropDeadTimeString = dropDeadTimeString;
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

    public List<Object> getGroups() {
        return groups;
    }

    public void setGroups(List<Object> groups) {
        this.groups = groups;
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

    public BigInteger getPositionOrder() {
        return positionOrder;
    }

    public void setPositionOrder(BigInteger positionOrder) {
        this.positionOrder = positionOrder;
    }

    public Object getPrivateNoteText() {
        return privateNoteText;
    }

    public void setPrivateNoteText(Object privateNoteText) {
        this.privateNoteText = privateNoteText;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
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

    public TimeCreated getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(TimeCreated timeCreated) {
        this.timeCreated = timeCreated;
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

    public Boolean getDraft() {
        return draft;
    }

    public void setDraft(Boolean draft) {
        this.draft = draft;
    }

    public String getEntityReference() {
        return entityReference;
    }

    public void setEntityReference(String entityReference) {
        this.entityReference = entityReference;
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
        return new ToStringBuilder(this).append("access", access).append("allPurposeItemText", allPurposeItemText).append("attachments", attachments).append("authorLastModified", authorLastModified).append("authors", authors).append("closeTime", closeTime).append("closeTimeString", closeTimeString).append("content", content).append("contentReference", contentReference).append("context", context).append("creator", creator).append("dropDeadTime", dropDeadTime).append("dropDeadTimeString", dropDeadTimeString).append("dueTime", dueTime).append("dueTimeString", dueTimeString).append("gradeScale", gradeScale).append("gradeScaleMaxPoints", gradeScaleMaxPoints).append("gradebookItemId", gradebookItemId).append("gradebookItemName", gradebookItemName).append("groups", groups).append("id", id).append("instructions", instructions).append("modelAnswerText", modelAnswerText).append("openTime", openTime).append("openTimeString", openTimeString).append("positionOrder", positionOrder).append("privateNoteText", privateNoteText).append("section", section).append("status", status).append("submissionType", submissionType).append("timeCreated", timeCreated).append("timeLastModified", timeLastModified).append("title", title).append("allowResubmission", allowResubmission).append("draft", draft).append("entityReference", entityReference).append("entityURL", entityURL).append("entityId", entityId).append("entityTitle", entityTitle).toString();
    }

}
