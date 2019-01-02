package com.sakaimobile.development.sakaiclient20.ui.listeners;

import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Development on 6/10/18.
 */

public class TreeViewItemClickListener implements TreeNode.TreeNodeClickListener {
    private AndroidTreeView treeView;

    public TreeViewItemClickListener(AndroidTreeView treeView) {
        this.treeView = treeView;
    }

    @Override
    public void onClick(TreeNode node, Object value) {
        // Find the clicked node's peers and collapse any
        // expanded nodes that are not the one currently selected.
        // This allows us to only have one node expanded per level.
        for (TreeNode adjacentNode : node.getParent().getChildren()) {
            if (adjacentNode.isExpanded() && adjacentNode != node) {
                treeView.collapseNode(adjacentNode);
                return;
            }
        }
    }
}
