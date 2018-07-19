package com.example.development.sakaiclientandroid.models;

import com.example.development.sakaiclientandroid.api_models.assignments.Assignment;
import com.example.development.sakaiclientandroid.R;
import com.example.development.sakaiclientandroid.api_models.assignments.AssignmentObject;
import com.example.development.sakaiclientandroid.api_models.gradebook.GradebookObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Course implements Serializable {


    private String id;
    private String title;
    private String description;
    private Term term;
    private ArrayList<SitePage> sitePages;
    private String siteOwner;
    private int subjectCode;
    private List<GradebookObject> gradebookObjectList;
    private List<Assignment> assignmentList;

    public Course(JSONObject jsonObject) throws JSONException {
        this.assignmentList = new ArrayList<Assignment>();

        String id = jsonObject.getString("id");
        this.setId(id);

        String desc = jsonObject.getString("description");
        this.setDescription(desc);

        String title = jsonObject.getString("title");
        this.setTitle(title);

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

        this.gradebookObjectList = null;

    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeObject(this);
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        Course course = (Course) in.readObject();
        this.assignmentList = course.assignmentList;
        this.title = course.title;
        this.term = course.term;
    }


    @Override
    public String toString() {
        String ret = (this.title + " : " + this.term.toString() + "     Sites:   ");
        for (SitePage s : this.sitePages) {
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

    public List<GradebookObject> getGradebookObjectList() {
        return gradebookObjectList;
    }

    public void setGradebookObjectList(List<GradebookObject> gradebookObjectList) {
        this.gradebookObjectList = gradebookObjectList;
    }

    public List<Assignment> getAssignmentList() {
        return this.assignmentList;
    }

    public void addAssignment(Assignment assignment) {
        assignmentList.add(assignment);
    }

    public int getNumAssignments() {
        return (this.assignmentList != null) ? this.assignmentList.size() : 0;
    }

}
