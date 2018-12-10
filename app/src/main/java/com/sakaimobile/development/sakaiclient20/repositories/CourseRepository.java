package com.sakaimobile.development.sakaiclient20.repositories;

import com.sakaimobile.development.sakaiclient20.models.Term;
import com.sakaimobile.development.sakaiclient20.models.sakai.courses.CoursesResponse;
import com.sakaimobile.development.sakaiclient20.networking.services.CoursesService;
import com.sakaimobile.development.sakaiclient20.persistence.access.CourseDao;
import com.sakaimobile.development.sakaiclient20.persistence.access.SitePageDao;
import com.sakaimobile.development.sakaiclient20.persistence.composites.CourseWithAllData;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Assignment;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Course;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import io.reactivex.Completable;
import io.reactivex.Single;

public class CourseRepository {

    private CourseDao courseDao;
    private SitePageDao sitePageDao;
    private CoursesService coursesService;

    public CourseRepository(
            CourseDao courseDao,
            SitePageDao sitePageDao,
            CoursesService coursesService
    ) {
        this.courseDao = courseDao;
        this.sitePageDao = sitePageDao;
        this.coursesService = coursesService;
    }

    public Single<Course> getCourse(String siteId) {
        return courseDao.getCourse(siteId)
                .firstOrError()
                .map(this::flattenCompositeToEntity);
    }

    public Single<List<List<Course>>> getCoursesSortedByTerm() {
        return courseDao.getAllCourses()
                .firstOrError()
                .toObservable()
                .flatMapIterable(courses -> courses)
                .map(this::flattenCompositeToEntity)
                .toList()
                .map(this::sortCoursesByTerm);
    }

    public Completable refreshCourse(String siteId) {
        return coursesService.getSite(siteId)
                // Collections.singletonList creates an immutable list
                // for a single element
                .map(Collections::singletonList)
                .map(this::persistCourses)
                .ignoreElement();
    }

    public Completable refreshAllCourses() {
        return coursesService.getAllSites()
                .map(CoursesResponse::getCourses)
                .map(this::persistCourses)
                .ignoreElement();
    }

    private List<Course> persistCourses(List<Course> courses) {
        courseDao.insert(courses);
        for(Course course : courses)
            sitePageDao.insert(course.sitePages);

        return courses;
    }

    private Course flattenCompositeToEntity(CourseWithAllData courseWithAllData) {
        Course entity = courseWithAllData.course;
        entity.sitePages = courseWithAllData.sitePages;
        entity.grades = courseWithAllData.grades;
        entity.assignments =
            AssignmentRepository.flattenCompositesToEntities(courseWithAllData.assignments);

        entity.announcements =
                AnnouncementRepository.flattenCompositesToEntities(courseWithAllData.announcements);

        // Make sure to add the assignment site page url for the submission page to
        // be accessible
        for(Assignment assignment : entity.assignments)
            assignment.assignmentSitePageUrl = entity.assignmentSitePageUrl;

        return entity;
    }

    private List<List<Course>> sortCoursesByTerm(List<Course> courses) {
        TreeMap<Term, List<Course>> coursesByTerm = new TreeMap<>();

        for(Course course : courses) {
            Term term = course.term;
            if(coursesByTerm.containsKey(term)) {
                coursesByTerm.get(term).add(course);
            } else {
                List<Course> coursesForTerm = new ArrayList<>();
                coursesForTerm.add(course);
                coursesByTerm.put(term, coursesForTerm);
            }
        }

        List<List<Course>> coursesSortedByTerm = new ArrayList<>(coursesByTerm.size());
        // We need to go through the keySet in descending order since the
        // more recent terms have larger values (year + starting month)
        for(Term term : coursesByTerm.descendingKeySet()) {
            coursesSortedByTerm.add(coursesByTerm.get(term));
        }

        return coursesSortedByTerm;
    }
}
