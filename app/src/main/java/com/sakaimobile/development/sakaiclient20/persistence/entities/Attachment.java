package com.sakaimobile.development.sakaiclient20.persistence.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

/**
 * Created by Development on 8/5/18.
 */

@Entity(tableName = "attachments",
        foreignKeys = {
                @ForeignKey(entity = Assignment.class,
                        parentColumns = "assignmentId",
                        childColumns = "assignmentId",
                        onDelete = ForeignKey.CASCADE,
                        onUpdate = ForeignKey.CASCADE),
                @ForeignKey(entity = Announcement.class,
                        parentColumns = "announcementId",
                        childColumns = "announcementId",
                        onDelete = ForeignKey.CASCADE,
                        onUpdate = ForeignKey.CASCADE)
        },
        indices = {
                @Index(value = "assignmentId"),
                @Index(value = "announcementId")
        })
public class Attachment implements Serializable {
    @PrimaryKey
    @NonNull
    public String url;
    public String name;
    public String assignmentId;
    public String announcementId;

    public Attachment(@NonNull String url) {
        this.url = url;
    }
}
