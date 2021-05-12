package com.gupb.manager.controllers;

import com.gupb.manager.model.ResourceNotFound;
import com.gupb.manager.model.AccessMode;
import com.gupb.manager.model.Round;
import com.gupb.manager.model.Tournament;
import com.gupb.manager.repositories.RoundRepository;
import com.gupb.manager.repositories.TournamentRepository;
import com.gupb.manager.scheduler.SchedulerConfig;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/")
public class RoundController {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private RoundRepository roundRepository;

    @Autowired
    private SchedulerConfig schedulerConfig;

    @GetMapping("/rounds")
    public Iterable<Round> getRounds() {
        return roundRepository.findAll();
    }

    @GetMapping("/rounds/{id}")
    public @ResponseBody
    ResponseEntity<Round>
    getTeamById(@PathVariable Integer id) {
        return roundRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFound("Round not found"));
    }

    @PostMapping("/rounds")
    public Round createRound(@RequestBody String roundData) {
        JSONObject roundJSON = new JSONObject(roundData);
        Optional<Tournament> tournamentOptional = tournamentRepository.findById(roundJSON.getInt("teamId"));
        int number = roundJSON.getInt("number");
        int numberOfRounds = roundJSON.getInt("numberOfRuns");
        LocalDateTime date = LocalDateTime.parse((String) roundJSON.get("date"));
        Round round = tournamentOptional
                .map(tournament -> new Round(tournament, number, numberOfRounds, date))
                .orElseThrow(() -> new ResourceNotFound("Tournament not found"));

        roundRepository.save(round);
        //schedulerConfig.appointMailsSending(round); //todo: this.repository is null
        //schedulerConfig.appointRound(round.getDate());
        return round;
    }
}