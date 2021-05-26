package com.gupb.manager.controllers;

import com.gupb.manager.model.ResourceNotFound;
import com.gupb.manager.model.Round;
import com.gupb.manager.repositories.RoundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/")
public class LogsController {

    @Autowired
    private RoundRepository roundRepository;

    @GetMapping("/logs/{id}")
    public String getLogs(@PathVariable Integer id) {
        Round round = roundRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Round not found"));
        StringBuilder logs = new StringBuilder();
        try {
            File logDirectory = new File(round.getPathToLogs());
            File logFile = List.of(Objects.requireNonNull(logDirectory.listFiles()))
                    .stream()
                    .filter(file -> file.getName().contains(".log"))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFound("Log file not found"));
            Scanner logReader = new Scanner(logFile);
            while (logReader.hasNextLine()) {
                logs.append(logReader.nextLine()).append("\n");
            }
            logReader.close();
        } catch (FileNotFoundException e) {
            throw new ResourceNotFound("Round not found");
        }
        return logs.toString();
    }
}
