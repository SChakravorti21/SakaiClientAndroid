package com.sakaimobile.development.sakaiclient20.ui.helpers;

import com.sakaimobile.development.sakaiclient20.persistence.entities.Assignment;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Course;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public final class AssignmentSortingUtils {

    private static final int HEAP_INIT_SIZE = 20;

    public static void sortCourseAssignments(List<List<Course>> coursesByTerm) {
        if(coursesByTerm == null)
            return;

        for(List<Course> term : coursesByTerm)
            for(Course course : term)
                // Sort the assignments just within this course
                // Multiply the result of compareTo by -1 since
                // we want assignments sorted in _reverse_ chronological order
                Collections.sort(course.assignments, (assignment1, assignment2) ->
                    -1 * assignment1.dueTime.compareTo(assignment2.dueTime)
                );
    }

    public static List<List<Assignment>> sortAssignmentsByTerm(List<List<Course>> coursesByTerm) {
        if(coursesByTerm == null)
            return null;

        List<List<Assignment>> assignments = new ArrayList<>();

        for(List<Course> term : coursesByTerm) {
            PriorityQueue<Assignment> assignmentsHeap = new PriorityQueue<>(HEAP_INIT_SIZE,
                (assignment1, assignment2) -> -1 * assignment1.dueTime.compareTo(assignment2.dueTime)
            );

            for(Course course : term) {
                for (Assignment assignment : course.assignments) {
                    // Set the assignment's term to the respective course's
                    // term (this is necessary for the assignment fragment to show
                    // the correct term name in the tree nodes)
                    assignment.term = course.term;
                    assignmentsHeap.offer(assignment);
                }
            }

            List<Assignment> sorted = new ArrayList<>();
            while(!assignmentsHeap.isEmpty())
                sorted.add(assignmentsHeap.poll());

            assignments.add(sorted);
        }

        return assignments;
    }

}
