package com.example.development.sakaiclientandroid.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.development.sakaiclientandroid.R;

import java.util.HashMap;
import java.util.List;

public class ChildExpListAdapter extends BaseExpandableListAdapter {

    private final Context context;
    private final List<String> parentHeaders;
    private final HashMap<String, List<String>> mapParentToChild;


    public ChildExpListAdapter(Context c, List<String> parentHeaders, HashMap<String, List<String>> mapParentToChild) {

        this.context = c;
        this.parentHeaders = parentHeaders;
        this.mapParentToChild = mapParentToChild;
    }


    @Override
    public int getGroupCount() {
        return this.parentHeaders.size();
    }

    @Override
    public int getChildrenCount(int i) {
        try {
            return this.mapParentToChild.get(this.parentHeaders.get(i)).size();
        }
        catch(Exception e) {
            return 0;
        }
    }

    @Override
    public Object getGroup(int i) {
        return this.parentHeaders.get(i);
    }

    @Override
    public Object getChild(int groupPos, int childPos) {
        return this.mapParentToChild.get(this.parentHeaders.get(groupPos)).get(childPos);
    }

    @Override
    public long getGroupId(int i) {
        return 0;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPos, boolean isExpandable, View convertView, ViewGroup parent) {

        final String groupText = (String) getGroup(groupPos);

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.course_list_item_home, parent, false);
        }


        TextView headerText = convertView.findViewById(R.id.course_name_text);
        headerText.setText(groupText);

        ImageView image = convertView.findViewById(R.id.course_icon);
        image.setImageResource(R.drawable.ic_chemistry);
        return convertView;

    }

    @Override
    public View getChildView(int groupPos, int childPos, boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPos, childPos);

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.grade_list_item_site, parent, false);
        }

        TextView assignmentText = convertView.findViewById(R.id.txt_assignment_name);
        assignmentText.setText(childText);
        return convertView;

    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
