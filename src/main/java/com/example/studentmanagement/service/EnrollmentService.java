package com.example.studentmanagement.service;

import com.example.studentmanagement.entity.Course;
import com.example.studentmanagement.entity.Enrollment;
import com.example.studentmanagement.entity.Student;

import java.util.List;

public interface EnrollmentService {
    void enrollStudentToCourses(Long studentId, List<Long> courseId);
    List<Enrollment> getEnrollmentsByStudent(Long studentId);
    List<Course> getEnrolledCoursesByStudent(Long studentId);

    List<Course> getAvailableCourses(Long studentsId);

}
