package com.example.development.sakaiclient20.models.sakai.gradebook;

import com.example.development.sakaiclient20.persistence.entities.Grade;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SiteGrades {

    public List<Grade> assignments = null;
    public String siteId;
    public String siteName;

    public SiteGrades() { }

}
