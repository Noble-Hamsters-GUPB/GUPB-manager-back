package com.gupb.manager.controllers;

import com.gupb.manager.model.Admin;
import com.gupb.manager.model.ResourceConflict;
import com.gupb.manager.model.Student;
import com.gupb.manager.repositories.AdminRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/")
public class AdminController {

    @Autowired
    private AdminRepository adminRepository;

    @GetMapping("/admins")
    public Iterable<Admin> getAdmins() {
        return adminRepository.findAll();
    }

    @PostMapping("/admins")
    public Admin createAdmin(@RequestBody String adminString) throws ResourceConflict {
        JSONObject adminData = new JSONObject(adminString);

        String emailAddress = adminData.getString("emailAddress");

        Optional<Admin> adminOptional = adminRepository.findByEmailAddress(emailAddress);
        if(adminOptional.isPresent()) {
            throw new ResourceConflict("Admin with this email address already exists");
        }

        Admin admin = new Admin(adminData.getString("firstName"), adminData.getString("lastName"),
                adminData.getString("emailAddress"), adminData.getString("password"));


        admin = adminRepository.save(admin);
        return admin;
    }
}
