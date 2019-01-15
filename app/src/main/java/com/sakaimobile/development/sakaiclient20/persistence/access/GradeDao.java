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

    /**
     * It is necessary to first delete site grades before inserting new
     * ones because the API does not provide anything resembling a primary key
     * and we auto-generate it. Inserting without deleting would mean that
     * inserting new grades would duplicate them, so we must delete the old ones first.
     */
    @Transaction
    public void insertGradesForSite(String siteId, Grade... grades) {
        deleteGradesForSite(siteId);
        insert(grades);
    }
}
