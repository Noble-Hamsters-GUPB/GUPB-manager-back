package com.gupb.manager.controllers;

import com.gupb.manager.model.*;
import com.gupb.manager.repositories.AdminRepository;
import com.gupb.manager.repositories.TournamentRepository;
import com.gupb.manager.scheduler.SchedulerConfig;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/")
public class TournamentController {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private AdminRepository adminRepository;

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
    public Tournament createTournament(@RequestBody String tournamentString) throws ResourceConflict {
        JSONObject tournamentData = new JSONObject(tournamentString);

        String name = tournamentData.getString("name");

        Optional<Tournament> tournamentOptional = tournamentRepository.findByName(name);

        if(tournamentOptional.isPresent()) {
            throw new ResourceConflict("Tournament with this name already exists");
        }

        Admin creator = adminRepository.findById(tournamentData.getInt("creator"))
                .orElseThrow(() -> new ResourceNotFound("Admin not found"));
        return tournamentRepository.save(new Tournament(name, tournamentData.optEnum(AccessMode.class,
                "accessMode"), creator, tournamentData.getString("invitationCode")));
    }
}
