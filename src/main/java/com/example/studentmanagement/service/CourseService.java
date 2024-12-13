package com.example.smsw.service;

import com.example.smsw.entity.Course;
import java.util.List;

public interface CourseService {
    List<Course> getAllCourses();
    Course saveCourse(Course course);
    Course getCourseById(Long id);
    Course updateCourse(Course course);
    void deleteCourseById(Long id);

    List<Course> getEnrolledCoursesByStudent(Long studentId);

    List<Course> getAvailableCourses();
}