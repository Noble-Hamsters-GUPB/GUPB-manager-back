package com.gupb.manager.controllers;

import com.gupb.manager.security.AuthenticationBean;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/")
public class AuthenticationController {

    @GetMapping(path = "/auth")
    public AuthenticationBean authenticate() {
        return new AuthenticationBean("You are authenticated");
    }
}
