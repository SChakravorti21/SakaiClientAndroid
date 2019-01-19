package com.sakaimobile.development.sakaiclient20.ui.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.sakaimobile.development.sakaiclient20.R;
import com.sakaimobile.development.sakaiclient20.networking.utilities.SharedPrefsUtil;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Resource;
import com.sakaimobile.development.sakaiclient20.ui.listeners.TreeViewItemClickListener;
import com.sakaimobile.development.sakaiclient20.ui.viewholders.ResourceDirectoryViewHolder;
import com.sakaimobile.development.sakaiclient20.ui.viewholders.ResourceItemViewHolder;
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.ResourceViewModel;
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.ViewModelFactory;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

public class SiteResourcesFragment extends Fragment {

    private String currentSiteId;
    @Inject ViewModelFactory viewModelFactory;
    private ResourceViewModel resourceViewModel;

    private ProgressBar spinner;
    private FrameLayout treeContainer;
    private AndroidTreeView resourcesTreeView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        currentSiteId = getArguments().getString(getString(R.string.siteid_tag));
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
        resourceViewModel = ViewModelProviders.of(this, viewModelFactory).get(ResourceViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_site_resources, container, false);
        spinner = view.findViewById(R.id.progress_circular);
        treeContainer = view.findViewById(R.id.container);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // setup the treeview
        resourcesTreeView = new AndroidTreeView(getActivity(), TreeNode.root());
        resourcesTreeView.setDefaultAnimation(true);

        // Default node click listener that only allows one node in each level to
        // be open at a given time
        TreeViewItemClickListener nodeClickListener = new TreeViewItemClickListener(resourcesTreeView);
        resourcesTreeView.setDefaultNodeClickListener(nodeClickListener);
        // Save the resource tree state when the TreeView is created
        // because we do NOT want to share tree structures between different courses
        saveResourceTreeState();

        resourceViewModel.getResourcesForSite(currentSiteId)
                .observe(getViewLifecycleOwner(), resources -> {
                    if(resources == null || resources.size() == 1) {
                        Toast.makeText(getContext(), "No resources found", Toast.LENGTH_SHORT).show();
                    } else {
                        // update the resources tree view
                        updateResourcesTreeView(resources);
                    }

                    spinner.setVisibility(View.GONE);
                    treeContainer.setVisibility(View.VISIBLE);
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        spinner = null;
        treeContainer = null;
        resourcesTreeView = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.refresh_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                saveResourceTreeState();
                spinner.setVisibility(View.VISIBLE);
                treeContainer.setVisibility(View.GONE);
                resourceViewModel.refreshSiteResources(currentSiteId);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Updates the resources tree view by removing old nodes and adding new nodes
     * from the resources list
     *
     * @param flatResources new list of resources
     */
    private void updateResourcesTreeView(List<Resource> flatResources) {
        // create new tree root
        TreeNode root = TreeNode.root();
        List<TreeNode> children = getChildren(flatResources, 1, flatResources.size());
        root.addChildren(children);

        // set the new root
        resourcesTreeView.setRoot(root);
        String treeState = getResourceTreeState();
        resourcesTreeView.restoreState(treeState);

        // render the tree
        treeContainer.removeAllViews();
        treeContainer.addView(resourcesTreeView.getView());
    }


    /**
     * Recursively gets the children for a "node" given the list of resources
     *
     * @param resources list, must be in a proper traversal format, otherwise algorithm won't work
     *                  vist, then go to all children, recursively
     * @param start     start of the list to get children of
     * @param end       end of the part of the list representing the descendants
     * @return a list of nodes which are the children of the node at resources[start]
     */
    private List<TreeNode> getChildren(List<Resource> resources, int start, int end) {
        List<TreeNode> children = new ArrayList<>();
        boolean isFirstFile = true;

        for (int i = start; i < end && i < resources.size(); i++) {
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
                children.add(buildResourceFileNode(resource, isFirstFile));
                isFirstFile = false;
            }
        }

        return children;
    }


    /**
     * Build a node for a resource file item
     *
     * @param resource item
     * @return treenode to add
     */
    private TreeNode buildResourceFileNode(Resource resource, boolean isFirstFile) {
        ResourceItemViewHolder.ResourceFileItem fileItem =
                new ResourceItemViewHolder.ResourceFileItem(resource.title, resource.url, isFirstFile);

        TreeNode fileNode = new TreeNode(fileItem).setViewHolder(new ResourceItemViewHolder(getContext()));
        fileNode.setClickListener((node, value) ->
                downloadFile((ResourceItemViewHolder.ResourceFileItem) value)
        );

        return fileNode;
    }

    /**
     * Build a node for a resource directory, only using the resource title
     *
     * @param resource item
     * @return treenode to add
     */
    private TreeNode buildResourceDirNode(Resource resource) {
        ResourceDirectoryViewHolder.ResourceDirectoryItem dirItem =
                new ResourceDirectoryViewHolder.ResourceDirectoryItem(resource.title);

        return new TreeNode(dirItem).setViewHolder(new ResourceDirectoryViewHolder(getContext()));
    }


    /**
     * Downloads a file inside a given resource file item node
     * <p>
     * loads up a web fragment to download the file
     *
     * @param item resource file item node
     */
    private void downloadFile(ResourceItemViewHolder.ResourceFileItem item) {
        WebFragment fragment = WebFragment.newInstance(item.url);

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                .add(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }


    private void saveResourceTreeState() {
        SharedPrefsUtil.saveTreeState(getActivity(), resourcesTreeView, SharedPrefsUtil.SITE_RESOURCES_TREE_TYPE);
    }

    private String getResourceTreeState() {
        return SharedPrefsUtil.getTreeState(getActivity(), SharedPrefsUtil.SITE_RESOURCES_TREE_TYPE);
    }

}
