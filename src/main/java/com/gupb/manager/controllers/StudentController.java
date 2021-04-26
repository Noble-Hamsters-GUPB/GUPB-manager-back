package com.gupb.manager.controllers;

import com.gupb.manager.model.Student;
import com.gupb.manager.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @GetMapping("/students")
    public Iterable<Student> getStudents() { return studentRepository.findAll(); }

    @PostMapping("/students")
    public Student createStudent(@RequestBody String studentData) {
        return null;
    }
}
