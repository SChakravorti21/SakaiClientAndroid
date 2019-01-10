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
public abstract class AnnouncementDao implements BaseDao<Announcement> {

    @Query("DELETE FROM announcements")
    public abstract void deleteAllAnnouncements();

    @Query("SELECT COUNT(*) from announcements")
    public abstract Single<Integer> getAnnouncementCount();

    @Transaction
    @Query("SELECT * FROM announcements ORDER BY createdOn DESC LIMIT :start, :count")
    public abstract Flowable<List<AnnouncementWithAttachments>> getAllAnnouncementsInRange(int start, int count);


    @Transaction
//    @Query("SELECT * from announcements WHERE siteId = :siteId ORDER BY createdOn DESC LIMIT :start, :count")
//    public abstract Flowable<List<AnnouncementWithAttachments>> getXSiteAnnouncements(String siteId, int count);



    @Query("DELETE FROM announcements WHERE siteId = :siteId")
    public abstract void deleteAnnouncementsForSite(String siteId);

    @Transaction
    public void insertAnnouncementsForSite(String siteId, Announcement... announcements) {
        deleteAnnouncementsForSite(siteId);
        insert(announcements);
    }
}
