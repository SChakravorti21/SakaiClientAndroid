package com.sakaimobile.development.sakaiclient20.persistence.composites;

import com.sakaimobile.development.sakaiclient20.persistence.entities.Announcement;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Attachment;

import java.util.List;

import androidx.room.Embedded;
import androidx.room.Relation;

public class AnnouncementWithAttachments {

    @Embedded
    public final Announcement announcement;

    @Relation(parentColumn = "announcementId", entityColumn = "announcementId")
    public List<Attachment> attachments;

    public AnnouncementWithAttachments(Announcement announcement) {
        this.announcement = announcement;
    }

}
