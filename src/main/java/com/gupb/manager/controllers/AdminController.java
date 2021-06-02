package com.gupb.manager.controllers;

import com.gupb.manager.model.Admin;
import com.gupb.manager.model.ResourceConflict;
import com.gupb.manager.model.ResourceNotFound;
import com.gupb.manager.model.Student;
import com.gupb.manager.repositories.AdminRepository;
import com.gupb.manager.repositories.StudentRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/")
public class AdminController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private StudentRepository studentRepository;

    @GetMapping("/admins")
    public Iterable<Admin> getAdmins() {
        return adminRepository.findAll();
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

    @PostMapping("/admins/edit")
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
}
