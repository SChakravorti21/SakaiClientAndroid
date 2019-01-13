package com.sakaimobile.development.sakaiclient20.persistence.access;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.sakaimobile.development.sakaiclient20.persistence.composites.CourseWithAllData;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Course;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

/**
 * Created by Development on 8/11/18.
 */
@Dao
public abstract class CourseDao implements BaseDao<Course> {

    @Transaction
    @Query("SELECT * FROM courses")
    public abstract Flowable<List<CourseWithAllData>> getAllCourses();

    @Transaction
    @Query("SELECT * FROM courses WHERE siteId = :siteId LIMIT 1")
    public abstract Flowable<CourseWithAllData> getCourse(String siteId);

    @Query("SELECT siteId FROM courses")
    public abstract Single<List<String>> getAllSiteIds();

    ////////////////////////////////////////
    //  PROTOCOL FOR REMOVING OLD COURSES
    ////////////////////////////////////////

    @Query("DELETE FROM courses WHERE siteId NOT IN (:availableSiteIds)")
    public abstract int removeExtraneousCourses(Set<String> availableSiteIds);

    public boolean removeExtraneousCourses(List<Course> courses) {
        Set<String> siteIds = new HashSet<>();
        for(Course course : courses)
            siteIds.add(course.siteId);
        return removeExtraneousCourses(siteIds) > 0;
    }

    ////////////////////////////////////////
    //  PROTOCOL FOR UPSERTING COURSES
    ////////////////////////////////////////

    /**
     * @return the status code of each insert for the corresponding entity (-1 if failed)
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract List<Long> insertIgnoringConflicts(List<Course> courses);

    /**
     * An UPSERT implementation for upserting courses. This is necessary because
     * we do NOT want to replace existing courses on insert, as that would cascade down the
     * foreign keys and delete related entities (such as grades, assignments, etc).
     * Since there is no UPSERT OnConflictStrategy, this behavior must be implemented ourselves.
     *
     * Consider moving this UPSERT implementation to {@see BaseDao} if such behavior is needed
     * for other entities (although it is not required as of writing this implementation).
     * @param courses the courses to upsert
     */
    public void upsert(List<Course> courses) {
        List<Long> insertResults = insertIgnoringConflicts(courses);
        List<Course> toUpdate = new ArrayList<>();

        // For any of the inserts that failed, if they failed because of a foreign key exception
        // (in which case the status code is -1), then update them instead
        for(int index = 0; index < insertResults.size(); index++) {
            if(insertResults.get(index) == -1)
                toUpdate.add(courses.get(index));
        }

        if(!toUpdate.isEmpty())
            update(toUpdate);
    }

}

