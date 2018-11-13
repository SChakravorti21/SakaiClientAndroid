package com.example.development.sakaiclient20.networking.deserializers;

import com.example.development.sakaiclient20.persistence.entities.Assignment;
import com.example.development.sakaiclient20.persistence.entities.Attachment;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Date;

public class AssignmentDeserializer implements JsonDeserializer<Assignment> {

    @Override
    public Assignment deserialize(JsonElement raw,
                                  Type typeOfT,
                                  JsonDeserializationContext context)
            throws JsonParseException {


        JsonObject json = raw.getAsJsonObject();

        // TODO: Once the course to term map is created, set the assignment's term
        String assignmentId = json.get("entityId").getAsString();
        Assignment assignment = new Assignment(assignmentId);

        assignment.title = json.get("title").getAsString();
        assignment.siteId = json.get("context").getAsString();
        assignment.instructions = json.get("instructions").getAsString();

        assignment.entityURL = json.get("entityURL").getAsString();
        assignment.entityTitle = json.get("entityTitle").getAsString();
        assignment.entityReference = json.get("entityReference").getAsString();

        assignment.status = json.get("status").getAsString();
        long dueTimeMilliseconds = json.get("dueTime")
                                        .getAsJsonObject()
                                        .get("time").getAsLong();

        assignment.dueTime = new Date(dueTimeMilliseconds);
        assignment.allowResubmission = json.get("allowResubmission").getAsBoolean();

        assignment.creator = json.get("creator").getAsString();
        assignment.authorLastModified = json.get("authorLastModified").getAsString();

        assignment.gradeScale = json.get("gradeScale").getAsString();
        JsonElement gradescaleElement = json.get("gradeScaleMaxPoints");
        assignment.gradeScaleMaxPoints = gradescaleElement instanceof JsonNull
                ? null
                : gradescaleElement.getAsString();

        JsonArray attachments = json.get("attachments").getAsJsonArray();
        for(int i = 0; i < attachments.size(); i++) {
            JsonObject attachmentObject = attachments.get(i).getAsJsonObject();
            Attachment attachment = context.deserialize(attachmentObject, Attachment.class);
            attachment.assignmentId = assignment.assignmentId;
            assignment.attachments.add(attachment);
        }

        return assignment;
    }
}
