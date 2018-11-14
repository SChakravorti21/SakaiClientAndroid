package com.example.development.sakaiclient20.networking.deserializers.builders.grades;

import com.example.development.sakaiclient20.networking.deserializers.builders.AbstractBuilder;
import com.example.development.sakaiclient20.persistence.entities.Grade;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds the list of grade objects that belong to each site (contained in getting grades response)
 *
 */
public class GradesBuilder extends AbstractBuilder<JsonArray, List<Grade>> {

    private String siteId;

    public GradesBuilder(JsonArray src, String siteId) {
        super(src);
        this.siteId = siteId;
    }

    @Override
    public AbstractBuilder<JsonArray, List<Grade>> build() {

        result = new ArrayList<>();

        for(int i = 0; i < source.size(); i++) {
            JsonObject gradeJson = source.get(i).getAsJsonObject();

            boolean gradeIsNull = gradeJson.get("grade").isJsonNull();
            boolean pointsIsNull = gradeJson.get("points").isJsonNull();

            String grade = !gradeIsNull
                    ? gradeJson.get("grade").getAsString()
                    : "";
            double points = !pointsIsNull
                    ? gradeJson.get("points").getAsDouble()
                    : 0d;

            String itemName = gradeJson.get("itemName").getAsString();

            result.add(new Grade(siteId, itemName, grade, points));
        }

        return this;
    }
}
