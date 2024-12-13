package com.example.smsw.service.impl;

import com.example.smsw.entity.Course;
import com.example.smsw.repository.CourseRepository;
import com.example.smsw.service.CourseService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;

    public CourseServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
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