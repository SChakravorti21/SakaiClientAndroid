package com.sakaimobile.development.sakaiclient20.persistence.access;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.sakaimobile.development.sakaiclient20.persistence.composites.AnnouncementWithAttachments;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Announcement;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public abstract class AnnouncementDao implements BaseDao<Announcement> {

    @Query("DELETE FROM announcements")
    public abstract void deleteAllAnnouncements();

    @Transaction
    @Query("SELECT * FROM announcements ORDER BY createdOn DESC")
    public abstract Flowable<List<AnnouncementWithAttachments>> getAllAnnouncements();

    @Transaction
    @Query("SELECT * FROM announcements WHERE siteId = :siteId ORDER BY createdOn DESC")
    public abstract Flowable<List<AnnouncementWithAttachments>> getAnnouncementsForSite(String siteId);

    @Query("DELETE FROM announcements WHERE siteId = :siteId")
    public abstract void deleteAnnouncementsForSite(String siteId);

    @Transaction
    public void insertAnnouncementsForSite(String siteId, Announcement... announcements) {
        deleteAnnouncementsForSite(siteId);
        insert(announcements);
    }
}
