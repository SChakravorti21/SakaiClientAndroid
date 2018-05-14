package com.example.development.sakaiclientandroid.api_models.all_sites;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class AllSites implements Serializable
{

    @SerializedName("entityPrefix")
    @Expose
    private String entityPrefix;
    @SerializedName("site_collection")
    @Expose
    private List<SiteCollectionAPI> siteCollectionAPI = new ArrayList<SiteCollectionAPI>();
    private final static long serialVersionUID = 6697315883558393879L;

    public String getEntityPrefix() {
        return entityPrefix;
    }

    public void setEntityPrefix(String entityPrefix) {
        this.entityPrefix = entityPrefix;
    }

    public List<SiteCollectionAPI> getSiteCollectionAPI() {
        return siteCollectionAPI;
    }

    public void setSiteCollectionAPI(List<SiteCollectionAPI> siteCollectionAPI) {
        this.siteCollectionAPI = siteCollectionAPI;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("entityPrefix", entityPrefix).append("siteCollectionAPI", siteCollectionAPI).toString();
    }

}
