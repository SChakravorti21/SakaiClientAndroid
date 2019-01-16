package com.sakaimobile.development.sakaiclient20.persistence.access;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.sakaimobile.development.sakaiclient20.persistence.composites.AssignmentWithAttachments;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Assignment;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Created by Development on 8/5/18.
 */
@Dao
public abstract class AssignmentDao extends BaseDao<Assignment> {

    //Wrap in transaction since Room technically performs multiple transactions
    // for @Relations (used here with attachments)
    @Transaction
    @Query("SELECT * FROM assignments WHERE siteId IN (:siteIds)")
    public abstract Flowable<List<AssignmentWithAttachments>> getSiteAssignments(List<String> siteIds);

}
