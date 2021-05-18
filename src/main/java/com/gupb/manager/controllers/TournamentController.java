package com.gupb.manager.controllers;

import com.gupb.manager.model.AccessMode;
import com.gupb.manager.model.ResourceNotFound;
import com.gupb.manager.model.Team;
import com.gupb.manager.model.Tournament;
import com.gupb.manager.repositories.TournamentRepository;
import com.gupb.manager.scheduler.SchedulerConfig;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/")
public class TournamentController {

    @Autowired
    private TournamentRepository tournamentRepository;

    @GetMapping("/tournaments")
    public Iterable<Tournament> getTournaments() {
        return tournamentRepository.findAll();
    }

    @GetMapping("/tournaments/{id}")
    public @ResponseBody
    ResponseEntity<Tournament>
    getTeamById(@PathVariable Integer id) {
        return tournamentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFound("Tournament not found"));
    }
    
    @PostMapping("/tournaments")
    public Tournament createTournament(@RequestBody String tournamentData) {
        JSONObject tournamentJSON = new JSONObject(tournamentData);
        String name = tournamentJSON.getString("name");
        AccessMode accessMode = tournamentJSON.optEnum(AccessMode.class, "accessMode");
        return tournamentRepository.save(new Tournament(name, accessMode));
    }
}
