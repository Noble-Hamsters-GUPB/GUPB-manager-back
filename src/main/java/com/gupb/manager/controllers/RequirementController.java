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

        Tournament tournament = requirement.getRequestedBy().getTournament();

        template.convertAndSend("/topic/requirements/" + tournament.getId(), requirementRepository.findByTournament(tournament));
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

        Tournament tournament;

        Requirement requirement;
        if(teamOptional.isPresent()) {
            tournament = teamOptional.get().getTournament();
            requirement = new Requirement(packageInfo, status, teamOptional.get());
        }
        else {
            throw new ResourceNotFound("Team not found");
        }
        requirement = requirementRepository.save(requirement);
        template.convertAndSend("/topic/requirements/" + tournament.getId(), requirementRepository.findByTournament(tournament));
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

        Tournament tournament = requirement.getRequestedBy().getTournament();

        template.convertAndSend("/topic/requirements/" + tournament.getId(), requirementRepository.findByTournament(tournament));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/requirements/package")
    public ResponseEntity<Boolean>
    packageAlreadyExistsOrIsNotValid(@RequestParam String packageInfo, @RequestParam Integer tournamentId) {
        Optional<Requirement> requirementOptional = requirementRepository.findByPackageInfo(packageInfo);
        boolean exists = requirementOptional.isPresent();
        if(exists) {
            exists = requirementOptional.get().getRequestedBy().getTournament().getId() == tournamentId;
        }
        return ResponseEntity.ok(exists || !pythonPackageManager.pythonPackageExists(packageInfo));
    }

    @SendTo("/topic/requirements")
    public List<Requirement> broadcastMessage(@Payload List<Requirement> requirements) {
        return requirements;
    }
}
