package com.sakaimobile.development.sakaiclient20.ui.viewholders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.sakaimobile.development.sakaiclient20.R;
import com.unnamed.b.atv.model.TreeNode;

public class ResourceDirectoryViewHolder extends TreeNode.BaseNodeViewHolder<ResourceDirectoryViewHolder.ResourceDirectoryItem> {

    public ResourceDirectoryViewHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, ResourceDirectoryItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.tree_node_resource_dir, null, false);

        TextView txt = view.findViewById(R.id.resource_dir_txt);
        txt.setText(value.dirName);

        return view;
    }

    public static class ResourceDirectoryItem {
        public String dirName;

        public ResourceDirectoryItem(String dirName) {
            this.dirName = dirName;
        }
    }
}
