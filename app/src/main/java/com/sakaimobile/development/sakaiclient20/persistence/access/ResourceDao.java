package com.sakaimobile.development.sakaiclient20.persistence.access;

import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.sakaimobile.development.sakaiclient20.persistence.entities.Resource;

import java.util.List;

import io.reactivex.Flowable;

public abstract class ResourceDao implements BaseDao<Resource> {

    @Query("SELECT * FROM resources WHERE siteId = :siteId")
    public abstract Flowable<List<Resource>> getSiteResources(String siteId);

    @Query("DELETE FROM resources WHERE siteId = :siteId")
    public abstract void deleteResourcesForSite(String siteId);

    @Transaction
    public void insertResourcesForSite(String siteId, Resource... resources) {
        deleteResourcesForSite(siteId);
        insert(resources);
    }

}
