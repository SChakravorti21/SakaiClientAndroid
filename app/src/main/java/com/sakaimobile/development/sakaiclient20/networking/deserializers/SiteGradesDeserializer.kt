package com.sakaimobile.development.sakaiclient20.networking.deserializers

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.sakaimobile.development.sakaiclient20.models.sakai.gradebook.SiteGrades
import com.sakaimobile.development.sakaiclient20.networking.deserializers.builders.grades.SiteGradesBuilder

import java.lang.reflect.Type

/**
 * Deserializes the Json object into a pojo by delegating to the site grades builder
 */
class SiteGradesDeserializer : JsonDeserializer<SiteGrades> {

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): SiteGrades {

        return SiteGradesBuilder(json.asJsonObject)
                .build()
                .result
    }
}
