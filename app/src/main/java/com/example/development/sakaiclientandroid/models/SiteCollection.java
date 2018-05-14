package com.example.development.sakaiclientandroid.models;

import com.example.development.sakaiclientandroid.api_models.all_sites.PropsObject;
import com.example.development.sakaiclientandroid.api_models.all_sites.SiteCollectionObject;
import com.example.development.sakaiclientandroid.api_models.all_sites.SiteOwnerObject;
import com.example.development.sakaiclientandroid.api_models.all_sites.SitePageObject;

import java.util.ArrayList;
import java.util.List;

public class SiteCollection {

    private String id;
    private String title;
    private String description;
    private Term term;
    private ArrayList<SitePage> sitePages;
    private String siteOwner;


    public SiteCollection(SiteCollectionObject siteCollectionObject) {

        this.title = siteCollectionObject.getTitle();
        this.description = siteCollectionObject.getDescription();
        this.id = siteCollectionObject.getId();

        PropsObject propsObject = siteCollectionObject.getPropsObject();
        this.term = (propsObject != null) ? new Term(propsObject.getTermEid()) : null;


        SiteOwnerObject siteOwnerAPI = siteCollectionObject.getSiteOwnerObject();
        this.siteOwner = (siteOwnerAPI != null) ? siteOwnerAPI.getUserDisplayName() : "None";

        //converts each sitePageAPI object into a SitePage object by getting rid of
        //useless information
        ArrayList<SitePage> sitePages = new ArrayList<>();
        for(SitePageObject page : siteCollectionObject.getSitePageObjects()) {
            sitePages.add(new SitePage(page));
        }

        this.sitePages = sitePages;

    }


    public static ArrayList<SiteCollection> convertApiToSiteCollection(List<SiteCollectionObject> siteCollectionAPIS) {

        ArrayList<SiteCollection> siteCollections = new ArrayList<>();
        for(SiteCollectionObject siteAPI : siteCollectionAPIS) {
            siteCollections.add(new SiteCollection(siteAPI));
        }

        return siteCollections;

    }


    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Term getTerm() {
        return term;
    }

    public ArrayList<SitePage> getSitePages() {
        return sitePages;
    }


}
