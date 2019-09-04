package com.sakaimobile.development.sakaiclient20.persistence.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

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
public class SitePage implements Serializable {

    @PrimaryKey
    @NonNull
    public String sitePageId;
    public String siteId;
    public String url;
    public String title;

    public SitePage(String sitePageId, String siteId, String title, String url) {
        this.sitePageId = sitePageId;
        this.siteId = siteId;
        this.title = title;
        this.url = url;
    }

}
