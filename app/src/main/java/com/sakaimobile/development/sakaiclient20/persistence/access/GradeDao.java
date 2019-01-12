package com.sakaimobile.development.sakaiclient20.persistence.access;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.sakaimobile.development.sakaiclient20.persistence.entities.Grade;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public abstract class GradeDao implements BaseDao<Grade>{

    @Query("SELECT * FROM grades WHERE siteId = :siteId")
    public abstract Flowable<List<Grade>> getGradesForSite(String siteId);

    @Query("DELETE FROM grades WHERE siteId = :siteId")
    public abstract void deleteGradesForSite(String siteId);

    @Transaction
    public void insertGradesForSite(String siteId, Grade... grades) {
        deleteGradesForSite(siteId);
        insert(grades);
    }
}
