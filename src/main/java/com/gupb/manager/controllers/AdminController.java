package com.gupb.manager.controllers;

import com.gupb.manager.model.Admin;
import com.gupb.manager.repositories.AdminRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
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
    public Admin createAdmin(@RequestBody String adminString) {
        JSONObject adminData = new JSONObject(adminString);
        Admin admin = new Admin(adminData.getString("firstName"), adminData.getString("lastName"),
                adminData.getString("emailAddress"), adminData.getString("password"));

        adminRepository.save(admin);
        return admin;
    }
}
