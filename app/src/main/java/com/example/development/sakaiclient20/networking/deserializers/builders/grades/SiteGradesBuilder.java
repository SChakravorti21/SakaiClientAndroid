package com.example.development.sakaiclient20.networking.deserializers.builders.grades;

import com.example.development.sakaiclient20.models.sakai.gradebook.SiteGrades;
import com.example.development.sakaiclient20.networking.deserializers.builders.AbstractBuilder;
import com.example.development.sakaiclient20.persistence.entities.Grade;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * We need a builder for site grades.
 * If we just made a custom serializer for a grade object, there would be no
 * way of getting the siteIds for each grade, so we must make a custom builder
 * to assign them to each grade
 *
 */
public class SiteGradesBuilder extends AbstractBuilder<JsonObject, SiteGrades> {

    public SiteGradesBuilder(JsonObject src) {
        super(src);
    }

    public SiteGradesBuilder build() {

        String siteId = source.get("siteId").getAsString();
        String siteName = source.get("siteName").getAsString();

        JsonArray assignments = source.get("assignments").getAsJsonArray();

        // build the list of grades for this site
        GradesBuilder gradesBuilder = new GradesBuilder(assignments, result.siteId);
        List<Grade> gradeList = gradesBuilder.build().getResult();

        result = new SiteGrades(siteId, siteName, gradeList);

        return this;
    }

}
