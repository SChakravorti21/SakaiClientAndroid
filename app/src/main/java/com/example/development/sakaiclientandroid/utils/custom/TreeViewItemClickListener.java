package com.example.development.sakaiclientandroid.utils.custom;

import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

/**
 * Created by Development on 6/10/18.
 */

public class TreeViewItemClickListener implements TreeNode.TreeNodeClickListener {
    private AndroidTreeView treeView;
    private TreeNode root;
    private TreeNode lastParent;

    public TreeViewItemClickListener(AndroidTreeView treeView, TreeNode root) {
        this.treeView = treeView;
        this.root = root;
        this.lastParent = null;
    }

    @Override
    public void onClick(TreeNode node, Object value) {
        if(node.getLevel() > 1)
            return;

        // Get all parent nodes
        for( TreeNode parent : root.getChildren()) {
            // If the parent was previously expanded and is not a parent
            // of the currently expanded node, then collapse it
            if(parent.isExpanded() && parent == lastParent && parent != node) {
                // Collapse its children as well
                // for (TreeNode child : parent.getChildren()) {
                //    if(child.isExpanded())
                //        treeView.collapseNode(child);
                // }
                treeView.collapseNode(parent);
            }
        }

        if(node.getLevel() == 1 && node != lastParent)
            lastParent = node;
    }
}
