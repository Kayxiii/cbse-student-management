package com.example.studentmanagement.service.impl;

import com.example.studentmanagement.entity.Course;
import com.example.studentmanagement.entity.Enrollment;
import com.example.studentmanagement.entity.Student;
import com.example.studentmanagement.repository.CourseRepository;
import com.example.studentmanagement.repository.EnrollmentRepository;
import com.example.studentmanagement.repository.StudentRepository;
import com.example.studentmanagement.service.EnrollmentService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Service
@Transactional
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    @Autowired
    public EnrollmentServiceImpl(EnrollmentRepository enrollmentRepository,
                                 StudentRepository studentRepository,
                                 CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public void enrollStudentToCourses(Long studentId, List<Long> courseIds) {
        if (courseIds == null || courseIds.isEmpty()) {
            throw new RuntimeException("No courses selected for enrollment.");
        }

        // Fetch student and validate existence
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + studentId));

        // Fetch existing enrollments and selected courses
        List<Enrollment> existingEnrollments = enrollmentRepository.findByStudentId(studentId);
        List<Course> selectedCourses = courseRepository.findAllById(courseIds);

        if (selectedCourses.isEmpty()) {
            throw new RuntimeException("No valid courses found for the provided course IDs.");
        }

        // Validate selected courses
        validateSelectedCourses(existingEnrollments, selectedCourses);

        // Enroll the student in the validated courses
        for (Course course : selectedCourses) {
            enrollStudentInCourse(student, course);
        }
    }

    private void validateSelectedCourses(List<Enrollment> existingEnrollments, List<Course> selectedCourses) {
        // Use a set to track unique course codes for duplicates within selected courses
        Set<String> uniqueCourseCodes = new HashSet<>();

        for (Course selectedCourse : selectedCourses) {
            // Check if the course has space for enrollment
            if (selectedCourse.getActual() >= selectedCourse.getTarget()) {
                throw new RuntimeException("Course " + selectedCourse.getCourseName() + " has reached its maximum capacity.");
            }

            // Check for duplicate course codes within the selected courses
            if (!uniqueCourseCodes.add(selectedCourse.getCourseCode())) {
                throw new RuntimeException("Duplicate course code found within the selected courses: " + selectedCourse.getCourseCode());
            }

            // Check for duplicate course codes in existing enrollments
            if (existingEnrollments.stream()
                    .anyMatch(enrollment -> enrollment.getCourse().getCourseCode().equals(selectedCourse.getCourseCode()))) {
                throw new RuntimeException("Student is already enrolled in a course with code: " + selectedCourse.getCourseCode());
            }

            // Check for time conflicts with existing enrollments
            for (Enrollment enrollment : existingEnrollments) {
                Course existingCourse = enrollment.getCourse();
                validateTimeConflict(existingCourse, selectedCourse);
            }
        }
    }

    private void validateTimeConflict(Course existingCourse, Course selectedCourse) {
        // Check if the courses are on the same day
        if (existingCourse.getDay().equals(selectedCourse.getDay())) {
            // Check for same start time
            if (existingCourse.getTimeStart().equals(selectedCourse.getTimeStart())) {
                throw new RuntimeException("Time conflict: Student is already enrolled in a course starting at "
                        + existingCourse.getTimeStart());
            }

            // Check for overlapping time intervals
            if (!(existingCourse.getTimeEnd().compareTo(selectedCourse.getTimeStart()) <= 0 ||
                    selectedCourse.getTimeEnd().compareTo(existingCourse.getTimeStart()) <= 0)) {
                throw new RuntimeException("Time conflict detected between enrolled course: " + existingCourse.getCourseName() +
                        " and the course to be enrolled: " + selectedCourse.getCourseName());
            }
        }
    }

    private void enrollStudentInCourse(Student student, Course course) {
        // Create and save a new enrollment
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrollmentDate(LocalDate.now().toString()); // Use current date
        enrollmentRepository.save(enrollment);

        // Increment the actual count of the course
        course.setActual(course.getActual() + 1);
        courseRepository.save(course);
    }

    @Override
    public List<Enrollment> getEnrollmentsByStudent(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }

    @Override
    public List<Course> getEnrolledCoursesByStudent(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId).stream()
                .map(Enrollment::getCourse)
                .distinct()
                .toList();
    }

    @Override
    public List<Course> getAvailableCourses(Long studentId) {
        // Fetch all courses and exclude the ones the student is already enrolled in
        List<Course> allCourses = courseRepository.findAll();

        // Get enrolled courses, ensuring it is not null
        List<Course> enrolledCourses = getEnrolledCoursesByStudent(studentId);

        return allCourses.stream()
                .filter(course -> enrolledCourses == null || !enrolledCourses.contains(course))
                .toList();
    }

}
