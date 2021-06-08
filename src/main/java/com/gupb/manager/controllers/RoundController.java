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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@CrossOrigin
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

    @GetMapping("/rounds/id")
    public @ResponseBody
    ResponseEntity<Round>
    getRoundById(@RequestParam Integer id) {
        return roundRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFound("Round not found"));
    }

    @GetMapping("/rounds/tournament")
    public ResponseEntity<List<Round>>
    getRoundsByTournament(@RequestParam Integer tournamentId) {
        return ResponseEntity.ok(roundRepository.findByTournamentId(tournamentId));
    }

    @PostMapping("/rounds")
    @Transactional
    public Round createRound(@RequestBody String roundData) {
        JSONObject roundJSON = new JSONObject(roundData);
        Integer tournamentId = roundJSON.getInt("tournamentId");
        Optional<Tournament> tournamentOptional = tournamentRepository.findById(tournamentId);
        int numberOfRounds = roundJSON.getInt("numberOfRuns");
        LocalDateTime date = LocalDateTime.parse((String) roundJSON.get("date"));
        Round round = tournamentOptional
                .map(tournament -> new Round(tournament,
                        roundRepository.findByTournamentId(tournamentId).size() + 1, numberOfRounds, date))
                .orElseThrow(() -> new ResourceNotFound("Tournament not found"));

        round = roundRepository.save(round);
        schedulerConfig.appointMailsSending(round);
        schedulerConfig.appointRound(round);
        return round;
    }

    @PutMapping("/rounds/edit")
    @Transactional
    public Round editRound(@RequestBody String roundString) {
        JSONObject roundData = new JSONObject(roundString);

        Optional<Round> roundOptional = roundRepository.findById(roundData.getInt("id"));
        Round round = roundOptional.orElseThrow(() -> new ResourceNotFound("Round not found"));

        round.setNumberOfRuns(roundData.getInt("numberOfRuns"));

        round = roundRepository.save(round);
        return round;
    }
}
