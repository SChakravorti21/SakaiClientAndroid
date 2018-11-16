package com.example.development.sakaiclient20.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.development.sakaiclient20.R;
import com.example.development.sakaiclient20.models.Term;
import com.example.development.sakaiclient20.networking.services.AssignmentsService;
import com.example.development.sakaiclient20.networking.services.CoursesService;
import com.example.development.sakaiclient20.networking.services.GradesService;
import com.example.development.sakaiclient20.networking.services.ServiceFactory;
import com.example.development.sakaiclient20.persistence.SakaiDatabase;
import com.example.development.sakaiclient20.persistence.access.AssignmentDao;
import com.example.development.sakaiclient20.persistence.access.AttachmentDao;
import com.example.development.sakaiclient20.persistence.access.CourseDao;
import com.example.development.sakaiclient20.persistence.access.GradeDao;
import com.example.development.sakaiclient20.persistence.entities.Assignment;
import com.example.development.sakaiclient20.persistence.entities.Course;
import com.example.development.sakaiclient20.persistence.entities.Grade;
import com.example.development.sakaiclient20.repositories.AssignmentRepository;
import com.example.development.sakaiclient20.repositories.GradesRepository;

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

        repo.getAllAssignments(true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        assignments -> {
                            for(Assignment assignment : assignments) {
                                Log.d("Assignment", assignment.title);
                            }
                        },
                        error -> error.printStackTrace()
                );

        CoursesService coursesService = ServiceFactory.getService(this, CoursesService.class);
        coursesService.getAllSites()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sites -> {
                            for(Course site : sites.getCourses())
                                Log.d("Site", site.title);
                        },
                        Throwable::printStackTrace
                );


        GradesService gradesService = ServiceFactory.getService(this, GradesService.class);
        GradeDao gradeDao = SakaiDatabase.getInstance(this).getGradeDao();
        CourseDao courseDao = SakaiDatabase.getInstance(this).getCourseDao();
        GradesRepository gradesRepository = new GradesRepository(gradeDao, courseDao, gradesService);

//        gradesRepository.getGradesForSite("cbc83f22-e436-4e54-b88a-14e6e4dd621b", true)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                        grades -> {
//                            for(Grade g : grades) {
//                                System.out.println("grade item: " + g.itemName);
//                            }
//                        }
//                );

        gradesRepository.getAllGradesSortedByTerm(true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        treemap -> {
                            for(Term term : treemap.descendingKeySet()) {
                                for(String siteId : treemap.get(term).keySet()) {
                                    for(Grade g : treemap.get(term).get(siteId)) {
                                        System.out.println(g.itemName);
                                    }
                                }
                            }
                        }
                );

    }
}
