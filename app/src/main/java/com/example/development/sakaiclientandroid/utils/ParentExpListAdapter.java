package com.example.development.sakaiclientandroid.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.development.sakaiclientandroid.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ParentExpListAdapter extends BaseExpandableListAdapter {

    private final Context context;
    private final List<String> grandparentHeaders;
    private final HashMap<String, List<String>> mapGrandparentToParent;
    private final HashMap<String, List<String>> mapParentToChild;


    public ParentExpListAdapter(Context c, List<String> grandparentHeaders) {

        this.context = c;
        this.grandparentHeaders = new ArrayList<>();
        this.grandparentHeaders.addAll(grandparentHeaders);

        String[] parent1 = {"par1", "par2"};
        String[] parent2 = {"par3"};
        String[] parent3 = {"par4", "par5", "par6"};

        this.mapGrandparentToParent = new HashMap<>();
        this.mapGrandparentToParent.put(grandparentHeaders.get(0), Arrays.asList(parent1));
        this.mapGrandparentToParent.put(grandparentHeaders.get(1), Arrays.asList(parent2));
        this.mapGrandparentToParent.put(grandparentHeaders.get(2), Arrays.asList(parent3));

        //now each item in the list of items in the parents list must have its own list
        this.mapParentToChild = new HashMap<>();
        String[] childs = {"child1", "child2"};
        this.mapParentToChild.put("par1", Arrays.asList(childs));
        this.mapParentToChild.put("par2", Arrays.asList(childs));
        this.mapParentToChild.put("par3", Arrays.asList(childs));
        this.mapParentToChild.put("par5", Arrays.asList(childs));
    }


    @Override
    public int getGroupCount() {
        return this.grandparentHeaders.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return 1;
    }

    @Override
    public Object getGroup(int i) {
        return this.grandparentHeaders.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return i1;
    }

    @Override
    public long getGroupId(int i) {
        return i;
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
    public View getGroupView(int groupPos, boolean isExpanded, View convertView, ViewGroup parent) {

        String header = (String) getGroup(groupPos);
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.term_list_group, parent, false);
        }

        TextView headerTxtView = convertView.findViewById(R.id.term_header_group);
        headerTxtView.setText(header);
        return convertView;
    }



    @Override
    public View getChildView(int groupPos, int childPos, boolean isLastChild, View convertView, ViewGroup parent) {

        final CustomParentListView parentListView = new CustomParentListView(this.context);
        String parentNode = (String) getGroup(groupPos);
        parentListView.setAdapter(new ChildExpListAdapter(this.context, mapGrandparentToParent.get(parentNode), mapParentToChild));

        return parentListView;

    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
