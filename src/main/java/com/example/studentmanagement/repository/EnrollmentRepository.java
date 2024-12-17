package com.example.studentmanagement.repository;

import com.example.studentmanagement.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId")
    List<Enrollment> findByStudentId(@Param("studentId") Long studentId);
    @Query("SELECT e FROM Enrollment e WHERE e.course.id = :courseId")
    List<Enrollment> findByCourseId(@Param("courseId") Long courseId);

}