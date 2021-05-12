package com.gupb.manager.controllers;

import com.gupb.manager.model.ResourceNotFound;
import com.gupb.manager.model.Requirement;
import com.gupb.manager.repositories.RequirementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/")
public class RequirementController {
    @Autowired
    private RequirementRepository requirementRepository;

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