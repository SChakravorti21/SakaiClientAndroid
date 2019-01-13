package com.sakaimobile.development.sakaiclient20.networking.deserializers

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.sakaimobile.development.sakaiclient20.persistence.entities.Resource

import java.lang.reflect.Type

class ResourceDeserializer : JsonDeserializer<Resource> {

    companion object {
        private const val COLLECTION = "collection"
    }

    @Throws(JsonParseException::class)
    override fun deserialize(raw: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Resource {

        val json = raw.asJsonObject

        val url = json.getStringMember("url")
        val resource = Resource(url)

        // SiteId is not set directly after deserializing because
        // the siteId is not present in the json response
        // check resource repository.refreshSiteResources()

        resource.numChildren = json.get("numChildren").asInt
        resource.container = json.getStringMember("container")
        resource.title = json.getStringMember("title")
        resource.type = json.getStringMember("type")

        if (resource.type == COLLECTION) {
            resource.isDirectory = true
            resource.numDescendants = json.get("size").asInt
        } else {
            resource.isDirectory = false
            resource.numDescendants = 0
        }

        return resource
    }
}
