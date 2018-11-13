package com.example.development.sakaiclient20.persistence.access;

import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Update;

/**
 * Created by Development on 8/5/18.
 */

public interface BaseDao<TEntity> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TEntity... entities);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(TEntity... entities);

    @Delete
    void delete(TEntity... entities);

}
