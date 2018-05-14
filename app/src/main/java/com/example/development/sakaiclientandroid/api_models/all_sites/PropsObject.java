package com.example.development.sakaiclientandroid.api_models.all_sites;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class PropsObject implements Serializable
{

    @SerializedName("sections_externally_maintained")
    @Expose
    private String sectionsExternallyMaintained;
    @SerializedName("contact-name")
    @Expose
    private String contactName;
    @SerializedName("contact-email")
    @Expose
    private String contactEmail;
    @SerializedName("mathJaxAllowed")
    @Expose
    private String mathJaxAllowed;
    @SerializedName("term")
    @Expose
    private String term;
    @SerializedName("term_eid")
    @Expose
    private String termEid;
    @SerializedName("presenceTool")
    @Expose
    private String presenceTool;
    @SerializedName("original-site-id")
    @Expose
    private String originalSiteId;
    @SerializedName("locale_string")
    @Expose
    private String localeString;
    @SerializedName("joinerGroup")
    @Expose
    private String joinerGroup;
    @SerializedName("template_used")
    @Expose
    private String templateUsed;
    @SerializedName("site-request-course-sections")
    @Expose
    private String siteRequestCourseSections;
    private final static long serialVersionUID = -7586051354731547029L;

    public String getSectionsExternallyMaintained() {
        return sectionsExternallyMaintained;
    }

    public void setSectionsExternallyMaintained(String sectionsExternallyMaintained) {
        this.sectionsExternallyMaintained = sectionsExternallyMaintained;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getMathJaxAllowed() {
        return mathJaxAllowed;
    }

    public void setMathJaxAllowed(String mathJaxAllowed) {
        this.mathJaxAllowed = mathJaxAllowed;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getTermEid() {
        return termEid;
    }

    public void setTermEid(String termEid) {
        this.termEid = termEid;
    }

    public String getPresenceTool() {
        return presenceTool;
    }

    public void setPresenceTool(String presenceTool) {
        this.presenceTool = presenceTool;
    }

    public String getOriginalSiteId() {
        return originalSiteId;
    }

    public void setOriginalSiteId(String originalSiteId) {
        this.originalSiteId = originalSiteId;
    }

    public String getLocaleString() {
        return localeString;
    }

    public void setLocaleString(String localeString) {
        this.localeString = localeString;
    }

    public String getJoinerGroup() {
        return joinerGroup;
    }

    public void setJoinerGroup(String joinerGroup) {
        this.joinerGroup = joinerGroup;
    }

    public String getTemplateUsed() {
        return templateUsed;
    }

    public void setTemplateUsed(String templateUsed) {
        this.templateUsed = templateUsed;
    }

    public String getSiteRequestCourseSections() {
        return siteRequestCourseSections;
    }

    public void setSiteRequestCourseSections(String siteRequestCourseSections) {
        this.siteRequestCourseSections = siteRequestCourseSections;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("sectionsExternallyMaintained", sectionsExternallyMaintained).append("contactName", contactName).append("contactEmail", contactEmail).append("mathJaxAllowed", mathJaxAllowed).append("term", term).append("termEid", termEid).append("presenceTool", presenceTool).append("originalSiteId", originalSiteId).append("localeString", localeString).append("joinerGroup", joinerGroup).append("templateUsed", templateUsed).append("siteRequestCourseSections", siteRequestCourseSections).toString();
    }

}
