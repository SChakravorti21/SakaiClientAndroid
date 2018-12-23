package com.sakaimobile.development.sakaiclient20.models.sakai.resources;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Resource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ResourcesResponse implements Serializable {

    @SerializedName("content_collection")
    @Expose
    private List<Resource> resources = new ArrayList<>();

    public List<Resource> getResources() {
        return this.resources;
    }
}
