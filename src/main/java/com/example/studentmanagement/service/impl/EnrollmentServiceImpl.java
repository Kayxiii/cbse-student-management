package com.example.smsw.service.impl;

import com.example.smsw.entity.Course;
import com.example.smsw.entity.Enrollment;
import com.example.smsw.entity.Student;
import com.example.smsw.repository.CourseRepository;
import com.example.smsw.repository.EnrollmentRepository;
import com.example.smsw.repository.StudentRepository;
import com.example.smsw.service.EnrollmentService;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public EnrollmentServiceImpl(EnrollmentRepository enrollmentRepository, StudentRepository studentRepository, CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public void enrollStudentToCourses(Long studentId, List<Long> courseIds) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<Enrollment> existingEnrollments = enrollmentRepository.findByStudentId(studentId);
        List<Course> selectedCourses = courseRepository.findAllById(courseIds);

        if (selectedCourses.isEmpty()) {
            throw new RuntimeException("No courses selected for enrollment.");
        }

        // Check for time clashes among selected courses
        for (int i = 0; i < selectedCourses.size(); i++) {
            for (int j = i + 1; j < selectedCourses.size(); j++) {
                Course course1 = selectedCourses.get(i);
                Course course2 = selectedCourses.get(j);

                if (course1.getDay().equals(course2.getDay()) &&
                        !(course1.getTimeEnd().compareTo(course2.getTimeStart()) <= 0 ||
                                course2.getTimeEnd().compareTo(course1.getTimeStart()) <= 0)) {
                    throw new RuntimeException("Time conflict detected between " +
                            course1.getCourseName() + " and " + course2.getCourseName());
                }
            }
        }

        // Check for duplicate course codes among selected courses
        for (int i = 0; i < selectedCourses.size(); i++) {
            for (int j = i + 1; j < selectedCourses.size(); j++) {
                Course course1 = selectedCourses.get(i);
                Course course2 = selectedCourses.get(j);

                if (course1.getCourseCode().equals(course2.getCourseCode())) {
                    throw new RuntimeException("Duplicate course code detected: " + course1.getCourseCode());
                }
            }
        }

        // Check for duplicate occurrences of the same course code in existing enrollments
        for (Course course : selectedCourses) {
            if (existingEnrollments.stream()
                    .anyMatch(enrollment -> enrollment.getCourse().getCourseCode().equals(course.getCourseCode()))) {
                throw new RuntimeException("Duplicate occurrence detected for course code: " + course.getCourseCode());
            }
        }

        // Enroll selected courses
        for (Course course : selectedCourses) {
            Enrollment enrollment = new Enrollment();
            enrollment.setStudent(student);
            enrollment.setCourse(course);
            enrollment.setEnrollmentDate("2024-12-15"); // Example date
            enrollmentRepository.save(enrollment);

            // Increment the actual count of the course
            course.setActual(course.getActual() + 1);
            courseRepository.save(course);
        }
    }


    @Override
    public List<Enrollment> getEnrollmentsByStudent(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }

    @Override
    public List<Course> getEnrolledCoursesByStudent(Long studentId) {
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);
        return enrollments.stream()
                .map(Enrollment::getCourse)
                .toList();
    }

}