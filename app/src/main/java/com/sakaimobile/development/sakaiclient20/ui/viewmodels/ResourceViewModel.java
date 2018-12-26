package com.sakaimobile.development.sakaiclient20.ui.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.sakaimobile.development.sakaiclient20.persistence.entities.Resource;
import com.sakaimobile.development.sakaiclient20.repositories.ResourceRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class ResourceViewModel extends ViewModel {

    private ResourceRepository resourceRepository;
    private CompositeDisposable compositeDisposable;

    private String siteIdOfResources;
    private MutableLiveData<List<Resource>> resourcesLiveData;


    @Inject
    ResourceViewModel(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
        compositeDisposable = new CompositeDisposable();
    }

    /**
     * Gets the live data to observe on, for the resources for a site
     * @param siteId resources for this site ID
     * @return live data to observe on
     */
    public LiveData<List<Resource>> getResourcesForSite(String siteId) {
        // if the live data is null, initialize it and try loading data
        if (resourcesLiveData == null) {
            resourcesLiveData = new MutableLiveData<>();
            loadSiteResources(siteId);
            siteIdOfResources = siteId;
        }
        // if we have observable is for the wrong site, load data for the correct site
        else if(!siteIdOfResources.equals(siteId)) {
            loadSiteResources(siteId);
            siteIdOfResources = siteId;
        }

        return resourcesLiveData;
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
    public void refreshSiteResources(String siteId) {
        compositeDisposable.add(
                resourceRepository.refreshSiteResources(siteId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                (resources) -> {
                                    if(resources.size() == 0)
                                        resourcesLiveData.setValue(null);
                                    else
                                        loadSiteResources(siteId);
                                },
                                Throwable::printStackTrace
                        )
        );
    }

    /**
     * Loads site resources from database into live data
     *
     * if the database doesn't have any resources for this site, it will refresh
     * otherwise if its already in the database, then set the live data value
     *
     * @param siteId siteId to load resources for
     */
    private void loadSiteResources(String siteId) {
        compositeDisposable.add(
                resourceRepository.getSiteResources(siteId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                resources -> {
                                    // if nothing in DB, refresh
                                    if(resources.size() == 0)
                                        refreshSiteResources(siteId);
                                    else
                                        resourcesLiveData.setValue(resources);
                                },
                                Throwable::printStackTrace

                        )
        );
    }

}
