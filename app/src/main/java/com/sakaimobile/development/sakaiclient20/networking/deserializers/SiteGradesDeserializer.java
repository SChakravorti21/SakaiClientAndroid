package com.sakaimobile.development.sakaiclient20.networking.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.sakaimobile.development.sakaiclient20.models.sakai.gradebook.SiteGrades;
import com.sakaimobile.development.sakaiclient20.networking.deserializers.builders.grades.SiteGradesBuilder;

import java.lang.reflect.Type;

/**
 * Deserializes the Json object into a pojo by delegating to the site grades builder
 */
public class SiteGradesDeserializer implements JsonDeserializer<SiteGrades> {

    @Override
    public SiteGrades deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        return new SiteGradesBuilder(json.getAsJsonObject())
                .build()
                .getResult();
    }
}
