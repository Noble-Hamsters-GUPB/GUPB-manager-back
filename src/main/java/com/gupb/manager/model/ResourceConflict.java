package com.gupb.manager.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class ResourceConflict extends Exception {

    public ResourceConflict(String message) {
        super(message);
    }
}
