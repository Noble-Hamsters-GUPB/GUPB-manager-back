package com.gupb.manager.controllers;

import com.gupb.manager.model.Requirement;
import com.gupb.manager.model.Tournament;
import com.gupb.manager.providers.GameProvider;
import com.gupb.manager.repositories.RequirementRepository;
import com.gupb.manager.repositories.TeamRepository;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/")
public class RequirementController {

    @Autowired
    private RequirementRepository requirementRepository;

    public void setRequirements(Tournament tournament, String path) {
        List<Requirement> requirements = requirementRepository.findByTournament(tournament);
        StringBuilder stringBuilder = new StringBuilder();
        for (Requirement requirement : requirements) {
            stringBuilder.append(requirement.getPackageInfo()).append("\n");
        }
        try {
            Files.write(Paths.get(path), stringBuilder.toString().getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
        }
    }
}
