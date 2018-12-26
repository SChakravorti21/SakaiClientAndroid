package com.sakaimobile.development.sakaiclient20.dependency_injection;

import com.sakaimobile.development.sakaiclient20.networking.services.AnnouncementsService;
import com.sakaimobile.development.sakaiclient20.networking.services.AssignmentsService;
import com.sakaimobile.development.sakaiclient20.networking.services.CoursesService;
import com.sakaimobile.development.sakaiclient20.networking.services.GradeService;
import com.sakaimobile.development.sakaiclient20.networking.services.ResourcesService;
import com.sakaimobile.development.sakaiclient20.persistence.access.AnnouncementDao;
import com.sakaimobile.development.sakaiclient20.persistence.access.AssignmentDao;
import com.sakaimobile.development.sakaiclient20.persistence.access.AttachmentDao;
import com.sakaimobile.development.sakaiclient20.persistence.access.CourseDao;
import com.sakaimobile.development.sakaiclient20.persistence.access.GradeDao;
import com.sakaimobile.development.sakaiclient20.persistence.access.ResourceDao;
import com.sakaimobile.development.sakaiclient20.persistence.access.SitePageDao;
import com.sakaimobile.development.sakaiclient20.repositories.AnnouncementRepository;
import com.sakaimobile.development.sakaiclient20.repositories.AssignmentRepository;
import com.sakaimobile.development.sakaiclient20.repositories.CourseRepository;
import com.sakaimobile.development.sakaiclient20.repositories.GradeRepository;
import com.sakaimobile.development.sakaiclient20.repositories.ResourceRepository;

import dagger.Module;
import dagger.Provides;

@Module(includes = {DaoModule.class, ServiceModule.class})
class RepositoryModule {

    @Provides
    static CourseRepository provideCourseRepository(
            CourseDao courseDao,
            SitePageDao sitePageDao,
            CoursesService coursesService) {
        return new CourseRepository(courseDao, sitePageDao, coursesService);
    }

    @Provides
    static ResourceRepository provideResourceRepository(
            ResourceDao dao,
            ResourcesService service) {
        return new ResourceRepository(dao, service);
    }

    @Provides
    static AssignmentRepository provideAssignmentRepository(
            AssignmentDao assignmentDao,
            AttachmentDao attachmentDao,
            AssignmentsService assignmentsService) {
        return new AssignmentRepository(assignmentDao, attachmentDao, assignmentsService);
    }

    @Provides
    static AnnouncementRepository provideAnnouncementRepository(
            AnnouncementDao announcementDao,
            AttachmentDao attachmentDao,
            AnnouncementsService announcementsService) {
        return new AnnouncementRepository(announcementDao, attachmentDao, announcementsService);
    }

    @Provides
    static GradeRepository provideGradesRepository(
            GradeDao gradeDao,
            GradeService gradesService) {
        return new GradeRepository(gradeDao, gradesService);
    }

}
