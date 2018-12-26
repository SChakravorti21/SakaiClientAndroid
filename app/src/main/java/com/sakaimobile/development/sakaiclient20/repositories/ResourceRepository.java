package com.sakaimobile.development.sakaiclient20.repositories;

import com.sakaimobile.development.sakaiclient20.models.sakai.resources.ResourcesResponse;
import com.sakaimobile.development.sakaiclient20.networking.services.ResourcesService;
import com.sakaimobile.development.sakaiclient20.persistence.access.ResourceDao;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Resource;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public class ResourceRepository {

    private ResourceDao resourceDao;
    private ResourcesService resourcesService;

    public ResourceRepository(ResourceDao resourceDao, ResourcesService service) {
        this.resourceDao = resourceDao;
        this.resourcesService = service;
    }

    public Single<List<Resource>> getSiteResources(String siteId) {
        return resourceDao
                .getSiteResources(siteId)
                .firstOrError();
    }

    public Completable refreshSiteResources(String siteId) {
        return resourcesService
                .getSiteResources(siteId)
                .map(ResourcesResponse::getResources)
                .map(resources -> persistSiteResources(siteId, resources))
                .ignoreElement();
    }

    private List<Resource> persistSiteResources(String siteId, List<Resource> resources) {
        // if its empty list, don't persist them
        if(resources.size() == 0)
            return resources;

        resourceDao.insertResourcesForSite(siteId, resources.toArray(new Resource[0]));
        return resources;
    }
}
