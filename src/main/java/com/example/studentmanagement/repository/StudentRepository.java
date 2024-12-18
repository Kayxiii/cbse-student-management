package com.example.studentmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.studentmanagement.entity.Student;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long>{
}
