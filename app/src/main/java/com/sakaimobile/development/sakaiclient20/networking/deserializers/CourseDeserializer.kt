package com.sakaimobile.development.sakaiclient20.networking.deserializers

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.sakaimobile.development.sakaiclient20.networking.deserializers.builders.courses.CourseBuilder
import com.sakaimobile.development.sakaiclient20.persistence.entities.Course

import java.lang.reflect.Type

class CourseDeserializer : JsonDeserializer<Course> {
    @Throws(JsonParseException::class)
    override fun deserialize(
            json: JsonElement,
            typeOfT: Type,
            context: JsonDeserializationContext): Course {
        return CourseBuilder(json.asJsonObject)
                .build()
                .result
    }
}
