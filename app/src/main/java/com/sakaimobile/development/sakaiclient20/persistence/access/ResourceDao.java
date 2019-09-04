package com.sakaimobile.development.sakaiclient20.persistence.access;

import com.sakaimobile.development.sakaiclient20.persistence.entities.Resource;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Query;
import io.reactivex.Flowable;

@Dao
public abstract class ResourceDao extends BaseDao<Resource> {

    @Query("SELECT * FROM resources WHERE siteId = :siteId")
    public abstract Flowable<List<Resource>> getSiteResources(String siteId);

}
