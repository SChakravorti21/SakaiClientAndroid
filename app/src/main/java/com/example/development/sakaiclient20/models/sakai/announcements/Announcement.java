package com.example.development.sakaiclient20.models.sakai.announcements;

/**
 * Created by atharva on 7/7/18
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Announcement implements Serializable {

    private String shortFormattedDate;
    private String longFormattedDate;

    @SerializedName("announcementId")
    @Expose
    private String announcementId;
    @SerializedName("attachments")
    @Expose
    private List<Attachment> attachments = null;
    @SerializedName("body")
    @Expose
    private String body;
    @SerializedName("createdByDisplayName")
    @Expose
    private String createdByDisplayName;
    @SerializedName("createdOn")
    @Expose
    private Long createdOn;
    @SerializedName("siteId")
    @Expose
    private String siteId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("entityTitle")
    @Expose
    private String entityTitle;
    private final static long serialVersionUID = -8704355189688718489L;

    public String getAnnouncementId() {
        return announcementId;
    }

    public void setAnnouncementId(String announcementId) {
        this.announcementId = announcementId;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCreatedByDisplayName() {
        return createdByDisplayName;
    }

    public void setCreatedByDisplayName(String createdByDisplayName) {
        this.createdByDisplayName = createdByDisplayName;
    }

    public Long getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Long createdOn) {
        this.createdOn = createdOn;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEntityTitle() {
        return entityTitle;
    }

    public void setEntityTitle(String entityTitle) {
        this.entityTitle = entityTitle;
    }

    /**
     * Formats a millisecond time into a Date string to be shown to the user.
     * If the date of the announcement was less than 5 days ago, then just show the day
     * eg. Mon
     * Or if the announcement was posted a longer time ago, shown the month and day.
     *
     * @return formatted string (either Tues, or Jun 5 format)
     */
    public String getShortFormattedDate() {

        //if the date has not been formatted yet, format it, save it, then return
        if (shortFormattedDate == null) {
            Date createdDate = new Date(createdOn);
            Date currentDate = new Date(System.currentTimeMillis());

            long daysPassedMs = currentDate.getTime() - createdDate.getTime();
            float days = (daysPassedMs / 1000 / 60 / 60 / 24);

            if(days <= 0.5f)
                shortFormattedDate = "today";
            else if (days <= 5f)
                shortFormattedDate = (new SimpleDateFormat("EEE").format(createdDate));
            else
                shortFormattedDate = (new SimpleDateFormat("MMM d").format(createdDate));
        }


        //if it was already formatted then just get the formatted one.
        return shortFormattedDate;
    }


    public String getLongFormattedDate() {

        //if the date has not been formatted yet, format it, save it, then return
        if (longFormattedDate == null) {
            Date createdDate = new Date(createdOn);
            Date currentDate = new Date(System.currentTimeMillis());

            long daysPassedMs = currentDate.getTime() - createdDate.getTime();
            float days = (daysPassedMs / 1000 / 60 / 60 / 24);
            int years = (int)(days / 365);


            if (years < 1)
                longFormattedDate = (new SimpleDateFormat("MMM d 'at' h:mm a").format(createdDate));
            else
                longFormattedDate = (new SimpleDateFormat("MMM d, yyyy 'at' h:mm a").format(createdDate));
        }


        //if it was already formatted then just get the formatted one.
        return longFormattedDate;
    }

}
