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

import dagger.Module;
import dagger.Provides;

@Module
class DaoModule {

    @Provides static CourseDao provideCourseDao(Context context) {
        return SakaiDatabase.getInstance(context).getCourseDao();
    }

    @Provides static ResourceDao provideResourceDao(Context context) {
        return SakaiDatabase.getInstance(context).getResourceDao();
    }

    @Provides static SitePageDao provideSitePageDao(Context context) {
        return SakaiDatabase.getInstance(context).getSitePageDao();
    }

    @Provides static AnnouncementDao provideAnnouncementDao(Context context) {
        return SakaiDatabase.getInstance(context).getAnnouncementDao();
    }

    @Provides static AssignmentDao provideAssignmentDao(Context context) {
        return SakaiDatabase.getInstance(context).getAssignmentDao();
    }

    @Provides static AttachmentDao provideAttachmentDao(Context context) {
        return SakaiDatabase.getInstance(context).getAttachmentDao();
    }

    @Provides static GradeDao provideGradeDao(Context context) {
        return SakaiDatabase.getInstance(context).getGradeDao();
    }
}
