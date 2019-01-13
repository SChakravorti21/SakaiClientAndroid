package com.sakaimobile.development.sakaiclient20.dependency_injection;

import android.content.Context;

import com.sakaimobile.development.sakaiclient20.persistence.SakaiDatabase;
import com.sakaimobile.development.sakaiclient20.persistence.access.AnnouncementDao;
import com.sakaimobile.development.sakaiclient20.persistence.access.AssignmentDao;
import com.sakaimobile.development.sakaiclient20.persistence.access.AttachmentDao;
import com.sakaimobile.development.sakaiclient20.persistence.access.CourseDao;
import com.sakaimobile.development.sakaiclient20.persistence.access.GradeDao;
import com.sakaimobile.development.sakaiclient20.persistence.access.ResourceDao;
import com.sakaimobile.development.sakaiclient20.persistence.access.SitePageDao;

import javax.inject.Scope;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
class DaoModule {

    @Singleton
    @Provides
    static CourseDao provideCourseDao(Context context) {
        return SakaiDatabase.getInstance(context).getCourseDao();
    }

    @Singleton
    @Provides
    static ResourceDao provideResourceDao(Context context) {
        return SakaiDatabase.getInstance(context).getResourceDao();
    }

    @Singleton
    @Provides
    static SitePageDao provideSitePageDao(Context context) {
        return SakaiDatabase.getInstance(context).getSitePageDao();
    }

    @Singleton
    @Provides
    static AnnouncementDao provideAnnouncementDao(Context context) {
        return SakaiDatabase.getInstance(context).getAnnouncementDao();
    }

    @Singleton
    @Provides
    static AssignmentDao provideAssignmentDao(Context context) {
        return SakaiDatabase.getInstance(context).getAssignmentDao();
    }

    @Singleton
    @Provides
    static AttachmentDao provideAttachmentDao(Context context) {
        return SakaiDatabase.getInstance(context).getAttachmentDao();
    }

    @Singleton
    @Provides
    static GradeDao provideGradeDao(Context context) {
        return SakaiDatabase.getInstance(context).getGradeDao();
    }
}
