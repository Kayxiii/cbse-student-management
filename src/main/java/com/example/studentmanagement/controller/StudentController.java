package com.example.studentmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.studentmanagement.entity.Student;
import com.example.studentmanagement.service.StudentService;
import com.example.studentmanagement.service.CourseService;
import com.example.studentmanagement.service.EnrollmentService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/students")
public class StudentController {

	private final StudentService studentService;
	private final CourseService courseService;
	private final EnrollmentService enrollmentService;

    @Autowired
	public StudentController(StudentService studentService, CourseService courseService, EnrollmentService enrollmentService) {
		this.studentService = studentService;
		this.courseService = courseService;
		this.enrollmentService = enrollmentService;
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
	public String saveStudent(@ModelAttribute("student") Student student, Model model, RedirectAttributes redirectAttributes) {
		try {
			studentService.saveStudent(student);
			redirectAttributes.addFlashAttribute("successMessage", "Successfully Added");
			return "redirect:/students";
		} catch (IllegalArgumentException e) {
			// Add error message to model
			model.addAttribute("errorMessage", e.getMessage());
			model.addAttribute("student", student);
			return "create_student";
		}
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
			Model model, RedirectAttributes redirectAttributes) {
		
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

		try {
			//save update student object
			studentService.updateStudent(existingStudent);
			redirectAttributes.addFlashAttribute("successMessage", "Successfully Updated");
			return "redirect:/students";
		} catch (IllegalArgumentException e) {
			// Add error message to model
			model.addAttribute("errorMessage", e.getMessage());
			model.addAttribute("student", student);
			return "edit_student";
		}
	}

	//handler method to delete student
	@PostMapping("/delete/{id}")
	public String deleteStudent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		studentService.deleteStudentById(id);
		redirectAttributes.addFlashAttribute("successMessage", "Student deleted successfully!");
		return "redirect:/students";
	}
}
