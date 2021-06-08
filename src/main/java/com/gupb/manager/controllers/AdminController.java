package com.gupb.manager.controllers;

import com.gupb.manager.model.*;
import com.gupb.manager.repositories.AdminRepository;
import com.gupb.manager.repositories.StudentRepository;
import com.gupb.manager.repositories.TournamentRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/")
public class AdminController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @GetMapping("/admins")
    public Iterable<Admin> getAdmins() {
        return adminRepository.findAll();
    }

    @GetMapping("/admins/id")
    public ResponseEntity<Admin> getAdmin(@RequestParam Integer id) {
        return adminRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFound("Admin not found"));
    }

    @GetMapping("/admins/tournaments")
    public @ResponseBody
    ResponseEntity<List<Tournament>>
    getTournaments(@RequestParam Integer id) {
        return ResponseEntity.ok(tournamentRepository.findByCreatorId(id));
    }

    @PostMapping("/admins")
    @Transactional
    public Admin createAdmin(@RequestBody String adminString) throws ResourceConflict {
        JSONObject adminData = new JSONObject(adminString);

        String emailAddress = adminData.getString("emailAddress");

        Optional<Admin> adminOptional = adminRepository.findByEmailAddress(emailAddress);
        Optional<Student> studentOptional = studentRepository.findByEmailAddress(emailAddress);
        if(studentOptional.isPresent() || adminOptional.isPresent()) {
            throw new ResourceConflict("Person with this email address already exists");
        }

        Admin admin = new Admin(adminData.getString("firstName"), adminData.getString("lastName"),
                adminData.getString("emailAddress"), adminData.getString("password"));


        admin = adminRepository.save(admin);
        return admin;
    }

    @PutMapping("/admins/edit")
    @Transactional
    public Admin editAdmin(@RequestBody String adminString) throws ResourceConflict {
        JSONObject adminData = new JSONObject(adminString);

        Optional<Admin> adminOptional = adminRepository.findById(adminData.getInt("id"));
        Admin admin = adminOptional.orElseThrow(() -> new ResourceNotFound("Admin not found"));
        String emailAddress = adminData.getString("emailAddress");

        if(!emailAddress.equals(admin.getEmailAddress())) {
            Optional<Admin> anotherAdminOptional = adminRepository.findByEmailAddress(emailAddress);
            Optional<Student> studentOptional = studentRepository.findByEmailAddress(emailAddress);
            if(anotherAdminOptional.isPresent() || studentOptional.isPresent()) {
                throw new ResourceConflict("Person with this email address already exists");
            }
            admin.setEmailAddress(emailAddress);
        }

        admin.setPassword(adminData.getString("password"));

        admin = adminRepository.save(admin);
        return admin;
    }

    @GetMapping("/admins/email")
    public ResponseEntity<Boolean>
    emailAlreadyExists(@RequestParam String emailAddress) {
        return ResponseEntity.ok(adminRepository.findByEmailAddress(emailAddress).isPresent()
                || studentRepository.findByEmailAddress(emailAddress).isPresent());
    }
}
