package com.gupb.manager.controllers;

import com.gupb.manager.model.*;
import com.gupb.manager.repositories.AdminRepository;
import com.gupb.manager.repositories.StudentRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    @GetMapping("/students/id")
    public ResponseEntity<Student> getStudent(@RequestParam Integer id) {
        return studentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFound("Student not found"));
    }

    @GetMapping("/students/tournaments")
    public @ResponseBody
    ResponseEntity<List<Tournament>>
    getTournaments(@RequestParam Integer id) {
        Optional<Student> studentOptional = studentRepository.findById(id);
        Student student = studentOptional.orElseThrow(() -> new ResourceNotFound("Student not found"));
        List<Tournament> tournaments = student.getTeams()
                .stream()
                .map(Team::getTournament)
                .collect(Collectors.toList());
        return ResponseEntity.ok(tournaments);
    }

    @GetMapping("/students/not_in_tournament")
    public @ResponseBody
    ResponseEntity<List<Student>>
    getStudentsNotInTournament(@RequestParam Integer id) {
        List<Student> students = studentRepository.findAll()
                .stream()
                .filter(student -> student.getTeams()
                        .stream()
                        .noneMatch(team -> team.getTournament().getId() == id))
                .collect(Collectors.toList());
        return ResponseEntity.ok(students);
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

    @GetMapping("/students/email")
    public ResponseEntity<Boolean>
    emailAlreadyExists(@RequestParam String emailAddress) {
        return ResponseEntity.ok(adminRepository.findByEmailAddress(emailAddress).isPresent()
                || studentRepository.findByEmailAddress(emailAddress).isPresent());
    }

    @GetMapping("/students/index")
    public ResponseEntity<Boolean>
    indexAlreadyExists(@RequestParam String indexNumber) {
        return ResponseEntity.ok(studentRepository.findByIndexNumber(indexNumber).isPresent());
    }
}
