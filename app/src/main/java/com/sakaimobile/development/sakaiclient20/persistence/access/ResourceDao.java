package com.sakaimobile.development.sakaiclient20.persistence.access;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.sakaimobile.development.sakaiclient20.persistence.entities.Resource;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public abstract class ResourceDao implements BaseDao<Resource> {

    @Query("SELECT * FROM resources WHERE siteId = :siteId")
    public abstract Flowable<List<Resource>> getSiteResources(String siteId);

}
