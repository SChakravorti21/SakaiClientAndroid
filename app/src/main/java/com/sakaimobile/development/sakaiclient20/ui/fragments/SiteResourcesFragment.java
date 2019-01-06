package com.sakaimobile.development.sakaiclient20.ui.fragments;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.Bundle;
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

import com.sakaimobile.development.sakaiclient20.R;
import com.sakaimobile.development.sakaiclient20.networking.utilities.SharedPrefsUtil;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Resource;
import com.sakaimobile.development.sakaiclient20.ui.viewholders.ResourceDirectoryViewHolder;
import com.sakaimobile.development.sakaiclient20.ui.viewholders.ResourceItemViewHolder;
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.ResourceViewModel;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

public class SiteResourcesFragment extends Fragment {

    @Inject
    ResourceViewModel resourceViewModel;
    private String currentSiteId;
    private AndroidTreeView resourcesTreeView;
    private ProgressBar spinner;
    private FrameLayout treeContainer;
    private View viewOfTree;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Bundle bun = getArguments();
        if(bun != null)
            currentSiteId = bun.getString(getString(R.string.siteid_tag));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.refresh_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_refresh:

                // this can be null if the user immediately clicks refresh while
                // resources are loading for the first time
                // don't refresh if this is the case
                if(viewOfTree == null)
                    return false;

                spinner.setVisibility(View.VISIBLE);
                viewOfTree.setVisibility(View.GONE);

                saveResourceTreeState();
                resourceViewModel.refreshSiteResources(currentSiteId);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        saveResourceTreeState();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_site_resources, null);

        spinner = view.findViewById(R.id.progress_circular);
        spinner.bringToFront();
        spinner.invalidate();
        spinner.setVisibility(View.VISIBLE);

        // get the parent view container
        treeContainer = view.findViewById(R.id.container);


        // setup the treeview
        final TreeNode root = TreeNode.root();
        resourcesTreeView = new AndroidTreeView(getActivity(), root);
        resourcesTreeView.setDefaultAnimation(true);



        // request the resources for the site
        LiveData<List<Resource>> resourceLiveData =
                resourceViewModel.getResourcesForSite(currentSiteId);

        // observe on the resources data
        resourceLiveData.observe(this, resources -> {

            // update the resources tree view
            updateResourcesTreeView(resources);

            spinner.setVisibility(View.GONE);
            viewOfTree.setVisibility(View.VISIBLE);
        });

        return view;
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

        restoreResourceTreeState();

        // render the tree
        treeContainer.removeAllViews();

        viewOfTree = resourcesTreeView.getView();
        treeContainer.addView(viewOfTree);
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
     * @param resource item
     * @return treenode to add
     */
    private TreeNode buildResourceFileNode(Resource resource) {
        ResourceItemViewHolder.ResourceFileItem fileItem =
                new ResourceItemViewHolder.ResourceFileItem(resource.title, resource.url);

        TreeNode fileNode = new TreeNode(fileItem).setViewHolder(new ResourceItemViewHolder(getActivity()));

        fileNode.setClickListener((node, value) -> {

            if (value instanceof ResourceItemViewHolder.ResourceFileItem)
                downloadFile((ResourceItemViewHolder.ResourceFileItem) value);
        });


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

        return new TreeNode(dirItem).setViewHolder(new ResourceDirectoryViewHolder(getActivity()));
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

        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                .add(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }



    private void saveResourceTreeState() {
        SharedPrefsUtil.saveTreeState(getActivity(), resourcesTreeView, SharedPrefsUtil.SITE_RESOURCES_TREE_TYPE);
    }

    private void restoreResourceTreeState() {
        String state = SharedPrefsUtil.getTreeState(getContext(), SharedPrefsUtil.SITE_RESOURCES_TREE_TYPE);
        resourcesTreeView.restoreState(state);
    }

}
