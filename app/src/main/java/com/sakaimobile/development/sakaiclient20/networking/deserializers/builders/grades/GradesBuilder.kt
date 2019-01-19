package com.sakaimobile.development.sakaiclient20.networking.deserializers.builders.grades

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.sakaimobile.development.sakaiclient20.networking.deserializers.builders.AbstractBuilder
import com.sakaimobile.development.sakaiclient20.networking.deserializers.getStringMember
import com.sakaimobile.development.sakaiclient20.persistence.entities.Grade

import java.util.ArrayList

/**
 * Builds the list of grade objects that belong to each site (contained in getting grades response)
 *
 */
class GradesBuilder(val src: JsonArray, private val siteId: String)
    : AbstractBuilder<JsonArray, List<Grade>>(src) {

    override fun build(): AbstractBuilder<JsonArray, List<Grade>> {

        result = source.map { element ->
            val gradeJson = element.asJsonObject
            val grade = gradeJson.getStringMember("grade")

            val pointsIsNull = gradeJson.get("points").isJsonNull
            val points = if (!pointsIsNull) gradeJson.get("points").asDouble else 0.0

            val itemName = gradeJson.getStringMember("itemName")

            // Last line in map returns the object to map to
            Grade(siteId, itemName, grade, points)
        }

        return this
    }
}
