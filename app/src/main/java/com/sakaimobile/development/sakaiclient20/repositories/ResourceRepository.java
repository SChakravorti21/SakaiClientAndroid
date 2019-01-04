package com.sakaimobile.development.sakaiclient20.repositories;

import com.sakaimobile.development.sakaiclient20.models.sakai.resources.ResourcesResponse;
import com.sakaimobile.development.sakaiclient20.networking.services.ResourcesService;
import com.sakaimobile.development.sakaiclient20.persistence.access.ResourceDao;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Resource;

import java.util.List;

import io.reactivex.Single;

public class ResourceRepository {

    private ResourceDao resourceDao;
    private ResourcesService resourcesService;

    public ResourceRepository(ResourceDao resourceDao, ResourcesService service) {
        this.resourceDao = resourceDao;
        this.resourcesService = service;
    }

    /**
     * Gets the stored site resources from room database
     * @param siteId siteId of resources
     * @return list of resources, can be observed on
     */
    public Single<List<Resource>> getSiteResources(String siteId) {
        return resourceDao
                .getSiteResources(siteId)
                .firstOrError();
    }

    /**
     * refresh the site resources, sets the siteIDs and then persists them
     * @param siteId siteId of resources to request
     * @return whether or not the operation was successful
     */
    public Single<List<Resource>> refreshSiteResources(String siteId) {
        return resourcesService
                .getSiteResources(siteId)
                .map(ResourcesResponse::getResources)
                .map(resources -> setSiteIdOfResources(siteId, resources))
                .map(this::persistSiteResources);
    }

    /**
     * Set the siteId of the requested resources, since it is not in the response body
     * @param siteId siteId to set
     * @param resources resource list
     * @return resource list with siteIds set
     */
    private List<Resource> setSiteIdOfResources(String siteId, List<Resource> resources) {
        for(Resource resource : resources) {
            resource.siteId = siteId;
        }
        return resources;
    }

    /**
     * Store the resources in room database
     * @param resources resource list
     * @return stored resources
     */
    private List<Resource> persistSiteResources(List<Resource> resources) {
        // if its empty list, don't persist them
        if(resources.size() == 0)
            return resources;

        resourceDao.insert(resources.toArray(new Resource[0]));
        return resources;
    }
}
