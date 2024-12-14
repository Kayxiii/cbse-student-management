package com.example.studentmanagement.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_code", nullable = false)
    private String courseCode;

    @Column(name = "course_name", nullable = false)
    private String courseName;

    @Column(name = "credit_hour", nullable = false)
    private int creditHour;

    @Column(name = "occurrence", nullable = false)
    private int occurrence;

    @Column(name = "academic_session", nullable = false)
    private String academicSession;

    @Column(name = "semester", nullable = false)
    private int semester;

    @Column(name = "day", nullable = false)
    private String day;

    @Column(name = "time_start", nullable = false)
    private String timeStart;

    @Column(name = "time_end", nullable = false)
    private String timeEnd;

    @Column(name = "lecturer_name", nullable = false)
    private String lecturerName;

    @Column(name = "target", nullable = false)
    private int target;

    @Column(name = "actual", nullable = false)
    private int actual = 0;

    @Column(name = "activity", nullable = false)
    private String activity;

    @Column(name = "registration_status", nullable = false)
    private String registrationStatus = "OPEN";

    @ManyToMany(mappedBy = "courses")
    private List<Student> students;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return id != null && id.equals(course.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getCreditHour() {
        return creditHour;
    }

    public void setCreditHour(int creditHour) {
        this.creditHour = creditHour;
    }

    public int getOccurrence() {
        return occurrence;
    }

    public void setOccurrence(int occurrence) {
        this.occurrence = occurrence;
    }

    public String getAcademicSession() {
        return academicSession;
    }

    public void setAcademicSession(String academicSession) {
        this.academicSession = academicSession;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getLecturerName() {
        return lecturerName;
    }

    public void setLecturerName(String lecturerName) {
        this.lecturerName = lecturerName;
    }

    public int getTarget() {
        return target;
    }

    public int getActual() {
        return actual;
    }


    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(String registrationStatus) {
        this.registrationStatus = registrationStatus;
    }

    public void setActual(int actual) {
        this.actual = actual;
        updateRegistrationStatus(); // Update status whenever 'actual' changes
    }

    public void setTarget(int target) {
        this.target = target;
        updateRegistrationStatus(); // Update status whenever 'target' changes
    }

    private void updateRegistrationStatus() {
        if (this.actual >= this.target) {
            this.registrationStatus = "CLOSED";
        } else {
            this.registrationStatus = "OPEN";
        }
    }

}