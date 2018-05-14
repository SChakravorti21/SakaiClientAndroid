package com.example.development.sakaiclientandroid.models;

import com.example.development.sakaiclientandroid.api_models.all_sites.SitePageAPI;

public class SitePage {

    private String id;
    private String title;
    private String siteId;

    public SitePage(SitePageAPI sitePageAPI) {
        this.id = sitePageAPI.getId();
        this.title = sitePageAPI.getTitle();
        this.siteId = sitePageAPI.getSiteId();
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSiteId() {
        return siteId;
    }
}
