package com.example.studentmanagement.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.studentmanagement.entity.Student;
import com.example.studentmanagement.service.StudentService;
import com.example.studentmanagement.service.CourseService;
import com.example.studentmanagement.service.EnrollmentService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/students")
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

	//handler method to handle list of students and return mode and view
	@GetMapping
	public String listStudents(Model model) {
		model.addAttribute("students", studentService.getAllStudents());
		return "students";
	}

	//get the add student page
	@GetMapping("/new")
	public String createStudentForm(Model model) {
		
		//create student object from student form data
		Student student = new Student();
		model.addAttribute("student", student);
		return "create_student";
		
	}

	//saving student
	@PostMapping
	public String saveStudent(@ModelAttribute("student") Student student, HttpSession session) {
		studentService.saveStudent(student);
		session.setAttribute("successMessage", "Successfully Added");
		return "redirect:/students";
	}

	//get the update or edit page
	@GetMapping("/edit/{id}")
	public String editStudentForm(@PathVariable Long id, Model model) {
		model.addAttribute("student", studentService.getStudentById(id));
		return "edit_student";
	}

	//update data into existing table
	@PostMapping("/{id}")
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
	@GetMapping("/{id}")
	public String deleteStudent(@PathVariable Long id, HttpSession session) {
		studentService.deleteStudentById(id);
		session.setAttribute("successMessage", "Successfully Deleted");
		return "redirect:/students";
	}

}
