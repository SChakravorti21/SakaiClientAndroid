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

public class myExpandableListAdapter extends BaseExpandableListAdapter {


    private Context context;
    private List<String> headerTitles;
    private HashMap<String, List<String>> childTitles;
    private HashMap<String, List<Integer>> subjectCodePerChild;

    public myExpandableListAdapter(Context c, List<String> headerTitles, HashMap<String, List<String>> childTitles, HashMap<String, List<Integer>> subjectCodePerChild) {

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

        List<String> children = this.childTitles.get(this.headerTitles.get(groupPos));
        return (children == null) ? 0 : children.size();
    }

    @Override
    public Object getGroup(int groupPos) {
        return this.headerTitles.get(groupPos);
    }

    @Override
    public Object getChild(int groupPos, int childPos) {
        return this.childTitles.get(this.headerTitles.get(groupPos)).get(childPos);
    }

    public int getChildResId(int groupPos, int childPos) {
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

    @Override
    public View getGroupView(int groupPos, boolean isExpanded, View convertView, ViewGroup parent) {

        String headerTitle = (String) getGroup(groupPos);

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group, null);
        }

        TextView labelHeader = convertView.findViewById(R.id.lblListHeader);
        labelHeader.setTypeface(null, Typeface.BOLD);
        labelHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public View getChildView(int groupPos, int childPos, boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPos, childPos);

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, null);
        }

        TextView textListChild = convertView.findViewById(R.id.lblListItemText);
        textListChild.setText(childText);

        ImageView imageView = convertView.findViewById(R.id.lblListItemImage);
        imageView.setImageResource(R.drawable.ic_computer);

//        final int resId = getChildResId(groupPos, childPos);
//        imageView.setImageResource(resId);


        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
