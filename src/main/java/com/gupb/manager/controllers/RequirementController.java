package com.gupb.manager.controllers;

import com.gupb.manager.mails.MailService;
import com.gupb.manager.model.*;
import com.gupb.manager.python.PythonPackageManager;
import com.gupb.manager.repositories.RequirementRepository;
import com.gupb.manager.repositories.TeamRepository;
import com.gupb.manager.repositories.TournamentRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
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

    @Autowired
    private PythonPackageManager pythonPackageManager;

    @GetMapping("/requirements")
    public Iterable<Requirement> getRequirements() { return requirementRepository.findAll(); }

    @GetMapping("/requirements/id")
    public ResponseEntity<Requirement> getRequirementById(@RequestParam Integer id) {
        Requirement requirement = requirementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Requirement not exists with id: " + id));

        return ResponseEntity.ok(requirement);
    }

    @GetMapping("/requirements/tournament")
    public ResponseEntity<List<Requirement>> getRequirementsByTournament(@RequestParam Integer id) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Tournament not found"));
        List<Requirement> requirements = requirementRepository.findByTournament(tournament);
        return ResponseEntity.ok(requirements);
    }

    @PutMapping("/requirements")
    public ResponseEntity<Requirement> updateRequirement(@RequestParam Integer id, @RequestBody Requirement requirementDetails) {
        Requirement requirement = requirementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Requirement not exists with id: " + id));

        requirement.setStatus(requirementDetails.getStatus());

        Requirement updatedRequirement = requirementRepository.save(requirement);

        template.convertAndSend("/topic/requirements", requirementRepository.findAll());
        mailService.sendEmailsToStudentsAfterRequestStatusChange(requirement);
        return ResponseEntity.ok(updatedRequirement);
    }

    @PostMapping("/requirements")
    @Transactional
    public Requirement createRequirement(@RequestBody String requirementData) {
        JSONObject requirementJSON = new JSONObject(requirementData);
        Optional<Team> teamOptional = teamRepository.findById(requirementJSON.getInt("teamId"));
        String packageInfo = requirementJSON.getString("packageInfo");

        if (!pythonPackageManager.pythonPackageExists(packageInfo)) {
            throw new ResourceNotFound("Could not find package: " + packageInfo);
        }

        RequirementStatus status = requirementJSON.optEnum(RequirementStatus.class, "status");

        Requirement requirement = teamOptional
                        .map(team -> new Requirement(packageInfo, status, team))
                        .orElseThrow(() -> new ResourceNotFound("Team not found"));

        requirement = requirementRepository.save(requirement);
        template.convertAndSend("/topic/requirements", requirementRepository.findAll());
        mailService.sendEmailToCreatorAfterLibraryRequest(requirement);
        return requirement;
    }

    @DeleteMapping("/requirements")
    public ResponseEntity<Map<String, Boolean>> deleteRequirement(@RequestParam Integer id) {
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
