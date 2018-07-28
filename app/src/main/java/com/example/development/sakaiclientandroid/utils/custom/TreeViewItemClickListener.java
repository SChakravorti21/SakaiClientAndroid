package com.example.development.sakaiclientandroid.utils.custom;

import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.lang.ref.WeakReference;

/**
 * Created by Shoumyo Chakravorti on 6/10/18.
 *
 * The default {@link com.unnamed.b.atv.model.TreeNode.TreeNodeClickListener} used throughout
 * the project. Simply, this class defines the behavior when a node is clicked,
 * which is to only allow one node of the highest level to be expanded at any time.
 * This is done to reduce memory consumption, and since having multiple terms
 * expanded can be difficult to navigate for a user.
 */
public class TreeViewItemClickListener implements TreeNode.TreeNodeClickListener {

    /**
     * A {@link WeakReference} to the {@link AndroidTreeView} that is managed by this
     * listener.
     */
    private WeakReference<AndroidTreeView> treeView;

    /**
     * The root {@link TreeNode} that represents the {@code treeView} of this listener.
     */
    private TreeNode root;

    /**
     * Constructor to initialize the {@link AndroidTreeView} being managed.
     * @param treeView the {@link AndroidTreeView} being managed.
     * @param root the {@link TreeNode} of the tree being managed
     */
    public TreeViewItemClickListener(AndroidTreeView treeView, TreeNode root) {
        this.treeView = new WeakReference<>(treeView);
        this.root = root;
    }

    /**
     * Called every time a node is clicked. Checks if the highest level node was
     * clicked (excluding the root node), and if this is the case, the last highest-level
     * node that was previously expanded gets closed.
     * @param node the clicked {@link TreeNode}
     * @param value unused (the {@code Object} representing the node's information)
     */
    @Override
    public void onClick(TreeNode node, Object value) {
        if (node.getLevel() > 1 || treeView == null || treeView.get() == null)
            return;

        // Get all parent nodes
        for (TreeNode parent : root.getChildren()) {
            // If the parent was previously expanded and is not a parent
            // of the currently expanded node, then collapse it
            if (parent.isExpanded() && parent != node) {
                treeView.get().collapseNode(parent);
            }
        }
    }
}
