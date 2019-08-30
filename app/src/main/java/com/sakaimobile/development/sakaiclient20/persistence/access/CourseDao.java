package com.sakaimobile.development.sakaiclient20.persistence.access;

import com.sakaimobile.development.sakaiclient20.persistence.composites.CourseWithAllData;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Course;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;
import io.reactivex.Flowable;
import io.reactivex.Single;

/**
 * Created by Development on 8/11/18.
 */
@Dao
public abstract class CourseDao extends BaseDao<Course> {

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


}

