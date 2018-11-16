package com.example.development.sakaiclient20.persistence.access;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.example.development.sakaiclient20.persistence.entities.Announcement;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public abstract class AnnouncementDao implements BaseDao<Announcement> {

//    @Transaction
//    @Query("SELECT * FROM announcements ORDER BY createdOn DESC")
//    public abstract Flowable<List<Announcement>> getAllAnnouncements();
}
