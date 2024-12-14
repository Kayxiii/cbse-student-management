package com.example.studentmanagement.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.studentmanagement.entity.Student;
import com.example.studentmanagement.repository.StudentRepository;
import com.example.studentmanagement.service.StudentService;

@Service
public class StudentServiceImpl implements StudentService{

	private StudentRepository studentRepository;

	@Autowired
	public StudentServiceImpl(StudentRepository studentRepository) {
		super();
		this.studentRepository = studentRepository;
	}
	
	//updating data whenever service will called.


	@Override
	public List<Student> getAllStudents() {
		
		return studentRepository.findAll();
	}


	@Override
	public Student saveStudent(Student student) {
		validateUniqueStudentId(student.getStudentId(),null);
		return studentRepository.save(student);
	}

	//to get data from the database
	@Override
	public Student getStudentById(Long id) {
		return studentRepository.findById(id).get();
	}


	@Override
	public Student updateStudent(Student student) {
		validateUniqueStudentId(student.getStudentId(), student.getId());
		return studentRepository.save(student);
	}


	@Override
	public void deleteStudentById(Long id) {
		studentRepository.deleteById(id);
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
