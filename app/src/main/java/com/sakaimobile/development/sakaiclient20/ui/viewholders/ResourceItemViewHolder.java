package com.sakaimobile.development.sakaiclient20.ui.viewholders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.sakaimobile.development.sakaiclient20.R;
import com.unnamed.b.atv.model.TreeNode;

public class ResourceItemViewHolder extends TreeNode.BaseNodeViewHolder<ResourceItemViewHolder.ResourceFileItem> {

    public ResourceItemViewHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, ResourceFileItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.tree_node_file, null, false);

        TextView txt = view.findViewById(R.id.resource_file_txt);
        txt.setText(value.fileName);

        return view;
    }

    public static class ResourceFileItem {
        public String fileName;
        public String url;

        public ResourceFileItem(String fileName, String url) {
            this.fileName = fileName;
            this.url = url;
        }
    }

}
