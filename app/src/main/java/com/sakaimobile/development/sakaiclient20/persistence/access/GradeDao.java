package com.sakaimobile.development.sakaiclient20.persistence.access;

import com.sakaimobile.development.sakaiclient20.persistence.entities.Grade;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;
import io.reactivex.Flowable;

@Dao
public abstract class GradeDao extends BaseDao<Grade>{

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
    public void insertGradesForSite(String siteId, List<Grade> grades) {
        deleteGradesForSite(siteId);
        insert(grades);
    }
}
