package com.gupb.manager.controllers;

import com.gupb.manager.model.Team;
import com.gupb.manager.repositories.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/")
public class TeamController {

    @Autowired
    private TeamRepository teamRepository;

    @GetMapping("/groups")
    public Iterable<Team> getTournaments() {
        return teamRepository.findAll();
    }

    @PostMapping("/groups")
    @Transactional
    public Team createTeam(@RequestBody Team team) {
        return teamRepository.save(team);
    }
}
