package com.example.development.sakaiclientandroid.models;

import org.json.JSONObject;

public class SitePage {

    private String url;
    private String title;
    private String siteId;

    public SitePage(JSONObject jsonObj) {

        try {
            this.siteId = jsonObj.getString("id");
            this.title = jsonObj.getString("title");
            this.url = jsonObj.getString("url");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getId() {
        return siteId;
    }

    public String getTitle() {
        return title;
    }

    public String getSiteId() {
        return siteId;
    }

    @Override
    public String toString() {
        return (this.title + ", " + this.siteId);
    }
}
