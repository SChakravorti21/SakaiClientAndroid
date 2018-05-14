package com.example.development.sakaiclientandroid.api_models.all_sites;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class AllSitesAPI implements Serializable
{

    @SerializedName("entityPrefix")
    @Expose
    private String entityPrefix;
    @SerializedName("site_collection")
    @Expose
    private List<SiteCollectionObject> siteCollectionObject = new ArrayList<SiteCollectionObject>();
    private final static long serialVersionUID = 6697315883558393879L;

    public String getEntityPrefix() {
        return entityPrefix;
    }

    public void setEntityPrefix(String entityPrefix) {
        this.entityPrefix = entityPrefix;
    }

    public List<SiteCollectionObject> getSiteCollectionObject() {
        return siteCollectionObject;
    }

    public void setSiteCollectionObject(List<SiteCollectionObject> siteCollectionObject) {
        this.siteCollectionObject = siteCollectionObject;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("entityPrefix", entityPrefix).append("siteCollectionObject", siteCollectionObject).toString();
    }

}
