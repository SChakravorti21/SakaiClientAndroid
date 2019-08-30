package com.sakaimobile.development.sakaiclient20.ui.viewmodels

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

import com.sakaimobile.development.sakaiclient20.persistence.entities.Resource
import com.sakaimobile.development.sakaiclient20.repositories.ResourceRepository
import com.sakaimobile.development.sakaiclient20.ui.viewholders.ResourceDirectoryViewHolder
import com.sakaimobile.development.sakaiclient20.ui.viewholders.ResourceItemViewHolder
import com.unnamed.b.atv.model.TreeNode

import javax.inject.Inject

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ResourceViewModel
@Inject constructor(private val resourceRepository: ResourceRepository) : BaseViewModel() {

    private val resourcesLiveData: MutableLiveData<TreeNode> = MutableLiveData()

    override fun refreshAllData() {
        throw NotImplementedError("Never need to request resources for all courses at once")
    }

    /**
     * Gets the live data to observe on, for the resources for a site
     * @param siteId resources for this site ID
     * @return live data to observe on
     */
    fun getResourcesForSite(siteId: String): LiveData<TreeNode> {
        loadSiteResources(siteId)
        return resourcesLiveData
    }

    /**
     * If try to refresh and still get no resources, set the value of the livedata to null
     * so that the activity knows what to do
     *
     * otherwise just load the resources from the database into the livedata
     *
     * if try to load the resources with an empty list, it will try to refresh again,
     * leading to an infinite loop
     *
     * @param siteId siteId
     */
    @SuppressLint("CheckResult")
    public override fun refreshSiteData(siteId: String) {
        resourceRepository.refreshSiteResources(siteId)
            .doOnSubscribe { compositeDisposable.add(it) }
            .doOnError { this.emitError(it) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                    { resources ->
                        // Resource size of 1 indicates that resources
                        // have been set up for the site but it is an empty collection
                        if (resources.size == 1)
                            resourcesLiveData.value = null
                    }, { it.printStackTrace() }
            )
    }

    /**
     * Loads site resources from database into live data
     *
     * if the database doesn't have any resources for this site, it will refresh
     * otherwise if its already in the database, then set the live data value
     *
     * @param siteId siteId to load resources for
     */
    private fun loadSiteResources(siteId: String) {
        compositeDisposable.add(
            resourceRepository.getSiteResources(siteId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { resources ->
                            // if nothing in DB, refresh
                            if (resources.isEmpty()) {
                                refreshSiteData(siteId)
                            } else {
                                val root = resources[0]
                                val (_, children) = buildResourceTree(
                                        resources.subList(1, resources.size),
                                        root.numChildren, 0)
                                val treeRoot = TreeNode.root().apply { addChildren(children) }
                                resourcesLiveData.value = treeRoot
                            }
                        }, { it.printStackTrace() }
                )
        )
    }

    private fun buildResourceTree(_data: List<Resource>?, numChildren: Int, onLevel: Int) : Pair<Int, List<TreeNode>> {
        // If the list is empty, return an empty list to denote no children
        val data = _data?.takeIf { !it.isEmpty() } ?: return Pair(0, listOf())
        // These are the children nodes we will be returning
        val tree: MutableList<TreeNode> = mutableListOf()
        // boolean used for ResourceItemViewHolder to determine the styling to apply
        // to downloadable resources
        var isFirstFile = true
        // index is managed separately from the loop because we might need to skip
        // over an entire chunk of the input resources if they form a whole subtree
        var index = 0

        for (i in 0 until numChildren) {
            // prevent out-of-bounds exceptions
            val resourceIndex = index.takeIf { it < data.size } ?: break
            // Edge case: sometimes resources are uploaded by instructors but hidden from
            // students, so numChildren is actually inaccurate. We do not want to include
            // child nodes if they do not belong to the parent (i.e. are not at the appropriate depth)
            val resource = data[resourceIndex].takeIf { it.level == onLevel } ?: return Pair(index, tree)
            val node: TreeNode = when(resource.isDirectory) {
                true -> {
                    // The direct descendants for this directory are from start to end inclusive
                    // start: the resource right after this one (since list is flattened tree)
                    // end: either the next chunk of `resource.size` elements, or up to the
                    //      end of the list, whichever has fewer elements
                    val start = index + 1
                    val end = Math.min(index + resource.size + 1, data.size)
                    val children = data.subList(start, end) // [start, end)
                    val (subtreeSize, childrenNodes) = buildResourceTree(children,
                            resource.numChildren,
                            resource.level + 1)
                    index += subtreeSize
                    TreeNode(ResourceDirectoryViewHolder.ResourceDirectoryItem(resource.title))
                            .apply { addChildren(childrenNodes) }
                }
                false -> {
                    val fileItem = TreeNode(ResourceItemViewHolder.ResourceFileItem(resource.title,
                            resource.url, isFirstFile))
                    isFirstFile = false
                    fileItem
                }
            }

            tree += node
            index++
        }

        return Pair(data.size, tree)
    }
}
