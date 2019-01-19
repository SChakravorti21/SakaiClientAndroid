package com.sakaimobile.development.sakaiclient20.ui.viewmodels;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.sakaimobile.development.sakaiclient20.persistence.entities.Resource;
import com.sakaimobile.development.sakaiclient20.repositories.ResourceRepository;

import org.apache.commons.lang3.NotImplementedException;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class ResourceViewModel extends BaseViewModel {

    private ResourceRepository resourceRepository;
    private MutableLiveData<List<Resource>> resourcesLiveData;

    @Inject
    ResourceViewModel(ResourceRepository resourceRepository) {
        super();
        this.resourceRepository = resourceRepository;
    }

    /**
     * Gets the live data to observe on, for the resources for a site
     * @param siteId resources for this site ID
     * @return live data to observe on
     */
    public LiveData<List<Resource>> getResourcesForSite(String siteId) {
        // if the live data is null, initialize it and try loading data
        if (resourcesLiveData == null)
            resourcesLiveData = new MutableLiveData<>();

        loadSiteResources(siteId);
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
    @SuppressLint("CheckResult")
    @Override
    public void refreshSiteData(String siteId) {
        resourceRepository.refreshSiteResources(siteId)
            .doOnSubscribe(compositeDisposable::add)
            .doOnError(this::emitError)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                    resources -> {
                        // Resource size of 1 indicates that resources
                        // have been set up for the site but it is an empty collection
                        if(resources.size() == 1)
                            resourcesLiveData.setValue(null);
                    }, Throwable::printStackTrace
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
                        if(resources.isEmpty())
                            refreshSiteData(siteId);
                        else
                            resourcesLiveData.setValue(resources);
                    }, Throwable::printStackTrace
                )
        );
    }

    @Override
    void refreshAllData() {
        throw new NotImplementedException("Never need to request resources for all courses at once");
    }
}
