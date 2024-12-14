package com.example.studentmanagement.controller;

import com.example.studentmanagement.entity.Course;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.studentmanagement.entity.Student;
import com.example.studentmanagement.service.StudentService;
import com.example.studentmanagement.service.CourseService;
import com.example.studentmanagement.service.EnrollmentService;
import jakarta.servlet.http.HttpSession;

import java.util.Collections;
import java.util.List;

@Controller
public class StudentController {

	private final StudentService studentService;
	private final CourseService courseService;
	private final EnrollmentService enrollmentService;

	public StudentController(StudentService studentService, CourseService courseService, EnrollmentService enrollmentService) {
		this.studentService = studentService;
		this.courseService = courseService;
		this.enrollmentService = enrollmentService;
	}
	
	
	//handler to remove popup success message as blank 
	@PostMapping("/resetSuccessMessage")
	public ResponseEntity<Void> resetSuccessMessage(HttpSession session) {
		session.removeAttribute("successMessage");
		return ResponseEntity.ok().build();
	}
		
	
	
	
	//what should get and what should not will be done here.
	
	//handler method to handle list of students and return mode and view 
	
	@GetMapping("/students")
	public String listStudents(Model model) {
		model.addAttribute("students", studentService.getAllStudents());
		return "students";
	}
	
	
	//get the add student page
	@GetMapping("/students/new")
	public String createStudentForm(Model model) {
		
		//create student object from student form data
		Student student = new Student();
		model.addAttribute("student", student);
		return "create_student";
		
	}
	
	
	//saving student
	@PostMapping("/students")
	public String saveStudent(@ModelAttribute("student") Student student, HttpSession session) {
		studentService.saveStudent(student);
		session.setAttribute("successMessage", "Successfully Added");
		return "redirect:/students";
	}
	
	
	//get the update or edit page
	@GetMapping("/students/edit/{id}")
	public String editStudentForm(@PathVariable Long id, Model model) {
		model.addAttribute("student", studentService.getStudentById(id));
		return "edit_student";
	}
	
	
	//update data into existing table
	@PostMapping("/students/{id}")
	public String updateStudent(@PathVariable Long id,
			@ModelAttribute("student") Student student,
			Model model, HttpSession session) {
		
		//get student from database by id
		Student existingStudent = studentService.getStudentById(id);
		existingStudent.setId(id);
		existingStudent.setFirstName(student.getFirstName());
		existingStudent.setLastName(student.getLastName());
		existingStudent.setEmail(student.getEmail());
		existingStudent.setFaculty(student.getFaculty());
		existingStudent.setCourses(student.getCourses());
		existingStudent.setStudentId(student.getStudentId());
		existingStudent.setBachelor(student.getBachelor());
		
		//save update student object 
		studentService.updateStudent(existingStudent);
		session.setAttribute("successMessage", "Successfully Updated");
		return "redirect:/students";
	
	}
	
	
	
	//handler method to delete student
	@GetMapping("/students/{id}")
	public String deleteStudent(@PathVariable Long id, HttpSession session) {
		studentService.deleteStudentById(id);
		session.setAttribute("successMessage", "Successfully Deleted");
		return "redirect:/students";
	}

	// Show the Enrol Page
	@GetMapping("/students/enrol/{id}")
	public String showEnrolPage(@PathVariable Long id, Model model) {
		Student student = studentService.getStudentById(id);
		List<Course> enrolledCourses = enrollmentService.getEnrolledCoursesByStudent(id);
		List<Course> availableCourses = enrollmentService.getAvailableCourses(student.getId());

		model.addAttribute("student", student);
		model.addAttribute("enrolledCourses", enrolledCourses);
		model.addAttribute("availableCourses", availableCourses);

		return "enroll";
	}

	// Handle Course Enrollment
	@PostMapping("/students/enrol/{id}")
	public String enrolCourses(@PathVariable Long id, @RequestParam List<Long> courseIds, Model model) {
		try {
			// Enroll the student in the selected courses
			enrollmentService.enrollStudentToCourses(id, courseIds);

			// Redirect back to the enrol page after successful enrollment
			return "redirect:/students/enrol/" + id;
		} catch (RuntimeException e) {
			// Handle validation errors (e.g., time clashes, duplicate occurrences)
			model.addAttribute("error", e.getMessage());
			return showEnrolPage(id, model);
		}
	}
}
