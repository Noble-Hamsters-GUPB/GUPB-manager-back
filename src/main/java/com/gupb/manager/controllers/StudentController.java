package com.gupb.manager.controllers;

import com.gupb.manager.model.ResourceNotFound;
import com.gupb.manager.model.Student;
import com.gupb.manager.model.Team;
import com.gupb.manager.repositories.StudentRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @GetMapping("/students")
    public Iterable<Student> getStudents() {
        return studentRepository.findAll();
    }

    @PostMapping("/students")
    public Student createStudent(@RequestBody String studentString) {
        JSONObject studentData = new JSONObject(studentString);
        Student student = new Student(new HashSet<>(), studentData.getString("firstName"),
                        studentData.getString("lastName"), studentData.getString("indexNumber"),
                        studentData.getString("emailAddress"), studentData.getString("password"));

        studentRepository.save(student);
        return student;
    }
}
