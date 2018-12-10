package com.example.development.sakaiclient20.persistence.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.example.development.sakaiclient20.models.Term;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Development on 8/5/18.
 */

@Entity(tableName = "courses")
public class Course implements Serializable {

    @Ignore
    private static final long serialVersionUID = 2381568326523856L;

    @NonNull
    @PrimaryKey
    public final String siteId;
    public String title;
    public String description;
    public Term term;
    public String siteOwner;
    public int subjectCode;
    public String assignmentSitePageUrl;

    @Ignore
    public List<Grade> grades;

    @Ignore
    public List<SitePage> sitePages;

    @Ignore
    public List<Assignment> assignments;

    @Ignore
    public List<Announcement> announcements;

    public Course(@NonNull String siteId) {
        this.siteId = siteId;
    }

}
