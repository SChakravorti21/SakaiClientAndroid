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

    //TODO add announcements

    public Course(@NonNull String siteId) {
        this.siteId = siteId;
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeObject(this);
    }

    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        Course course = (Course) in.readObject();
        this.assignments = course.assignments;
        this.title = course.title;
        this.term = course.term;
    }
}
