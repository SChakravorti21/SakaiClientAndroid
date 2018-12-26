package com.sakaimobile.development.sakaiclient20.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.sakaimobile.development.sakaiclient20.R;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Resource;
import com.sakaimobile.development.sakaiclient20.ui.fragments.WebFragment;
import com.sakaimobile.development.sakaiclient20.ui.viewholders.ResourceDirectoryViewHolder;
import com.sakaimobile.development.sakaiclient20.ui.viewholders.ResourceItemViewHolder;
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.ResourceViewModel;
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.ViewModelFactory;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class SiteResourcesActivity extends AppCompatActivity {

    @Inject
    ViewModelFactory viewModelFactory;

    private Set<LiveData> beingObserved;

    private String currentSiteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_resources);

        // get the parent view container
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swiperefresh);

        // initialized the being observed set
        beingObserved = new HashSet<>();

        // get the siteId of the course to show
        Intent intent = getIntent();
        currentSiteId = intent.getStringExtra(getString(R.string.site_resources_tag));

        LiveData<List<Resource>> resourceLiveData =
                ViewModelProviders.of(this, viewModelFactory)
                        .get(ResourceViewModel.class)
                        .getResourcesForSite(currentSiteId);

        beingObserved.add(resourceLiveData);

        // observe on the resources data
        resourceLiveData.observe(this, resources -> {

//            swipeRefreshLayout.removeAllViews();
            // build the treeView and add it to the parent view
            AndroidTreeView treeView = constructResourcesTreeView(resources);
            swipeRefreshLayout.addView(treeView.getView());
        });


        // set refresh listener
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
     *
     * @param resource
     * @return
     */
    private TreeNode buildResourceFileNode(Resource resource) {
        ResourceItemViewHolder.ResourceFileItem fileItem =
                new ResourceItemViewHolder.ResourceFileItem(resource.title, resource.url);

        TreeNode fileNode = new TreeNode(fileItem).setViewHolder(new ResourceItemViewHolder(this));

        fileNode.setClickListener((node, value) -> {

            if (value instanceof ResourceItemViewHolder.ResourceFileItem)
                downloadFile((ResourceItemViewHolder.ResourceFileItem) value);
        });


        return fileNode;
    }

    /**
     * Build a node for a resource directory
     *
     * @param resource
     * @return
     */
    private TreeNode buildResourceDirNode(Resource resource) {
        ResourceDirectoryViewHolder.ResourceDirectoryItem dirItem =
                new ResourceDirectoryViewHolder.ResourceDirectoryItem(resource.title);

        return new TreeNode(dirItem).setViewHolder(new ResourceDirectoryViewHolder(this));
    }


    private void downloadFile(ResourceItemViewHolder.ResourceFileItem item) {
        WebFragment fragment = WebFragment.newInstance(item.url);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.swiperefresh, fragment)
                .commit();
    }


    @Override
    protected void onPause() {
        super.onPause();
        removeObservations();
    }

    private void removeObservations() {
        for (LiveData liveData : beingObserved) {
            liveData.removeObservers(this);
        }
        beingObserved.clear();
    }

}
