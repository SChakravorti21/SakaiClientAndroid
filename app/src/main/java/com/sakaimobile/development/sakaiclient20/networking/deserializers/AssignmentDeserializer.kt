package com.sakaimobile.development.sakaiclient20.networking.deserializers

import com.google.gson.JsonArray
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.sakaimobile.development.sakaiclient20.persistence.entities.Assignment
import com.sakaimobile.development.sakaiclient20.persistence.entities.Attachment

import java.lang.reflect.Type
import java.util.Date

class AssignmentDeserializer : JsonDeserializer<Assignment> {

    @Throws(JsonParseException::class)
    override fun deserialize(raw: JsonElement,
                             typeOfT: Type,
                             context: JsonDeserializationContext): Assignment {

        val json = raw.asJsonObject

        val assignmentId = json.getStringMember("entityId")
        val assignment = Assignment(assignmentId)

        assignment.title = json.getStringMember("title")
        assignment.siteId = json.getStringMember("context")
        assignment.instructions = json.getStringMember("instructions")

        assignment.entityURL = json.getStringMember("entityURL")
        assignment.entityTitle = json.getStringMember("entityTitle")
        assignment.entityReference = json.getStringMember("entityReference")

        assignment.status = json.getStringMember("status")

        val dueTimeMilliseconds = json.get("dueTime").asJsonObject.get("time").asLong
        assignment.dueTime = Date(dueTimeMilliseconds)
        assignment.allowResubmission = json.get("allowResubmission").asBoolean

        assignment.creator = json.getStringMember("creator")
        assignment.authorLastModified = json.getStringMember("authorLastModified")

        assignment.gradeScale = json.getStringMember("gradeScale")
        assignment.gradeScaleMaxPoints = json.getStringMember("gradeScaleMaxPoints", default = null)

        assignment.attachments = json.get("attachments").asJsonArray.map { attachmentObject ->
            val attachment = context.deserialize<Attachment>(attachmentObject, Attachment::class.java)
            attachment.assignmentId = assignment.assignmentId
            attachment
        }

        return assignment
    }
}
