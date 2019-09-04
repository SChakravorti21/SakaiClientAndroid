package com.sakaimobile.development.sakaiclient20.persistence.entities;


import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "announcements",
        foreignKeys = @ForeignKey(entity = Course.class,
                parentColumns = "siteId",
                childColumns = "siteId",
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE),
        indices = {
                @Index(value = "siteId"),
                @Index(value = "announcementId")
        })
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

    @Ignore public int subjectCode;
    @Ignore public String courseTitle;

    // Formatted dates are generated at runtime to be shown to the user
    // since we use relative terms like "Today"
    @Ignore private String shortFormattedDate;
    @Ignore private String longFormattedDate;


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
        //if it was already formatted then just get the formatted one.
        if(shortFormattedDate != null)
            return shortFormattedDate;

        //if the date has not been formatted yet, format it, save it, then return
        Date createdDate = new Date(createdOn);
        Date currentDate = new Date(System.currentTimeMillis());

        long daysPassedMs = currentDate.getTime() - createdDate.getTime();
        float days = (daysPassedMs / 1000 / 60 / 60 / 24);

        if(days <= 0.5f)
            shortFormattedDate = "today";
        else if (days <= 5f)
            shortFormattedDate = (new SimpleDateFormat("EEE", Locale.US).format(createdDate));
        else
            shortFormattedDate = (new SimpleDateFormat("MMM d", Locale.US).format(createdDate));

        return shortFormattedDate;
    }


    public String getLongFormattedDate() {
        //if it was already formatted then just get the formatted one.
        if(longFormattedDate != null)
            return longFormattedDate;

        //if the date has not been formatted yet, format it, save it, then return
        Date createdDate = new Date(createdOn);
        Date currentDate = new Date(System.currentTimeMillis());

        long daysPassedMs = currentDate.getTime() - createdDate.getTime();
        float days = (daysPassedMs / 1000 / 60 / 60 / 24);
        int years = (int)(days / 365);

        longFormattedDate = (years < 1)
            ? (new SimpleDateFormat("MMM d 'at' h:mm a", Locale.US).format(createdDate))
            : (new SimpleDateFormat("MMM d, yyyy 'at' h:mm a", Locale.US).format(createdDate));

        return longFormattedDate;
    }


}
