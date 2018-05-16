package com.example.development.sakaiclientandroid.models;

import android.util.Log;

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
    private int subjectCode;



    public SiteCollection(SiteCollectionObject siteCollectionObject) {


        this.title = siteCollectionObject.getTitle();
        this.description = siteCollectionObject.getDescription();
        this.id = siteCollectionObject.getId();

        PropsObject propsObject = siteCollectionObject.getPropsObject();

        if(propsObject != null && propsObject.getTermEid() != null) {
            this.term = new Term(propsObject.getTermEid());
        }
        else {
            this.term = new Term("0000:0");
        }



        SiteOwnerObject siteOwnerAPI = siteCollectionObject.getSiteOwnerObject();
        this.siteOwner = (siteOwnerAPI != null) ? siteOwnerAPI.getUserDisplayName() : "None";

        //converts each sitePageAPI object into a SitePage object by getting rid of
        //useless information
        ArrayList<SitePage> sitePages = new ArrayList<>();

        for(SitePageObject page : siteCollectionObject.getSitePageObjects()) {
            sitePages.add(new SitePage(page));
        }

        this.sitePages = sitePages;


        String providerGroupId = siteCollectionObject.getProviderGroupId();
        if(providerGroupId != null) {

            providerGroupId = providerGroupId.replace("+", "_delim_");

            try {
                String courseCode = providerGroupId.split("_delim_")[0];
                String subjectCode = courseCode.split(":")[3];
                this.subjectCode = Integer.parseInt(subjectCode);
            } catch (Exception e) {
                e.printStackTrace();
                this.subjectCode = -1;
            }
        }

    }


    public static ArrayList<SiteCollection> convertApiToSiteCollection(List<SiteCollectionObject> siteCollectionAPIS) {

        ArrayList<SiteCollection> siteCollections = new ArrayList<>();
        for(SiteCollectionObject siteAPI : siteCollectionAPIS) {
            siteCollections.add(new SiteCollection(siteAPI));
        }

        return siteCollections;

    }

    @Override
    public String toString() {
        String ret = (this.title + " : " + this.term + "     Sites:   ");
        for(SitePage s : this.sitePages) {
            ret += s.toString() + ";  ";
        }

        return ret;

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

    public String getSiteOwner() {
        return siteOwner;
    }

    public int getSubjectCode() {
        return subjectCode;
    }


}
