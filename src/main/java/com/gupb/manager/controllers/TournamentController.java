package com.gupb.manager.controllers;

import com.gupb.manager.model.*;
import com.gupb.manager.repositories.AdminRepository;
import com.gupb.manager.repositories.TournamentRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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
    getTournamentById(@PathVariable Integer id) {
        return tournamentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFound("Tournament not found"));
    }
    
    @PostMapping("/tournaments")
    @Transactional
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
                "accessMode"), creator, tournamentData.getString("githubLink"),
                tournamentData.getString("branchName"), tournamentData.getString("invitationCode"),
                tournamentData.getString("moduleName")));
    }

    @PutMapping("/tournaments/edit")
    @Transactional
    public Tournament editTournament(@RequestBody String tournamentString) throws ResourceConflict {
        JSONObject tournamentData = new JSONObject(tournamentString);

        Optional<Tournament> tournamentOptional = tournamentRepository.findById(tournamentData.getInt("id"));
        Tournament tournament = tournamentOptional.orElseThrow(() -> new ResourceNotFound("Tournament not found"));

        String name = tournamentData.getString("name");

        if(!name.equals(tournament.getName())) {
            Optional<Tournament> anotherTournamentOptional = tournamentRepository.findByName(name);
            if(anotherTournamentOptional.isPresent()) {
                throw new ResourceConflict("Tournament with this name already exists");
            }
            tournament.setName(name);
        }

        tournament.setGithubLink(tournamentData.getString("githubLink"));
        tournament.setBranchName(tournamentData.getString("branchName"));
        tournament.setInvitationCode(tournamentData.getString("invitationCode"));
        tournament.setModuleName(tournamentData.getString("moduleName"));

        return tournamentRepository.save(tournament);
    }
}
