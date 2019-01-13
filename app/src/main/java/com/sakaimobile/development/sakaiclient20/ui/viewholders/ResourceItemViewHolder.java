package com.sakaimobile.development.sakaiclient20.ui.viewholders;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.TypedValue;
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

        // Need to programmatically define the width as being the device
        // screen width since there was no container that we could inflate the
        // view relative to.
        Resources resources = inflater.getContext().getResources();

        // Convert pixels to density-independent units
        int widthPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                resources.getDisplayMetrics().widthPixels,
                resources.getDisplayMetrics()
        );

        view.setLayoutParams(new LinearLayoutCompat.LayoutParams(
                widthPx,
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT
        ));

        // set padding on the view to make it look like a nested structure
        int paddingLeft = ResourceDirectoryViewHolder.getPaddingForTreeNode(node, resources);
        view.setPadding(paddingLeft,
                view.getPaddingTop(),
                view.getPaddingRight(),
                view.getPaddingBottom());

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
