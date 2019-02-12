package com.sakaimobile.development.sakaiclient20.networking.deserializers.builders.courses

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.sakaimobile.development.sakaiclient20.networking.deserializers.builders.AbstractBuilder
import com.sakaimobile.development.sakaiclient20.networking.deserializers.getStringMember
import com.sakaimobile.development.sakaiclient20.persistence.entities.SitePage

import java.util.ArrayList

/**
 * Created by Development on 8/5/18.
 */

class SitePagesBuilder(jsonArray: JsonArray) : AbstractBuilder<JsonArray, List<SitePage>>(jsonArray) {

    var assignmentSitePageUrl: String? = null
        get() { return field ?: "" }
        private set

    override fun build(): AbstractBuilder<JsonArray, List<SitePage>> {
        result = source.map { element ->
            val json = element.asJsonObject
            val sitePage = SitePage(json.getStringMember("id"),
                                    json.getStringMember("siteId"),
                                    json.getStringMember("title"),
                                    // default url of null ensures that UI will not show
                                    // the site page if the url is not provided
                                    json.getStringMember("url", default = null))

            // The assignment site page URL is used to initialize the submission
            // dialog WebView
            if (sitePage.title.toLowerCase().contains("assignment")
                    && this.assignmentSitePageUrl == null)
                this.assignmentSitePageUrl = sitePage.url

            sitePage
        }

        return this
    }
}
