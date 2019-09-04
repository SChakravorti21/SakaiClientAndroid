package com.sakaimobile.development.sakaiclient20.ui.viewholders;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.sakaimobile.development.sakaiclient20.R;
import com.unnamed.b.atv.model.TreeNode;

import androidx.appcompat.widget.LinearLayoutCompat;

public class ResourceDirectoryViewHolder extends TreeNode.BaseNodeViewHolder<ResourceDirectoryViewHolder.ResourceDirectoryItem> {

    public static class ResourceDirectoryItem {
        String dirName;
        public ResourceDirectoryItem(String dirName) {
            this.dirName = dirName;
        }
    }

    private static final String FOLDER_OPEN = "\uf07c";
    private static final String FOLDER_CLOSED = "\uf07b";

    private static final int PADDING_DP = 28;
    private static final int MAX_HUE = 360;
    private static final int HUE_DIFF = 25;

    private TextView folderIcon;

    public ResourceDirectoryViewHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, ResourceDirectoryItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        Resources resources = inflater.getContext().getResources();
        View view = inflater.inflate(R.layout.tree_node_resource_dir, null, false);

        TextView directoryName = view.findViewById(R.id.resource_dir_txt);
        directoryName.setText(value.dirName);

        folderIcon = view.findViewById(R.id.folder_icon);
        folderIcon.setText(FOLDER_CLOSED);
        folderIcon.setTextColor(getColorForNode(node, resources));

        // Need to programmatically define the width as being the device
        // screen width since there was no container that we could inflate the
        // view relative to.

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

    @Override
    public void toggle(boolean active) {
        folderIcon.setText( active ? FOLDER_OPEN : FOLDER_CLOSED );
    }

    // gets the padding required for a given treenode
    public static int getPaddingForTreeNode(TreeNode node, Resources resources) {
        int paddingDp = PADDING_DP * node.getLevel();
        float density = resources.getDisplayMetrics().density;
        return (int)(paddingDp * density);
    }

    /**
     * @return A resolved color relative to the height of the given node
     */
    private static int getColorForNode(TreeNode node, Resources resources) {
        // Get the default color
        int sakaiMediumRed = resources.getColor(R.color.sakaiTint);

        // If this is a top-level node (level of 1 indicate a top-level resource directory),
        // then do not perform unnecessary computations
        if(node.getLevel() == 1)
            return sakaiMediumRed;

        // In order for tree levels to be distinct from each other, we will increase the hue,
        // moving around the color wheel
        float[] hsv = new float[3];
        Color.colorToHSV(sakaiMediumRed, hsv);
        hsv[0] += (node.getLevel() - 1) * HUE_DIFF;
        hsv[0] = hsv[0] % MAX_HUE;
        return Color.HSVToColor(hsv);
    }
}
