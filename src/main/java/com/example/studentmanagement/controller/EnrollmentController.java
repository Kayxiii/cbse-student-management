package com.example.studentmanagement.controller;

import com.example.studentmanagement.entity.Course;
import com.example.studentmanagement.entity.Student;
import com.example.studentmanagement.service.CourseService;
import com.example.studentmanagement.service.EnrollmentService;
import com.example.studentmanagement.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/enrollment")
public class EnrollmentController {

    private final StudentService studentService;
    private final EnrollmentService enrollmentService;
    @Autowired
    public EnrollmentController(StudentService studentService, EnrollmentService enrollmentService) {
        this.studentService = studentService;
        this.enrollmentService = enrollmentService;
    }

    @GetMapping("/{studentId}")
    public String showEnrollmentPage(@PathVariable Long studentId, Model model) {

        Student student = studentService.getStudentById(studentId);

        // Fetch enrolled and available courses
        List<Course> enrolledCourses = enrollmentService.getEnrolledCoursesByStudent(studentId);
        List<Course> availableCourses = enrollmentService.getAvailableCourses(student.getId());

        model.addAttribute("student", student);
        model.addAttribute("enrolledCourses", enrolledCourses);
        model.addAttribute("availableCourses", availableCourses);

        return "enroll";
    }

    @PostMapping("/{studentId}/enroll")
    public String enrollCourses(@PathVariable Long studentId, @RequestParam(value = "courseIds", required = false) List<Long> courseIds, Model model) {
        try {
            if (courseIds == null || courseIds.isEmpty()) {
                throw new RuntimeException("No courses selected for enrollment.");
            }

            // Use the service to fetch the student and enroll in courses
            enrollmentService.enrollStudentToCourses(studentId, courseIds);

            // Redirect to reload the page with updated data
            return "redirect:/enrollment/" + studentId;
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return showEnrollmentPage(studentId, model);
        }
    }

}