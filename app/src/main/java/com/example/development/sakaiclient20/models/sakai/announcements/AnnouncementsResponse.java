package com.example.development.sakaiclient20.models.sakai.announcements;

/**
 * Created by atharva on 7/7/18
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class AnnouncementsResponse implements Serializable {

    @SerializedName("announcement_collection")
    @Expose
    private List<Announcement> announcementCollection = null;
    private final static long serialVersionUID = -73560208033089894L;


    public List<Announcement> getAnnouncements() {
        return announcementCollection;
    }

    public void setAnnouncements(List<Announcement> announcementCollection) {
        this.announcementCollection = announcementCollection;
    }

}
