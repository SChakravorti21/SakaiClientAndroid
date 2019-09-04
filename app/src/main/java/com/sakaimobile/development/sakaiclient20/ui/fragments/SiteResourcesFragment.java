package com.sakaimobile.development.sakaiclient20.ui.fragments;

import android.content.Context;
import android.os.Bundle;
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
import com.sakaimobile.development.sakaiclient20.ui.listeners.TreeViewItemClickListener;
import com.sakaimobile.development.sakaiclient20.ui.viewholders.ResourceDirectoryViewHolder;
import com.sakaimobile.development.sakaiclient20.ui.viewholders.ResourceItemViewHolder;
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.ResourceViewModel;
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.ViewModelFactory;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import dagger.android.support.AndroidSupportInjection;

public class SiteResourcesFragment extends BaseFragment {

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

        this.initRefreshFailureListener(resourceViewModel, () -> {
            this.spinner.setVisibility(View.GONE);
            this.treeContainer.setVisibility(View.VISIBLE);
            return null;
        });

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
                    if(resources == null || resources.size() == 0) {
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
                resourceViewModel.refreshSiteData(currentSiteId);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Updates the resources tree view by removing old nodes and adding new nodes
     * from the resources list
     * @param root root of the tree constructed by the ViewModel
     */
    private void updateResourcesTreeView(TreeNode root) {
        // Ensure directories and files are shown appropriately
        attachViewHolders(root);

        // set the new root
        resourcesTreeView.setRoot(root);
        String treeState = getResourceTreeState();
        resourcesTreeView.restoreState(treeState);

        // render the tree
        treeContainer.removeAllViews();
        treeContainer.addView(resourcesTreeView.getView());
    }

    /**
     * Provides the appropriate ViewHolders for tree nodes based on whether
     * it is a directory or file. Ensures that clicking on a file downloads it.
     */
    private void attachViewHolders(TreeNode node) {
        // Not a base case, but just an NPE check
        if(node == null) return;

        if(node.getValue() instanceof ResourceDirectoryViewHolder.ResourceDirectoryItem) {
            node.setViewHolder(new ResourceDirectoryViewHolder(getContext()));
        } else {
            node.setViewHolder(new ResourceItemViewHolder(getContext()));
            node.setClickListener((fileNode, value) ->
                downloadFile((ResourceItemViewHolder.ResourceFileItem) value)
            );
        }

        for(TreeNode child : node.getChildren()) {
            attachViewHolders(child);
        }
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
