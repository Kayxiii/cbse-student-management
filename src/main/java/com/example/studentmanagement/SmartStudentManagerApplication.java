package com.example.studentmanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.studentmanagement.repository.StudentRepository;

@SpringBootApplication
public class SmartStudentManagerApplication implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(SmartStudentManagerApplication.class, args);
	}

	
	@Autowired
	private StudentRepository studentRepository;
	@Override
	public void run(String... args) throws Exception {
				
	}

}
