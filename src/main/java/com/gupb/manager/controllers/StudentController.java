package com.gupb.manager.controllers;

import com.gupb.manager.model.*;
import com.gupb.manager.repositories.AdminRepository;
import com.gupb.manager.repositories.StudentRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AdminRepository adminRepository;

    @GetMapping("/students")
    public Iterable<Student> getStudents() {
        return studentRepository.findAll();
    }

    @PostMapping("/students")
    @Transactional
    public Student createStudent(@RequestBody String studentString) throws ResourceConflict {
        JSONObject studentData = new JSONObject(studentString);

        String indexNumber = studentData.getString("indexNumber");
        String emailAddress = studentData.getString("emailAddress");

        Optional<Student> studentOptional = studentRepository.findByEmailAddress(emailAddress);
        Optional<Admin> adminOptional = adminRepository.findByEmailAddress(emailAddress);
        if(studentOptional.isPresent() || adminOptional.isPresent()) {
            throw new ResourceConflict("Person with this email address already exists");
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

    @PutMapping("/students/edit")
    @Transactional
    public Student editStudent(@RequestBody String studentString) throws ResourceConflict {
        JSONObject studentData = new JSONObject(studentString);

        Optional<Student> studentOptional = studentRepository.findById(studentData.getInt("id"));
        Student student = studentOptional.orElseThrow(() -> new ResourceNotFound("Student not found"));
        String emailAddress = studentData.getString("emailAddress");

        if(!emailAddress.equals(student.getEmailAddress())) {
            Optional<Student> anotherStudentOptional = studentRepository.findByEmailAddress(emailAddress);
            Optional<Admin> adminOptional = adminRepository.findByEmailAddress(emailAddress);
            if(anotherStudentOptional.isPresent() || adminOptional.isPresent()) {
                throw new ResourceConflict("Person with this email address already exists");
            }
            student.setEmailAddress(emailAddress);
        }

        student.setPassword(studentData.getString("password"));

        student = studentRepository.save(student);
        return student;
    }
}
