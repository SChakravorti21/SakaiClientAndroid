package com.sakaimobile.development.sakaiclient20.networking.deserializers.builders.courses

import com.google.gson.JsonObject
import com.sakaimobile.development.sakaiclient20.models.Term
import com.sakaimobile.development.sakaiclient20.networking.deserializers.builders.AbstractBuilder
import com.sakaimobile.development.sakaiclient20.networking.deserializers.getStringMember
import com.sakaimobile.development.sakaiclient20.persistence.entities.Course

/**
 * Created by Development on 8/5/18.
 */

class CourseBuilder(jsonObject: JsonObject) : AbstractBuilder<JsonObject, Course>(jsonObject) {

    override fun build(): CourseBuilder {
        // Initialize the course with the siteId
        result = Course(source.getStringMember("id")!!)

        result.term = parseTerm()
        result.subjectCode = parseSubjectCode()
        result.title = source.getStringMember("title")

        result.description = source.getStringMember("description")
        result.siteOwner = source.getAsJsonObject("siteOwner")
                                 .getStringMember("userDisplayName")

        val rawSitePages = source.getAsJsonArray("sitePages")
        val builder = SitePagesBuilder(rawSitePages)
        result.sitePages = builder.build().result
        result.assignmentSitePageUrl = builder.getAssignmentSitePageUrl()

        return this
    }

    private fun parseTerm(): Term {
        // It is possible that even if props is defined, term_eid
        // is not provided, in which case the element itself will
        // be null (instead of holding a null value and being JsonNull)
        val props = source.getAsJsonObject("props")
        val termEid = props.getStringMember("term_eid", default = "0000:0")
        return Term(termEid)
    }

    private fun parseSubjectCode(): Int {
        val providerGroupId = source.getStringMember("providerGroupId", default = null)
                                    ?.replace("+", "_delim_")
                                    ?: return 0
        // dropLastWhile removes trailing empty strings, just good practice when splitting
        val courseCode = providerGroupId.split("_delim_".toRegex())
                                        .dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        val subjectCode = courseCode.split(":".toRegex())
                                    .dropLastWhile { it.isEmpty() }.toTypedArray()[3]
        return Integer.parseInt(subjectCode)
    }
}
