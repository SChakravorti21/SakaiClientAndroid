package com.sakaimobile.development.sakaiclient20.ui.viewholders;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sakaimobile.development.sakaiclient20.R;
import com.unnamed.b.atv.model.TreeNode;

import androidx.appcompat.widget.LinearLayoutCompat;

public class ResourceItemViewHolder extends TreeNode.BaseNodeViewHolder<ResourceItemViewHolder.ResourceFileItem> {

    private static final String FILE_DOWNLOAD = "\uf381";

    public ResourceItemViewHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, ResourceFileItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.tree_node_resource_file, null, false);

        // Inflate the stepper that connects adjacent files
        FrameLayout stepperContainer = view.findViewById(R.id.stepper_container);
        View stepperView;
        if(value.isFirstFile && node.isLastChild()) {
            stepperView = null;
        } else if(value.isFirstFile) {
            stepperView = inflater.inflate(R.layout.tree_node_resource_stepper_first, stepperContainer, false);
        } else if(node.isLastChild()) {
            stepperView = inflater.inflate(R.layout.tree_node_resource_stepper_last, stepperContainer, false);
        } else {
            stepperView = inflater.inflate(R.layout.tree_node_resource_stepper_middle, stepperContainer, false);
        }

        if(stepperView != null)
            stepperContainer.addView(stepperView);

        TextView txt = view.findViewById(R.id.resource_file_txt);
        txt.setText(value.fileName);

        TextView downloadIcon = view.findViewById(R.id.download_icon);
        downloadIcon.setText(FILE_DOWNLOAD);

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
        String fileName;
        public String url;
        boolean isFirstFile;

        public ResourceFileItem(String fileName, String url, boolean isFirstFile) {
            this.fileName = fileName;
            this.url = url;
            this.isFirstFile = isFirstFile;
        }
    }

}
