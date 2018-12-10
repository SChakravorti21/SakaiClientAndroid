package com.example.development.sakaiclient20.networking.deserializers;

import com.example.development.sakaiclient20.persistence.entities.Announcement;
import com.example.development.sakaiclient20.persistence.entities.Attachment;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class AnnouncementDeserializer implements JsonDeserializer<Announcement> {


    @Override
    public Announcement deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonObject jsonObject = json.getAsJsonObject();
        String id = jsonObject.get("announcementId").getAsString();
        Announcement announcement = new Announcement(id);

        announcement.createdOn = jsonObject.get("createdOn").getAsLong();

        JsonElement createdByElement = jsonObject.get("createdByDisplayName");
        announcement.createdBy = createdByElement.isJsonNull() ?
                "" : createdByElement.getAsString();

        announcement.body = jsonObject.get("body").getAsString();
        announcement.title = jsonObject.get("title").getAsString();
        announcement.siteId = jsonObject.get("siteId").getAsString();

        JsonArray attachments = jsonObject.get("attachments").getAsJsonArray();
        for(int i = 0; i < attachments.size(); i++) {
            JsonObject attachmentObject = attachments.get(i).getAsJsonObject();
            Attachment attachment = context.deserialize(attachmentObject, Attachment.class);
            attachment.assignmentId = announcement.announcementId;
            announcement.attachments.add(attachment);
        }

        return announcement;

    }
}
