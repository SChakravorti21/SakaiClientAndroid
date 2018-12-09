package com.example.development.sakaiclient20.persistence.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

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
