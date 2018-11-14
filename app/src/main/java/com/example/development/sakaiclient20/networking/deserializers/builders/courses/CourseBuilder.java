package com.example.development.sakaiclient20.networking.deserializers.builders.courses;

import com.example.development.sakaiclient20.models.Term;
import com.example.development.sakaiclient20.networking.deserializers.builders.AbstractBuilder;
import com.example.development.sakaiclient20.persistence.entities.Course;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

/**
 * Created by Development on 8/5/18.
 */

public class CourseBuilder extends AbstractBuilder<JsonObject, Course> {

    public CourseBuilder(JsonObject jsonObject) {
        super(jsonObject);
    }

    @Override
    public CourseBuilder build() {
        // Initialize the course with the siteId
        result = new Course(source.get("id").getAsString());

        result.term = parseTerm();
        result.subjectCode = parseSubjectCode();
        result.title = source.get("title").getAsString();

        JsonElement desc = source.get("description");

        result.description = !desc.isJsonNull()
                ? desc.getAsString()
                : "";
        result.siteOwner = source.getAsJsonObject("siteOwner")
                .get("userDisplayName")
                .getAsString();

        JsonArray rawSitePages = source.getAsJsonArray("sitePages");
        SitePagesBuilder builder = new SitePagesBuilder(rawSitePages);
        result.sitePages = builder.build().getResult();
        result.assignmentSitePageUrl = builder.getAssignmentSitePageUrl();

        return this;
    }

    private Term parseTerm() {
        JsonObject props = source.getAsJsonObject("props");
        JsonElement termEidElement = props.get("term_eid");

        // It is possible that even if props is defined, term_eid
        // is not provided, in which case the element itself will
        // be null (instead of holding a null value and being JsonNull)
        if(termEidElement == null)
            return new Term("0000:0");

        String termEid = termEidElement.getAsString();
        return new Term(termEid);
    }

    private int parseSubjectCode() {
        JsonElement providerGroupIdElement = source.get("providerGroupId");
        if(providerGroupIdElement instanceof JsonNull)
            return 0;

        String providerGroupId = providerGroupIdElement.getAsString();
        providerGroupId = providerGroupId.replace("+", "_delim_");
        String courseCode = providerGroupId.split("_delim_")[0];
        String subjectCode = courseCode.split(":")[3];
        return Integer.parseInt(subjectCode);
    }
}
