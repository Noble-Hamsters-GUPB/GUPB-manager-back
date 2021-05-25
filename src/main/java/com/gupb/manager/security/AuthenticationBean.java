package com.gupb.manager.security;

import org.springframework.context.annotation.Bean;

public class AuthenticationBean {

    private String message;

    public AuthenticationBean(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
