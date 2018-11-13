package com.example.development.sakaiclient20.persistence.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "site_pages",
        foreignKeys = {
            @ForeignKey(entity = Course.class,
                parentColumns = "siteId",
                childColumns = "siteId",
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE)
        },
        indices = {
            @Index(value = "siteId")
        })
public class SitePage {

    @PrimaryKey(autoGenerate = true)
    public int sitePageId;
    public String url;
    public String title;
    public String siteId;

    public SitePage(String siteId, String title, String url) {
        this.siteId = siteId;
        this.title = title;
        this.url = url;
    }

}
