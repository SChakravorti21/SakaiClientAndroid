package com.sakaimobile.development.sakaiclient20.networking.deserializers.builders.grades

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.sakaimobile.development.sakaiclient20.models.sakai.gradebook.SiteGrades
import com.sakaimobile.development.sakaiclient20.networking.deserializers.builders.AbstractBuilder
import com.sakaimobile.development.sakaiclient20.networking.deserializers.getStringMember
import com.sakaimobile.development.sakaiclient20.persistence.entities.Grade

/**
 * We need a builder for site grades.
 * If we just made a custom serializer for a grade object, there would be no
 * way of getting the siteIds for each grade, so we must make a custom builder
 * to assign them to each grade
 *
 */
class SiteGradesBuilder(val src: JsonObject) : AbstractBuilder<JsonObject, SiteGrades>(src) {

    override fun build(): SiteGradesBuilder {

        val siteId = source.getStringMember("siteId")!!
        val siteName = source.getStringMember("siteName")
        val assignments = source.get("assignments").asJsonArray

        // build the list of grades for this site
        val gradesBuilder = GradesBuilder(assignments, siteId)
        val gradeList = gradesBuilder.build().result

        result = SiteGrades(siteId, siteName, gradeList)

        return this
    }

}
