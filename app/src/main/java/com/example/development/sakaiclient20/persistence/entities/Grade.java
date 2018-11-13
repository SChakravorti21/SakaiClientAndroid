package com.example.development.sakaiclient20.persistence.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Development on 8/5/18.
 */

@Entity(tableName = "grades",
        foreignKeys = @ForeignKey(entity = Course.class,
                                    parentColumns = "siteId",
                                    childColumns = "siteId",
                                    onDelete = ForeignKey.CASCADE,
                                    onUpdate = ForeignKey.CASCADE),
        indices = @Index(value = "siteId"))
public class Grade {
    // autogenerates means the id is incremented each time a new grade is added (our own id, not sakai's)
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String siteId;
    public String grade;
    public String itemName;
    public double points;
}
