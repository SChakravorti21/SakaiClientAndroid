package com.example.development.sakaiclient20.dependency_injection;

import com.example.development.sakaiclient20.networking.services.AssignmentsService;
import com.example.development.sakaiclient20.networking.services.CoursesService;
import com.example.development.sakaiclient20.networking.services.GradeService;
import com.example.development.sakaiclient20.persistence.access.AssignmentDao;
import com.example.development.sakaiclient20.persistence.access.AttachmentDao;
import com.example.development.sakaiclient20.persistence.access.CourseDao;
import com.example.development.sakaiclient20.persistence.access.GradeDao;
import com.example.development.sakaiclient20.persistence.access.SitePageDao;
import com.example.development.sakaiclient20.repositories.AssignmentRepository;
import com.example.development.sakaiclient20.repositories.CourseRepository;
import com.example.development.sakaiclient20.repositories.GradeRepository;

import dagger.Module;
import dagger.Provides;

@Module(includes = { DaoModule.class, ServiceModule.class })
class RepositoryModule {

    @Provides static CourseRepository provideCourseRepository(
            CourseDao courseDao,
            SitePageDao sitePageDao,
            CoursesService coursesService
    ) {
        return new CourseRepository(courseDao, sitePageDao, coursesService);
    }

    @Provides static AssignmentRepository provideAssignmentRepository(
            AssignmentDao assignmentDao,
            AttachmentDao attachmentDao,
            AssignmentsService assignmentsService
    ) {
        return new AssignmentRepository(assignmentDao, attachmentDao, assignmentsService);
    }

    @Provides static GradeRepository provideGradesRepository(
            GradeDao gradeDao,
            GradeService gradeService
    ) {
        return new GradeRepository(gradeDao, gradeService);
    }

}
