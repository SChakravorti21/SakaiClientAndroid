package com.example.development.sakaiclient20.persistence.access;

import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.example.development.sakaiclient20.persistence.entities.Grade;

import java.util.List;

import io.reactivex.Flowable;

public abstract class GradeDao implements BaseDao<Grade>{

    @Transaction
    @Query("SELECT * FROM grades WHERE siteId = :siteId")
    public abstract Flowable<List<Grade>> getGradesForSite(String siteId);

    @Transaction
    @Query("DELETE FROM grades WHERE siteId = :siteId")
    abstract void deleteGradesForSite(String siteId);

    @Transaction
    public void insertGradesForSite(String siteId, Grade... grades) {
        deleteGradesForSite(siteId);
        insert(grades);
    }
}
