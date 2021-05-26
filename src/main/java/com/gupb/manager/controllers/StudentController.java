package com.gupb.manager.controllers;

import com.gupb.manager.model.ResourceConflict;
import com.gupb.manager.model.ResourceNotFound;
import com.gupb.manager.model.Student;
import com.gupb.manager.model.Team;
import com.gupb.manager.repositories.StudentRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Optional;

@CrossOrigin
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
    public Student createStudent(@RequestBody String studentString) throws ResourceConflict {
        JSONObject studentData = new JSONObject(studentString);

        String indexNumber = studentData.getString("indexNumber");
        String emailAddress = studentData.getString("emailAddress");

        Optional<Student> studentOptional = studentRepository.findByEmailAddress(emailAddress);
        if(studentOptional.isPresent()) {
            throw new ResourceConflict("Student with this email address already exists");
        }
        studentOptional = studentRepository.findByIndexNumber(indexNumber);
        if(studentOptional.isPresent()) {
            throw new ResourceConflict("Student with this index number already exists");
        }

        Student student = new Student(new HashSet<>(), studentData.getString("firstName"),
                studentData.getString("lastName"), indexNumber, emailAddress, studentData.getString("password"));

        student = studentRepository.save(student);
        return student;
    }
}
