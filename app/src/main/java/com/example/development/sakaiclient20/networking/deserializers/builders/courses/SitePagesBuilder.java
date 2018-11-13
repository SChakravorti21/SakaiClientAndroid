package com.example.development.sakaiclient20.networking.deserializers.builders.courses;

import com.example.development.sakaiclient20.networking.deserializers.builders.AbstractBuilder;
import com.example.development.sakaiclient20.persistence.entities.SitePage;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Development on 8/5/18.
 */

public class SitePagesBuilder extends AbstractBuilder<JsonArray, List<SitePage>> {

    private String assignmentSitePageUrl;

    SitePagesBuilder(JsonArray jsonArray) {
        super(jsonArray);
    }

    @Override
    public AbstractBuilder<JsonArray, List<SitePage>> build() {
        result = new ArrayList<>();

        for (int index = 0; index < source.size(); index++) {
            JsonObject json = source.get(index).getAsJsonObject();
            SitePage sitePage = buildSitePage(json);
            result.add(sitePage);

            if(sitePage.title.toLowerCase().contains("assignment")) {
                this.assignmentSitePageUrl = sitePage.url;
            }
        }

        return this;
    }

    private SitePage buildSitePage(JsonObject json) {
        return new SitePage(
                json.get("id").getAsString(),
                json.get("title").getAsString(),
                json.get("url").getAsString()
        );
    }

    public String getAssignmentSitePageUrl() {
        return this.assignmentSitePageUrl != null ? this.assignmentSitePageUrl : "";
    }
}
