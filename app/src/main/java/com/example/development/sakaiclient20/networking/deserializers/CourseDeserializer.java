package com.example.development.sakaiclient20.networking.deserializers;

import com.example.development.sakaiclient20.networking.deserializers.builders.courses.CourseBuilder;
import com.example.development.sakaiclient20.persistence.entities.Course;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class CourseDeserializer implements JsonDeserializer<Course> {
    @Override
    public Course deserialize(
            JsonElement json,
            Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException {
        return new CourseBuilder(json.getAsJsonObject())
                .build()
                .getResult();
    }
}
