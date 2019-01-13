package com.sakaimobile.development.sakaiclient20.persistence.entities;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(tableName = "announcements",
        foreignKeys = @ForeignKey(entity = Course.class,
                parentColumns = "siteId",
                childColumns = "siteId",
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE),
        indices = {
                @Index(value = "siteId"),
                @Index(value = "announcementId")
        }
)
public class Announcement implements Serializable {

    @Ignore
    private static final long serialVersionUID = 7526472295622776147L;

    @NonNull
    @PrimaryKey
    public final String announcementId;

    @Ignore
    public List<Attachment> attachments = new ArrayList<>();

    public String body;
    public String title;
    public String siteId;
    public String createdBy;
    public long createdOn;

    @Ignore
    private String shortFormattedDate;
    @Ignore
    private String longFormattedDate;

    public Announcement(@NonNull String announcementId) {
        this.announcementId = announcementId;
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
