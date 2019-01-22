package com.sakaimobile.development.sakaiclient20.ui.viewmodels

import android.annotation.SuppressLint
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

import com.sakaimobile.development.sakaiclient20.persistence.entities.Resource
import com.sakaimobile.development.sakaiclient20.repositories.ResourceRepository
import com.unnamed.b.atv.model.TreeNode

import org.apache.commons.lang3.NotImplementedException

import javax.inject.Inject

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ResourceViewModel
@Inject constructor(private val resourceRepository: ResourceRepository) : BaseViewModel() {

    private val resourcesLiveData: MutableLiveData<List<Resource>> = MutableLiveData()

    override fun refreshAllData() {
        throw NotImplementedException("Never need to request resources for all courses at once")
    }

    /**
     * Gets the live data to observe on, for the resources for a site
     * @param siteId resources for this site ID
     * @return live data to observe on
     */
    fun getResourcesForSite(siteId: String): LiveData<List<Resource>> {
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
                            if (resources.isEmpty())
                                refreshSiteData(siteId)
                            else
                                resourcesLiveData!!.setValue(resources)
                        }, { it.printStackTrace() }
                )
        )
    }
}
