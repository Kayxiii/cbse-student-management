package com.example.smsw.service;

import com.example.smsw.entity.Course;
import com.example.smsw.entity.Enrollment;

import java.util.List;

public interface EnrollmentService {
    void enrollStudentToCourses(Long studentId, List<Long> courseId);
    List<Enrollment> getEnrollmentsByStudent(Long studentId);
    List<Course> getEnrolledCoursesByStudent(Long studentId);
}
