package com.gupb.manager.controllers;

import com.gupb.manager.model.Team;
import com.gupb.manager.repositories.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(path = "/db")
public class DatabaseController {

    @Autowired
    private TeamRepository teamRepository;

    @GetMapping(path="/all")
    public @ResponseBody
    Iterable<Team> getAllUsers() {
        return teamRepository.findAll();
    }
}
