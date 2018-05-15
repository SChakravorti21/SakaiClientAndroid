package com.example.development.sakaiclientandroid.api_models.all_sites;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

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


    /**
     * Takes the json string of the response from sakai and uses the JSONObject library to
     * get the json list of SitePages for each SiteCollection, and then uses Gson to convert this
     * json into an ArrayList of SitePageObject objects. This List is then added to the sitePageObject field
     * of the AllSitesApi class
     *
     * @param jsonBody json of the entire response of sitecollection objects
     */
    public void fillSitePages(String jsonBody) {

        Type type = new TypeToken<ArrayList<SitePageObject>>(){}.getType();
        Gson gson = new Gson();

        try {
            JSONObject obj = new JSONObject(jsonBody);
            JSONArray colls = obj.getJSONArray("site_collection");

            for (int i = 0; i < colls.length(); i++) {
                JSONObject collection = colls.getJSONObject(i);
                JSONArray sitePages = collection.getJSONArray("sitePages");
                String stringSitePages = sitePages.toString();

                ArrayList<SitePageObject> sites = gson.fromJson(stringSitePages, type);
                this.getSiteCollectionObject().get(i).setSitePageObjects(sites);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

}
