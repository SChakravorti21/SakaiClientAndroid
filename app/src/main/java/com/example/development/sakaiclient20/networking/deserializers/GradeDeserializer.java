package com.example.development.sakaiclient20.networking.deserializers;

import com.example.development.sakaiclient20.persistence.entities.Grade;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class GradeDeserializer implements JsonDeserializer<Grade> {


    @Override
    public Grade deserialize(JsonElement raw, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject json = raw.getAsJsonObject();

        Grade grade = new Grade();
        grade.grade = json.get("grade").getAsString();
        grade.itemName = json.get("itemName").getAsString();
        grade.points = json.get("points").getAsDouble();

        return grade;
    }
}
