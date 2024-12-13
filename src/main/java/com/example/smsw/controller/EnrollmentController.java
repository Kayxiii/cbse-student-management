package com.example.smsw.controller;

import com.example.smsw.entity.Course;
import com.example.smsw.entity.Enrollment;
import com.example.smsw.entity.Student;
import com.example.smsw.service.CourseService;
import com.example.smsw.service.EnrollmentService;
import com.example.smsw.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/enrollment")
public class EnrollmentController {

    private final StudentService studentService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;

    @Autowired
    public EnrollmentController(StudentService studentService, CourseService courseService, EnrollmentService enrollmentService) {
        this.studentService = studentService;
        this.courseService = courseService;
        this.enrollmentService = enrollmentService;
    }

    @GetMapping("/{studentId}")
    public String showEnrollmentPage(@PathVariable Long studentId, Model model) {
        Student student = studentService.getStudentById(studentId);
        List<Course> enrolledCourses = enrollmentService.getEnrolledCoursesByStudent(studentId);
        List<Course> availableCourses = courseService.getAvailableCourses();

        model.addAttribute("student", student);
        model.addAttribute("enrolledCourses", enrolledCourses);
        model.addAttribute("availableCourses", availableCourses);

        return "enroll";
    }

    @PostMapping("/{studentId}/enroll")
    public String enrollCourses(@PathVariable Long studentId, @RequestParam List<Long> courseIds, Model model) {
        try {
            enrollmentService.enrollStudentToCourses(studentId, courseIds);
            return "redirect:/enrollment/" + studentId;
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return showEnrollmentPage(studentId, model);
        }
    }
}