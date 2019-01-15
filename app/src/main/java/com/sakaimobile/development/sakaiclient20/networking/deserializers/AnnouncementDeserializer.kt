package com.sakaimobile.development.sakaiclient20.networking.deserializers

import com.google.gson.JsonArray
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.sakaimobile.development.sakaiclient20.persistence.entities.Announcement
import com.sakaimobile.development.sakaiclient20.persistence.entities.Attachment

import java.lang.reflect.Type

class AnnouncementDeserializer : JsonDeserializer<Announcement> {

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Announcement {

        val jsonObject = json.asJsonObject
        val id = jsonObject.getStringMember("announcementId")!!
        val announcement = Announcement(id)

        announcement.createdOn = jsonObject.get("createdOn").asLong
        announcement.createdBy = jsonObject.getStringMember("createdByDisplayName")

        announcement.body = jsonObject.getStringMember("body")
        announcement.title = jsonObject.getStringMember("title")
        announcement.siteId = jsonObject.getStringMember("siteId")

        announcement.attachments = jsonObject.get("attachments").asJsonArray.map { attachmentObject ->
            val attachment = context.deserialize<Attachment>(attachmentObject, Attachment::class.java)
            attachment.announcementId = announcement.announcementId
            attachment
        }

        return announcement

    }
}
