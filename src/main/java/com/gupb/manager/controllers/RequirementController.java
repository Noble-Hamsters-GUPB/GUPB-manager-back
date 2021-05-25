package com.gupb.manager.controllers;

import com.gupb.manager.mails.MailService;
import com.gupb.manager.model.*;
import com.gupb.manager.repositories.RequirementRepository;
import com.gupb.manager.repositories.TeamRepository;
import com.gupb.manager.repositories.TournamentRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
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

    @Autowired
    private MailService mailService;
  
    @Autowired
    private SimpMessagingTemplate template;

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

        template.convertAndSend("/topic/requirements", requirementRepository.findAll());
        mailService.sendEmailsToStudentsAfterRequestStatusChange(requirement);
        return ResponseEntity.ok(updatedRequirement);
    }

    @PostMapping("/requirements")
    public Requirement createRequirement(@RequestBody String requirementData) throws ResourceConflict {
        JSONObject requirementJSON = new JSONObject(requirementData);
        Optional<Tournament> tournamentOptional = tournamentRepository.findById(requirementJSON.getInt("tournamentId"));
        Optional<Team> teamOptional = teamRepository.findById(requirementJSON.getInt("teamId"));
        String packageInfo = requirementJSON.getString("packageInfo");

        Optional<Requirement> requirementOptional = requirementRepository.findByPackageInfo(packageInfo);

        if(requirementOptional.isPresent()) {
            throw new ResourceConflict("This requirement already exists");
        }

        RequirementStatus status = requirementJSON.optEnum(RequirementStatus.class, "status");

            Requirement requirement = tournamentOptional
                    .map(tournament -> teamOptional
                            .map(team -> new Requirement(packageInfo, status, tournament, team))
                            .orElseThrow(() -> new ResourceNotFound("Team not found")))
                    .orElseThrow(() -> new ResourceNotFound("Tournament not found"));

        requirement = requirementRepository.save(requirement);
        template.convertAndSend("/topic/requirements", requirementRepository.findAll());
        mailService.sendEmailToCreatorAfterLibraryRequest(requirement);
        return requirement;
    }

    @DeleteMapping("/requirements/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteRequirement(@PathVariable Integer id) {
        Requirement requirement = requirementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Requirement not exists with id: " + id));
        
        requirementRepository.delete(requirement);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        template.convertAndSend("/topic/requirements", requirementRepository.findAll());
        return ResponseEntity.ok(response);
    }

    @SendTo("/topic/requirements")
    public List<Requirement> broadcastMessage(@Payload List<Requirement> requirements) {
        return requirements;
    }
}
