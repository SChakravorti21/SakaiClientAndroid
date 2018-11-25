package com.example.development.sakaiclient20.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.development.sakaiclient20.R;
import com.example.development.sakaiclient20.networking.services.AssignmentsService;
import com.example.development.sakaiclient20.networking.services.CoursesService;
import com.example.development.sakaiclient20.networking.services.GradesService;
import com.example.development.sakaiclient20.networking.services.ServiceFactory;
import com.example.development.sakaiclient20.persistence.SakaiDatabase;
import com.example.development.sakaiclient20.persistence.access.AssignmentDao;
import com.example.development.sakaiclient20.persistence.access.AttachmentDao;
import com.example.development.sakaiclient20.persistence.access.CourseDao;
import com.example.development.sakaiclient20.persistence.access.GradeDao;
import com.example.development.sakaiclient20.persistence.access.SitePageDao;
import com.example.development.sakaiclient20.persistence.entities.Assignment;
import com.example.development.sakaiclient20.persistence.entities.Course;
import com.example.development.sakaiclient20.persistence.entities.Grade;
import com.example.development.sakaiclient20.repositories.AssignmentRepository;
import com.example.development.sakaiclient20.repositories.CourseRepository;
import com.example.development.sakaiclient20.repositories.GradesRepository;
import com.example.development.sakaiclient20.ui.viewmodels.CourseViewModel;
import com.example.development.sakaiclient20.ui.viewmodels.GradeViewModel;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    LiveData beingObserved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        CourseDao courseDao = SakaiDatabase.getInstance(this).getCourseDao();
        SitePageDao sitePageDao = SakaiDatabase.getInstance(this).getSitePageDao();
        CoursesService coursesService = ServiceFactory.getService(this, CoursesService.class);
        final CourseRepository courseRepository = new CourseRepository(courseDao, sitePageDao, coursesService);
        CourseViewModel courseViewModel = ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new CourseViewModel(courseRepository);
            }
        }).get(CourseViewModel.class);


        courseViewModel.getCoursesByTerm()
                .observe(this, courses -> {
                    for (List<Course> term : courses) {
                        for (Course course : term) {
//                        Log.d("Courses", course.title);
                        }
                    }
                });
        beingObserved = courseViewModel.getCoursesByTerm();

        findViewById(R.id.update_courses).setOnClickListener(view -> {
            courseViewModel.refreshData();
        });


        GradeDao gradeDao = SakaiDatabase.getInstance(this).getGradeDao();
        GradesService gradesService = ServiceFactory.getService(this, GradesService.class);
        final GradesRepository gradesRepository = new GradesRepository(gradeDao, gradesService);
        GradeViewModel gradeViewModel = ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new GradeViewModel(courseRepository, gradesRepository);
            }
        }).get(GradeViewModel.class);

//        gradeViewModel.getGradesForSite("19433b30-2511-40f3-8f74-ff60940decd4")
//                .observe(this, grades -> {
//                    for (Grade g : grades) {
//                        Log.d("GRADES", g.itemName);
//                    }
//                });

        gradeViewModel.refreshAllGrades();
        gradeViewModel.getCoursesByTerm()
                .observe(this, courses -> {
                    for (List<Course> term : courses) {
                        for (Course course : term) {
                            for(Grade grade : course.grades) {
                                Log.d("GRADES", String.format("grade %s, course %s", grade.itemName, course.title));
                            }
                        }
                    }
                });

    }

    @Override
    protected void onPause() {
        super.onPause();
        beingObserved.removeObservers(this);
    }
}
