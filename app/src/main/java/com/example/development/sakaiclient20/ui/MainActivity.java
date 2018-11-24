package com.example.development.sakaiclient20.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.development.sakaiclient20.R;
import com.example.development.sakaiclient20.networking.services.AssignmentsService;
import com.example.development.sakaiclient20.networking.services.CoursesService;
import com.example.development.sakaiclient20.networking.services.ServiceFactory;
import com.example.development.sakaiclient20.persistence.SakaiDatabase;
import com.example.development.sakaiclient20.persistence.access.AssignmentDao;
import com.example.development.sakaiclient20.persistence.access.AttachmentDao;
import com.example.development.sakaiclient20.persistence.access.CourseDao;
import com.example.development.sakaiclient20.persistence.access.SitePageDao;
import com.example.development.sakaiclient20.persistence.entities.Assignment;
import com.example.development.sakaiclient20.persistence.entities.Course;
import com.example.development.sakaiclient20.repositories.AssignmentRepository;
import com.example.development.sakaiclient20.repositories.CourseRepository;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AssignmentsService assignmentsService = ServiceFactory.getService(this, AssignmentsService.class);
        AssignmentDao assignmentDao = SakaiDatabase.getInstance(this).getAssignmentDao();
        AttachmentDao attachmentDao = SakaiDatabase.getInstance(this).getAttachmentDao();
        AssignmentRepository repo = new AssignmentRepository(assignmentDao, attachmentDao, assignmentsService);

//        repo.getAllAssignments(true)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                        assignments -> {
//                            for(Assignment assignment : assignments) {
//                                Log.d("Assignment", assignment.title);
//                            }
//                        },
//                        error -> error.printStackTrace()
//                );

        CourseDao courseDao = SakaiDatabase.getInstance(this).getCourseDao();
        SitePageDao sitePageDao = SakaiDatabase.getInstance(this).getSitePageDao();
        CoursesService coursesService = ServiceFactory.getService(this, CoursesService.class);
        CourseRepository courseRepository = new CourseRepository(courseDao, sitePageDao, coursesService);
        courseRepository.getCoursesSortedByTerm(false)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        coursesByTerm -> {
                            for(List<Course> courses : coursesByTerm) {
                                for(Course course : courses) {
                                    Log.d("Courses", course.title);
                                }
                            }
                        },
                        err -> {
                            err.printStackTrace();
                        }
                );

    }
}
