package com.example.development.sakaiclient20.dependency_injection;

import android.content.Context;

import com.example.development.sakaiclient20.persistence.SakaiDatabase;
import com.example.development.sakaiclient20.persistence.access.AssignmentDao;
import com.example.development.sakaiclient20.persistence.access.AttachmentDao;
import com.example.development.sakaiclient20.persistence.access.CourseDao;
import com.example.development.sakaiclient20.persistence.access.SitePageDao;

import dagger.Module;
import dagger.Provides;

@Module
class DaoModule {

    @Provides static CourseDao provideCourseDao(Context context) {
        return SakaiDatabase.getInstance(context).getCourseDao();
    }

    @Provides static SitePageDao provideSitePageDao(Context context) {
        return SakaiDatabase.getInstance(context).getSitePageDao();
    }

    @Provides static AssignmentDao provideAssignmentDao(Context context) {
        return SakaiDatabase.getInstance(context).getAssignmentDao();
    }

    @Provides static AttachmentDao provideAttachmentDao(Context context) {
        return SakaiDatabase.getInstance(context).getAttachmentDao();
    }

}
