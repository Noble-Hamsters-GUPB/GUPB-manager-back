package com.gupb.manager.controllers;

import com.gupb.manager.model.*;
import com.gupb.manager.repositories.RequirementRepository;
import com.gupb.manager.repositories.TeamRepository;
import com.gupb.manager.repositories.TournamentRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/")
public class RequirementController {
    @Autowired
    private RequirementRepository requirementRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TeamRepository teamRepository;

    @GetMapping("/requirements")
    public Iterable<Requirement> getRequirements() { return requirementRepository.findAll(); }

    @GetMapping("/requirements/{id}")
    public ResponseEntity<Requirement> getRequirementById(@PathVariable Integer id) {
        Requirement requirement = requirementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Requirement not exists with id: " + id));

        return ResponseEntity.ok(requirement);
    }

    @PutMapping("/requirements/{id}")
    public ResponseEntity<Requirement> updateRequirement(@PathVariable Integer id, @RequestBody Requirement requirementDetails) {
        Requirement requirement = requirementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Requirement not exists with id: " + id));

        requirement.setPackageInfo(requirementDetails.getPackageInfo());
        requirement.setStatus(requirementDetails.getStatus());

        Requirement updatedRequirement = requirementRepository.save(requirement);

        return ResponseEntity.ok(updatedRequirement);
    }

    @PostMapping("/requirements")
    public Requirement createRequirement(@RequestBody String requirementData) {
        JSONObject requirementJSON = new JSONObject(requirementData);
        Optional<Tournament> tournamentOptional = tournamentRepository.findById(requirementJSON.getInt("tournamentId"));
        Optional<Team> teamOptional = teamRepository.findById(requirementJSON.getInt("teamId"));
        String packageInfo = requirementJSON.getString("packageInfo");
        RequirementStatus status = requirementJSON.optEnum(RequirementStatus.class, "status");

            Requirement requirement = tournamentOptional
                    .map(tournament -> teamOptional
                            .map(team -> new Requirement(packageInfo, status, tournament, team))
                            .orElseThrow(() -> new ResourceNotFound("Team not found")))
                    .orElseThrow(() -> new ResourceNotFound("Tournament not found"));

        requirementRepository.save(requirement);
        return requirement;
    }

    @DeleteMapping("/requirements/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteRequirement(@PathVariable Integer id) {
        Requirement requirement = requirementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Requirement not exists with id: " + id));
        
        requirementRepository.delete(requirement);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return ResponseEntity.ok(response);
    }
}
