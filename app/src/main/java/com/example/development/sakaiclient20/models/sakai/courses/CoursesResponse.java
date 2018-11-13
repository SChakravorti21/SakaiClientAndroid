package com.example.development.sakaiclient20.models.sakai.courses;

import com.example.development.sakaiclient20.persistence.entities.Course;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class CoursesResponse {

    @SerializedName("site_collection")
    @Expose
    private List<Course> courses = new ArrayList<>();

    public List<Course> getCourses() {
        return courses;
    }
}
