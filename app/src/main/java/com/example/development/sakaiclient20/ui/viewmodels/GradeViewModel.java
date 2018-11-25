package com.example.development.sakaiclient20.ui.viewmodels;

import com.example.development.sakaiclient20.repositories.CourseRepository;
import com.example.development.sakaiclient20.repositories.GradesRepository;

public class GradeViewModel extends BaseViewModel {

    private GradesRepository gradesRepository;

    public GradeViewModel(CourseRepository courseRepository, GradesRepository gradesRepository) {
        super(courseRepository);
        this.gradesRepository = gradesRepository;
    }

    @Override
    void refreshData() {

    }
}
