package com.sakaimobile.development.sakaiclient20.persistence.access;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.sakaimobile.development.sakaiclient20.persistence.composites.AnnouncementWithAttachments;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Announcement;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;

@Dao
public abstract class AnnouncementDao extends BaseDao<Announcement> {

    @Transaction
    @Query("SELECT * FROM announcements ORDER BY createdOn DESC")
    public abstract Flowable<List<AnnouncementWithAttachments>> getAllAnnouncements();


    @Transaction
    @Query("SELECT * from announcements WHERE siteId = :siteId ORDER BY createdOn DESC")
    public abstract Flowable<List<AnnouncementWithAttachments>> getSiteAnnouncements(String siteId);

}
