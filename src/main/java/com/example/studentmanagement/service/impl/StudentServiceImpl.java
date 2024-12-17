package com.example.studentmanagement.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.studentmanagement.entity.Enrollment;
import com.example.studentmanagement.repository.CourseRepository;
import com.example.studentmanagement.entity.Course;
import com.example.studentmanagement.entity.Student;
import com.example.studentmanagement.repository.EnrollmentRepository;
import com.example.studentmanagement.repository.StudentRepository;
import com.example.studentmanagement.service.StudentService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {

	private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Autowired
    public StudentServiceImpl(StudentRepository studentRepository, CourseRepository courseRepository, EnrollmentRepository enrollmentRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

	@Override
	public List<Student> getAllStudents() {
		return studentRepository.findAll();
	}

	@Override
	public Student saveStudent(Student student) {
		validateUniqueStudentId(student.getStudentId(),null);
		return studentRepository.save(student);
	}

	@Override
	public Student getStudentById(Long id) {
		// Handle Optional properly
		return studentRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Student not found with ID: " + id));
	}

	@Override
	public Student updateStudent(Student student) {
		validateUniqueStudentId(student.getStudentId(), student.getId());
		return studentRepository.save(student);
	}

    @Override
    @Transactional
    public void deleteStudentById(Long id) {
        // Step 1: Fetch the student and validate existence
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + id));

        // Step 2: Fetch enrollments for the student
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(id);

        // Step 3: Update 'actual' count for each course associated with the enrollments
        for (Enrollment enrollment : enrollments) {
            Course course = enrollment.getCourse();

            // Decrease the actual count (ensure it does not go below zero)
            int updatedActual = Math.max(course.getActual() - 1, 0);
            course.setActual(updatedActual);

            // Save the updated course
            courseRepository.save(course);
        }

        // Step 4: Delete all enrollments for the student
        enrollmentRepository.deleteAll(enrollments);

        // Step 5: Delete the student
        studentRepository.delete(student);
    }


    /**
	 * Validates that the given studentId is unique.
	 *
	 * @param studentId The studentId to check for uniqueness.
	 * @param currentId The ID of the student being updated (null for new students).
	 */
    private void validateUniqueStudentId(String studentId, Long currentId) {
        List<Student> existingStudents = studentRepository.findAll();
        for (Student existingStudent : existingStudents) {
            // Check if studentId matches and the ID is not the current student's ID
            if (existingStudent.getStudentId().equals(studentId) &&
                    (currentId == null || !existingStudent.getId().equals(currentId))) {
                throw new IllegalArgumentException("Student ID already exists. Please use a unique ID.");
            }
        }
    }

}
