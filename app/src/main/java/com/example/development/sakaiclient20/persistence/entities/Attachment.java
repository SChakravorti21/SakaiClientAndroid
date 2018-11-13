package com.example.development.sakaiclient20.persistence.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Development on 8/5/18.
 */

@Entity(tableName = "attachments",
        foreignKeys = {
            @ForeignKey(entity = Assignment.class,
                parentColumns = "assignmentId",
                childColumns = "assignmentId",
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE)
        },
        indices =  {
            @Index(value = "assignmentId")
        })
public class Attachment {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String url;

    public String assignmentId;

}
