package com.example.development.sakaiclientandroid.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.development.sakaiclientandroid.R;

import java.util.HashMap;
import java.util.List;

public class SiteCollectionsExpandableListAdapter extends BaseExpandableListAdapter {


    private Context context;
    private List<String> headerTitles;
    private HashMap<String, List<String>> childTitles;
    private HashMap<String, List<Integer>> subjectCodePerChild;

    /**
     * Constructor for the custom expandable list adapter. Pretty self explanatory...
     * @param c = activity context
     * @param headerTitles = List of header titles (in this case Terms)
     * @param childTitles = HashMap mapping header titles to a list of class titles
     * @param subjectCodePerChild = HashMap mapping header titles to a list of subject codes, which corresponds to the child titles list
     */
    public SiteCollectionsExpandableListAdapter(Context c, List<String> headerTitles, HashMap<String, List<String>> childTitles, HashMap<String, List<Integer>> subjectCodePerChild) {

        this.context = c;
        this.headerTitles = headerTitles;
        this.childTitles = childTitles;
        this.subjectCodePerChild = subjectCodePerChild;
    }


    @Override
    public int getGroupCount() {
        return this.headerTitles.size();
    }

    @Override
    public int getChildrenCount(int groupPos) {

        return this.childTitles.get(this.headerTitles.get(groupPos)).size();
    }

    /**
     * @param groupPos
     * @return Header title of that group
     */
    public String getGroup(int groupPos) {
        return this.headerTitles.get(groupPos);
    }

    /**
     * @param groupPos
     * @param childPos
     * @return Child title of the given child in the given group
     */
    public String getChild(int groupPos, int childPos) {
        return this.childTitles.get(this.headerTitles.get(groupPos)).get(childPos);
    }

    /**
     * Returns subject code of a site collection so that the correct icon can be displayed
     * @param groupPos
     * @param childPos
     * @return Rutgers University subject code (ex 198 = computer science)
     */
    public int getChildSubjectCode(int groupPos, int childPos) {
        return this.subjectCodePerChild.get(this.headerTitles.get(groupPos)).get(childPos);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int groupPos, int childPos) {
        return childPos;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    /**
     * Displays the headers of the expandable list view (ex. Summer 2017)
     *
     * Inflates the list group layout and sets the text to the corresponding header
     *
     * @param groupPos
     * @param isExpanded
     * @param convertView
     * @param parent
     * @return the view that was inflated
     */
    @Override
    public View getGroupView(int groupPos, boolean isExpanded, View convertView, ViewGroup parent) {

        String headerTitle = getGroup(groupPos);

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.course_list_group, null);
        }

        TextView labelHeader = convertView.findViewById(R.id.lblListHeader);
        labelHeader.setTypeface(null, Typeface.BOLD);
        labelHeader.setText(headerTitle);

        return convertView;
    }


    /**
     * Displays each child label (ex. Intro to Computer Science)
     *
     * Inflates the list item layout which contains the class name and its relevant icon
     * The subject code is gotten from the HashMap of subjectCodePerChild, which was
     * passed into this adapter when created.
     *
     * Then the setSiteIcon method is called which takes a subject code and an image view
     * and sets the image view's resource to the correct icon.
     *
     * @param groupPos
     * @param childPos
     * @param isLastChild
     * @param convertView
     * @param parent
     * @return the list item view that was inflated.
     */
    @Override
    public View getChildView(int groupPos, int childPos, boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = getChild(groupPos, childPos);

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.course_list_item, null);
        }

        TextView textListChild = convertView.findViewById(R.id.lblListItemText);
        textListChild.setText(childText);

        ImageView imageView = convertView.findViewById(R.id.lblListItemImage);


        int subjectCode = getChildSubjectCode(groupPos, childPos);
        setSiteIcon(imageView, subjectCode);

        return convertView;
    }


    /**
     * Sets the image resource of an image view depending on the rutgers university
     * subject code that is given. These icons are there to make the app more lively.
     *
     * @param imageView = imageView to set the resource of
     * @param subjectCode = subject code of the class, so that the correct icon can be chosen
     */
    public void setSiteIcon(ImageView imageView, int subjectCode) {

        switch(subjectCode) {
            case 13:
                imageView.setImageResource(R.drawable.ic_language);
                break;
            case 80:
                imageView.setImageResource(R.drawable.ic_art);
                break;
            case 81:
                imageView.setImageResource(R.drawable.ic_art);
                break;
            case 160:
                imageView.setImageResource(R.drawable.ic_chemistry);
                break;
            case 198:
                imageView.setImageResource(R.drawable.ic_computer);
                break;
            case 220:
                imageView.setImageResource(R.drawable.ic_economics);
                break;
            case 420:
                imageView.setImageResource(R.drawable.ic_language);
                break;
            case 447:
                imageView.setImageResource(R.drawable.ic_genetics);
                break;
            case 640:
                imageView.setImageResource(R.drawable.ic_math);
                break;
            case 700:
                imageView.setImageResource(R.drawable.ic_music);
                break;
            case 750:
                imageView.setImageResource(R.drawable.ic_physics);
                break;

        }
    }

    @Override
    public boolean isChildSelectable(int groupPos, int childPos) {
        return true;
    }
}
