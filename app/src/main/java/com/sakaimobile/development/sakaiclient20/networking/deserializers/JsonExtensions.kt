package com.sakaimobile.development.sakaiclient20.networking.deserializers

import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject

fun JsonObject.getStringMember(key: String, default: String? = "") : String? {
    val element: JsonElement? = this.get(key)
    return if (element != null && element !is JsonNull) element.asString else default
}