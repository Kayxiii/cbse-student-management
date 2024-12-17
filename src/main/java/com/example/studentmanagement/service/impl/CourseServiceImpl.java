package com.example.studentmanagement.service.impl;

import com.example.studentmanagement.entity.Course;
import com.example.studentmanagement.entity.Enrollment;
import com.example.studentmanagement.repository.CourseRepository;
import com.example.studentmanagement.repository.EnrollmentRepository;
import com.example.studentmanagement.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    @Autowired
    public CourseServiceImpl(CourseRepository courseRepository, EnrollmentRepository enrollmentRepository) {
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @Override
    public Course saveCourse(Course course) {
        return courseRepository.save(course);
    }

    @Override
    public Course getCourseById(Long id) {
        return courseRepository.findById(id).orElse(null);
    }

    @Override
    public Course updateCourse(Course course) {
        return courseRepository.save(course);
    }

    @Override
    public void deleteCourseById(Long id) {
        // Delete associated enrollments
        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(id);
        enrollmentRepository.deleteAll(enrollments);

        // Delete the course
        courseRepository.deleteById(id);
    }

    @Override
    public List<Course> getEnrolledCoursesByStudent(Long studentId) {
        return courseRepository.findByStudentId(studentId); // Custom repository query
    }

    @Override
    public List<Course> getAvailableCourses() {
        return courseRepository.findAll().stream()
                .filter(course -> course.getRegistrationStatus().equals("OPEN"))
                .collect(Collectors.toList());
    }

}