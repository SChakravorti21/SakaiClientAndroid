package com.sakaimobile.development.sakaiclient20.networking.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Resource;

import java.lang.reflect.Type;

public class ResourceDeserializer implements JsonDeserializer<Resource> {

    private static final String COLLECTION_STRING = "collection";

    @Override
    public Resource deserialize(JsonElement raw, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonObject json = raw.getAsJsonObject();

        String url = json.get("url").getAsString();
        Resource resource = new Resource(url);

        // SiteId is not set directly after deserializing because
        // the siteId is not present in the json response
        // check resource repository.refreshSiteResources()

        resource.numChildren = json.get("numChildren").getAsInt();
        resource.container = json.get("container").getAsString();
        resource.title = json.get("title").getAsString();
        resource.type = json.get("type").getAsString();

        if(resource.type.equals(COLLECTION_STRING)) {
            resource.isDirectory = true;
            resource.numDescendants = json.get("size").getAsInt();
        } else {
            resource.isDirectory = false;
            resource.numDescendants = 0;
        }

        return resource;
    }
}
