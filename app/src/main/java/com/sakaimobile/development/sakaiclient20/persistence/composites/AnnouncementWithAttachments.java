package com.sakaimobile.development.sakaiclient20.persistence.composites;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import com.sakaimobile.development.sakaiclient20.persistence.entities.Announcement;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Attachment;

import java.util.List;

public class AnnouncementWithAttachments {

    @Embedded
    public final Announcement announcement;

    @Relation(parentColumn = "announcementId", entityColumn = "announcementId")
    public List<Attachment> attachments;

    public AnnouncementWithAttachments(Announcement announcement) {
        this.announcement = announcement;
    }

}
