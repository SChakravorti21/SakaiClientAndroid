package com.sakaimobile.development.sakaiclient20.networking.deserializers

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.sakaimobile.development.sakaiclient20.persistence.entities.Attachment

import java.lang.reflect.Type

class AttachmentDeserializer : JsonDeserializer<Attachment> {
    @Throws(JsonParseException::class)
    override fun deserialize(raw: JsonElement,
                             typeOfT: Type,
                             context: JsonDeserializationContext): Attachment {

        val json = raw.asJsonObject

        val url = json.getStringMember("url")
        val attachment = Attachment(url!!)
        attachment.name = json.getStringMember("name")

        return attachment
    }
}
