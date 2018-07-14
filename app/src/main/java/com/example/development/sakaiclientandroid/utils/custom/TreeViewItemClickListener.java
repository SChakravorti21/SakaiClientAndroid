package com.example.development.sakaiclientandroid.utils.custom;

import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

/**
 * Created by Development on 6/10/18.
 */

public class TreeViewItemClickListener implements TreeNode.TreeNodeClickListener {
    private AndroidTreeView treeView;
    private TreeNode root;

    public TreeViewItemClickListener(AndroidTreeView treeView, TreeNode root) {
        this.treeView = treeView;
        this.root = root;
    }

    @Override
    public void onClick(TreeNode node, Object value) {
        if (node.getLevel() > 1)
            return;


        // Get all parent nodes
        for (TreeNode parent : root.getChildren()) {
            // If the parent was previously expanded and is not a parent
            // of the currently expanded node, then collapse it
            if (parent.isExpanded() && parent != node) {
                treeView.collapseNode(parent);
            }
        }
    }
}
