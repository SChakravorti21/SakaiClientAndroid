package com.sakaimobile.development.sakaiclient20.persistence;

import android.content.Context;

import com.sakaimobile.development.sakaiclient20.persistence.access.AnnouncementDao;
import com.sakaimobile.development.sakaiclient20.persistence.access.AssignmentDao;
import com.sakaimobile.development.sakaiclient20.persistence.access.AttachmentDao;
import com.sakaimobile.development.sakaiclient20.persistence.access.CourseDao;
import com.sakaimobile.development.sakaiclient20.persistence.access.GradeDao;
import com.sakaimobile.development.sakaiclient20.persistence.access.ResourceDao;
import com.sakaimobile.development.sakaiclient20.persistence.access.SitePageDao;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Announcement;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Assignment;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Attachment;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Course;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Grade;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Resource;
import com.sakaimobile.development.sakaiclient20.persistence.entities.SitePage;
import com.sakaimobile.development.sakaiclient20.persistence.typeconverters.DateConverter;
import com.sakaimobile.development.sakaiclient20.persistence.typeconverters.TermConverter;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

/**
 * Created by Development on 8/5/18.
 */

@Database(entities = {
        Course.class,
        SitePage.class,
        Grade.class,
        Assignment.class,
        Attachment.class,
        Announcement.class,
        Resource.class
}, version = 10)
@TypeConverters({DateConverter.class, TermConverter.class})
public abstract class SakaiDatabase extends RoomDatabase {

    private static final Object lock = new Object();
    private static volatile SakaiDatabase mInstance;
    private static final String DB_NAME = "Sakai.db";

    public abstract GradeDao getGradeDao();
    public abstract CourseDao getCourseDao();
    public abstract ResourceDao getResourceDao();
    public abstract SitePageDao getSitePageDao();
    public abstract AssignmentDao getAssignmentDao();
    public abstract AttachmentDao getAttachmentDao();
    public abstract AnnouncementDao getAnnouncementDao();

    public static SakaiDatabase getInstance(Context context) {
        if (mInstance == null) {
            synchronized (lock) {
                if (mInstance == null) {
                    mInstance = Room.databaseBuilder(context, SakaiDatabase.class, DB_NAME)
                                    .fallbackToDestructiveMigration()
                                    .build();
                }
            }
        }

        return mInstance;
    }
}
