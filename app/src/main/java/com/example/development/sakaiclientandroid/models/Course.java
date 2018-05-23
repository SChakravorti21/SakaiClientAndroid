package com.example.development.sakaiclientandroid.models;

import com.example.development.sakaiclientandroid.api_models.gradebook.AssignmentObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Course {


    private String id;
    private String title;
    private String description;
    private Term term;
    private ArrayList<SitePage> sitePages;
    private String siteOwner;
    private int subjectCode;
    private List<AssignmentObject> assignmentObjectList;


    public Course(JSONObject jsonObject) {

        try {

            String id = jsonObject.getString("id");
            this.setId(id);

            String desc = jsonObject.getString("description");
            this.setDescription(desc);

            String title = jsonObject.getString("title");
            this.setTitle(capitalizeEveryWord(title));

            JSONObject props = jsonObject.getJSONObject("props");
            try {
                String term_eid = props.getString("term_eid");
                Term courseTerm = new Term(term_eid);
                this.setTerm(courseTerm);
            }
            catch(JSONException e) {
                Term courseTerm = new Term("0000:0");
                this.setTerm(courseTerm);
            }

            JSONObject siteOwner = jsonObject.getJSONObject("siteOwner");
            String ownerName = siteOwner.getString("userDisplayName");
            this.setSiteOwner(ownerName);


            String providerGroupId = jsonObject.getString("providerGroupId");
            if (!providerGroupId.equals("null")) {

                providerGroupId = providerGroupId.replace("+", "_delim_");

                String courseCode = providerGroupId.split("_delim_")[0];
                String subjectCode = courseCode.split(":")[3];
                this.setSubjectCode(Integer.parseInt(subjectCode));

            }


            ArrayList<SitePage> sitePages = new ArrayList<>();

            JSONArray sitePagesObj = jsonObject.getJSONArray("sitePages");
            for (int j = 0; j < sitePagesObj.length(); j++) {
                JSONObject pageObj = sitePagesObj.getJSONObject(j);
                SitePage sitePage = new SitePage(pageObj);
                sitePages.add(sitePage);
            }
            this.sitePages = sitePages;


            this.assignmentObjectList = null;

        }
        catch(Exception e) {
            e.printStackTrace();
        }

    }


    public String capitalizeEveryWord(String org) {
        org = org.toLowerCase();
        String[] words = org.split(" ");

        StringBuilder builder = new StringBuilder(org.length());
        for(int i = 0; i < words.length; i++) {
            builder.append(words[i].substring(0, 1).toUpperCase() + words[i].substring(1));


            if(i != words.length - 1)
                builder.append(" ");

        }

        return builder.toString();

    }


    @Override
    public String toString() {
        String ret = (this.title + " : " + this.term.toString() + "     Sites:   ");
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

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    public void setSitePages(ArrayList<SitePage> sitePages) {
        this.sitePages = sitePages;
    }

    public void setSiteOwner(String siteOwner) {
        this.siteOwner = siteOwner;
    }

    public void setSubjectCode(int subjectCode) {
        this.subjectCode = subjectCode;
    }

    public List<AssignmentObject> getAssignmentObjectList() {
        return assignmentObjectList;
    }

    public void setAssignmentObjectList(List<AssignmentObject> assignmentObjectList) {
        this.assignmentObjectList = assignmentObjectList;
    }


}
