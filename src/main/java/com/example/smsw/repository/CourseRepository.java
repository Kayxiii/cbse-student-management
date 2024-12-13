package com.example.smsw.repository;

import com.example.smsw.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query("SELECT c FROM Course c JOIN c.students s WHERE s.id = :studentId")
    List<Course> findByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT c FROM Course c WHERE c.registrationStatus = 'OPEN'")
    List<Course> findAvailableCourses();
}