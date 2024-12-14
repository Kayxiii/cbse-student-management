package com.example.studentmanagement.controller;

import com.example.studentmanagement.entity.Course;
import com.example.studentmanagement.service.CourseService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/courses")
public class CourseController {
    private final CourseService courseService;
    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    //handler to remove popup success message as blank
    @PostMapping("/resetSuccessMessage")
    public ResponseEntity<Void> resetSuccessMessage(HttpSession session) {
        session.removeAttribute("successCourseMessage");
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public String listCourses(Model model) {
        model.addAttribute("courses", courseService.getAllCourses());
        return "course";
    }

    @GetMapping("/new")
    public String createCourseForm(Model model) {
        model.addAttribute("course", new Course());
        return "create_course";
    }

    @PostMapping
    public String saveCourse(@ModelAttribute("course") Course course, HttpSession session) {
        int occurrenceCount = course.getOccurrence(); // Number of occurrences
        String startTime = course.getTimeStart(); // Initial start time for occurrence 1
        String endTime = course.getTimeEnd(); // Initial end time for occurrence 1

        // Parse the start and end times for duration calculation
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime initialStartTime = LocalTime.parse(startTime, timeFormatter);
        LocalTime initialEndTime = LocalTime.parse(endTime, timeFormatter);
        Duration duration = Duration.between(initialStartTime, initialEndTime); // Duration of each occurrence

        for (int i = 1; i <= occurrenceCount; i++) {
            Course newCourse = new Course();

            // Copy data to each course instance
            newCourse.setCourseCode(course.getCourseCode());
            newCourse.setCourseName(course.getCourseName());
            newCourse.setCreditHour(course.getCreditHour());
            newCourse.setOccurrence(i);
            newCourse.setAcademicSession(course.getAcademicSession());
            newCourse.setSemester(course.getSemester());
            newCourse.setDay(course.getDay());
            newCourse.setLecturerName(course.getLecturerName());
            newCourse.setTarget(course.getTarget());
            newCourse.setActivity(course.getActivity());
            newCourse.setActual(0); // Initialize actual as 0
            newCourse.setRegistrationStatus("OPEN"); // Default status

            // Assign start and end times dynamically for each occurrence
            newCourse.setTimeStart(initialStartTime.format(timeFormatter));
            newCourse.setTimeEnd(initialEndTime.format(timeFormatter));

            // Update start and end times for the next occurrence
            initialStartTime = initialEndTime;
            initialEndTime = initialEndTime.plus(duration);

            // Save the course occurrence
            courseService.saveCourse(newCourse);
        }

        session.setAttribute("successCourseMessage", "Successfully Added " + occurrenceCount + " Occurrences");
        return "redirect:/courses";
    }

    @GetMapping("/edit/{id}")
    public String editCourseForm(@PathVariable Long id, Model model) {
        model.addAttribute("course", courseService.getCourseById(id));
        return "edit_course";
    }

    @PostMapping("/{id}")
    public String updateCourse(@PathVariable Long id, @ModelAttribute("course") Course course, HttpSession session) {
        // Get existing course from the database
        Course existingCourse = courseService.getCourseById(id);

        // Update fields
        existingCourse.setCourseCode(course.getCourseCode());
        existingCourse.setCourseName(course.getCourseName());
        existingCourse.setCreditHour(course.getCreditHour());
        existingCourse.setAcademicSession(course.getAcademicSession());
        existingCourse.setSemester(course.getSemester());
        existingCourse.setDay(course.getDay());
        existingCourse.setTimeStart(course.getTimeStart());
        existingCourse.setTimeEnd(course.getTimeEnd());
        existingCourse.setLecturerName(course.getLecturerName());
        existingCourse.setTarget(course.getTarget());
        existingCourse.setActivity(course.getActivity());

        // Save updated course
        courseService.updateCourse(existingCourse);

        session.setAttribute("successCourseMessage", "Successfully Updated");
        return "redirect:/courses";
    }

    @GetMapping("/{id}")
    public String deleteCourse(@PathVariable Long id, HttpSession session) {
        courseService.deleteCourseById(id);
        session.setAttribute("successCourseMessage", "Successfully Deleted");
        return "redirect:/courses";
    }
}