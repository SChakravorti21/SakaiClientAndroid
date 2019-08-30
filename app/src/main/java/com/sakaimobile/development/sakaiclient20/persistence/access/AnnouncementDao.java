package com.sakaimobile.development.sakaiclient20.persistence.access;

import com.sakaimobile.development.sakaiclient20.persistence.composites.AnnouncementWithAttachments;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Announcement;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;
import io.reactivex.Flowable;

@Dao
public abstract class AnnouncementDao extends BaseDao<Announcement> {

    @Transaction
    @Query("SELECT * FROM announcements ORDER BY createdOn DESC")
    public abstract Flowable<List<AnnouncementWithAttachments>> getAllAnnouncements();


    @Transaction
    @Query("SELECT * from announcements WHERE siteId = :siteId ORDER BY createdOn DESC")
    public abstract Flowable<List<AnnouncementWithAttachments>> getSiteAnnouncements(String siteId);

}
