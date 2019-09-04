package com.sakaimobile.development.sakaiclient20.persistence.access;

import java.util.ArrayList;
import java.util.List;

import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Transaction;
import androidx.room.Update;

/**
 * Created by Development on 8/5/18.
 */

public abstract class BaseDao<TEntity> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(List<TEntity> entities);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void update(List<TEntity> entities);

    /////////////////////////////////////////////////////////
    //  PROTOCOL FOR UPSERTING COURSES AND RELATED ENTITIES
    /////////////////////////////////////////////////////////

    /**
     * @return the status code of each insert for the corresponding entity (-1 if failed)
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract List<Long> insertIgnoringConflicts(List<TEntity> entities);


    /**
     * An UPSERT implementation for upserting entities. This is necessary because
     * we do NOT want to replace existing courses on insert, as that would cascade down the
     * foreign keys and delete related entities (such as grades, assignments, etc).
     * Since there is no UPSERT OnConflictStrategy, this behavior must be implemented ourselves.
     *
     * This is also needed to update announcements in the DB when refreshing which will
     * improve performance
     *
     */
    @Transaction
    public void upsert(List<TEntity> entities) {
        List<Long> insertResults = insertIgnoringConflicts(entities);
        List<TEntity> toUpdate = new ArrayList<>();

        // For any of the inserts that failed, if they failed because of a foreign key exception
        // (in which case the status code is -1), then update them instead
        for(int index = 0; index < insertResults.size(); index++) {
            if(insertResults.get(index) == -1)
                toUpdate.add(entities.get(index));
        }

        if(!toUpdate.isEmpty())
            update(toUpdate);
    }

}
