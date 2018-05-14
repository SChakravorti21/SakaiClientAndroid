package com.example.development.sakaiclientandroid.models;

import com.example.development.sakaiclientandroid.api_models.all_sites.SitePageObject;

public class SitePage {

    private String id;
    private String title;
    private String siteId;

    public SitePage(SitePageObject sitePageObject) {
        this.id = sitePageObject.getId();
        this.title = sitePageObject.getTitle();
        this.siteId = sitePageObject.getSiteId();
    }
}
