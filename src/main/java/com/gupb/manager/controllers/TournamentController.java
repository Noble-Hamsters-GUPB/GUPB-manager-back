package com.gupb.manager.controllers;

import com.gupb.manager.model.Tournament;
import com.gupb.manager.repositories.TournamentRepository;
import com.gupb.manager.scheduler.SchedulerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/")
public class TournamentController {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private SchedulerConfig schedulerConfig;

    @GetMapping("/tournaments")
    public Iterable<Tournament> getTournaments() {
        return tournamentRepository.findAll();
    }
    
    @PostMapping("/tournaments")
    public Tournament createTournament(@RequestBody Tournament tournament) {
        Tournament newTournament = tournamentRepository.save(tournament);
        schedulerConfig.appointTournament(newTournament.getDate());
        return newTournament;
    }
}
