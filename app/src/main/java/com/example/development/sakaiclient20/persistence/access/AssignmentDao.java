package com.example.development.sakaiclient20.persistence.access;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.example.development.sakaiclient20.persistence.composites.AssignmentWithAttachments;
import com.example.development.sakaiclient20.persistence.entities.Assignment;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Created by Development on 8/5/18.
 */
@Dao
public abstract class AssignmentDao implements BaseDao<Assignment> {

    @Transaction
    @Query("SELECT * FROM assignments ORDER BY dueTime")
    public abstract Flowable<List<AssignmentWithAttachments>> getAllAssignments();

    @Transaction //Wrap in transaction since Room technically performs multiple transactions
    @Query("SELECT * FROM assignments WHERE siteId = :siteId")
    public abstract Flowable<List<AssignmentWithAttachments>> getAssignmentsForSite(String siteId);

}
