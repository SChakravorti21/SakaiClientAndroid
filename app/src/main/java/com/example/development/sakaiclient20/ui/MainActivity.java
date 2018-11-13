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
import com.example.development.sakaiclient20.persistence.entities.Assignment;
import com.example.development.sakaiclient20.persistence.entities.Course;
import com.example.development.sakaiclient20.repositories.AssignmentRepository;

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
                                Log.d("Sit", site.title);
                        },
                        Throwable::printStackTrace
                );

    }
}
