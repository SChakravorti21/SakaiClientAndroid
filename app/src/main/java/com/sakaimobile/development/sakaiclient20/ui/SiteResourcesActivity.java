package com.sakaimobile.development.sakaiclient20.ui;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.sakaimobile.development.sakaiclient20.R;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Resource;
import com.sakaimobile.development.sakaiclient20.ui.custom_components.CustomLinkMovementMethod;
import com.sakaimobile.development.sakaiclient20.ui.fragments.WebFragment;
import com.sakaimobile.development.sakaiclient20.ui.viewholders.ResourceDirectoryViewHolder;
import com.sakaimobile.development.sakaiclient20.ui.viewholders.ResourceItemViewHolder;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.ArrayList;
import java.util.List;

public class SiteResourcesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_resources);

        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swiperefresh);

        Intent intent = getIntent();

        List<Resource> resourceList = (List<Resource>)intent.getSerializableExtra(getString(R.string.site_resources_tag));

        AndroidTreeView treeView = constructResourcesTreeView(resourceList);
        swipeRefreshLayout.addView(treeView.getView());

        swipeRefreshLayout.setOnRefreshListener(() -> {
            Toast.makeText(this, "refresh", Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private AndroidTreeView constructResourcesTreeView(List<Resource> flatResources) {

        TreeNode root = TreeNode.root();
        AndroidTreeView treeView = new AndroidTreeView(this, root);
        treeView.setDefaultAnimation(true);

        root.addChildren(getChildren(flatResources, 0, flatResources.size()));
        return treeView;
    }

    private List<TreeNode> getChildren(List<Resource> resources, int start, int end) {

        List<TreeNode> children = new ArrayList<>();
        for (int i = start; i < end; i++) {

            Resource resource = resources.get(i);
            if (resource.isDirectory) {

                // build directory node
                TreeNode dirNode = buildResourceDirNode(resource);

                // add the directory as the child of the parent node
                children.add(dirNode);

                // recursively get the children of this directory, and add them
                List<TreeNode> dirChildren = getChildren(resources, i + 1, i + 1 + resource.numDescendants);
                dirNode.addChildren(dirChildren);

                // move forward index
                i += resource.numDescendants;
            } else {

                // add this file node as a child of the parent node
                children.add(buildResourceFileNode(resource));
            }
        }

        return children;
    }


    /**
     * Build a node for a resource file item
     * @param resource
     * @return
     */
    private TreeNode buildResourceFileNode(Resource resource) {
        ResourceItemViewHolder.ResourceFileItem fileItem =
                new ResourceItemViewHolder.ResourceFileItem(resource.title, resource.url);

        TreeNode fileNode = new TreeNode(fileItem).setViewHolder(new ResourceItemViewHolder(this));

        fileNode.setClickListener((node, value) -> {
           if(value instanceof ResourceItemViewHolder.ResourceFileItem) {

               String url = ((ResourceItemViewHolder.ResourceFileItem) value).url;
               WebFragment fragment = WebFragment.newInstance(url);

               getSupportFragmentManager()
                       .beginTransaction()
                       .add(R.id.swiperefresh, fragment)
                       .commit();
           }
        });

        return fileNode;
    }

    /**
     * Build a node for a resource directory
     * @param resource
     * @return
     */
    private TreeNode buildResourceDirNode(Resource resource) {
        ResourceDirectoryViewHolder.ResourceDirectoryItem dirItem =
                new ResourceDirectoryViewHolder.ResourceDirectoryItem(resource.title);

        return new TreeNode(dirItem).setViewHolder(new ResourceDirectoryViewHolder(this));
    }

}
