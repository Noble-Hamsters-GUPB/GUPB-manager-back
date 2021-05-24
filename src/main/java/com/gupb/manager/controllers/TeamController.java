package com.gupb.manager.controllers;

import com.gupb.manager.model.ResourceConflict;
import com.gupb.manager.model.ResourceNotFound;
import com.gupb.manager.bots.BotTester;
import com.gupb.manager.model.Student;
import com.gupb.manager.model.Team;
import com.gupb.manager.repositories.StudentRepository;
import com.gupb.manager.repositories.TeamRepository;
import com.gupb.manager.repositories.TournamentRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/")
public class TeamController {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private BotTester botTester;

    @GetMapping("/teams")
    public Iterable<Team> getTeams() {
        return teamRepository.findAll();
    }

    @GetMapping("/teams/{id}")
    public @ResponseBody ResponseEntity<Team>
    getTeamById(@PathVariable Integer id) {
        return teamRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFound("Team not found"));
    }

    @PostMapping("/teams")
    @Transactional
    public Team createTeam(@RequestBody String teamString) throws ResourceConflict {
        JSONObject teamData = new JSONObject(teamString);

        String name = teamData.getString("name");
        String playerName = teamData.getString("playerName");

        Optional<Team> teamOptional = teamRepository.findByName(name);
        if(teamOptional.isPresent()) {
            throw new ResourceConflict("Team with this name already exists");
        }
        teamOptional = teamRepository.findByPlayerName(playerName);
        if(teamOptional.isPresent()) {
            throw new ResourceConflict("Team with this player name already exists");
        }


        Team team = tournamentRepository.findById(teamData.getInt("tournment_id"))
                .map(tournament -> new Team(tournament, name, playerName,
                        teamData.getString("githubLink"), teamData.getString("className"),
                        teamData.getString("invitationCode")))
                .orElseThrow(() -> new ResourceNotFound("Tournament not found"));

        team = teamRepository.save(team);
        Set<Student> teamStudents = new HashSet<>();
        JSONArray members =  teamData.getJSONArray("members");
        for(int i = 0; i < members.length(); i++) {
            JSONObject member = members.getJSONObject(i);
            Student student = studentRepository.findById(member.getInt("id"))
                    .orElseThrow(() -> new ResourceNotFound("Student not found"));
            student.getTeams().add(team);
            teamStudents.add(student);
        }
        team.setStudents(teamStudents);
        team.setLastUpdated(LocalDateTime.now());
        botTester.testTeamBot(team);
        return team;
    }

    @PostMapping("/teams/{id}")
    @Transactional
    public Team joinTeam(@PathVariable Integer id, @RequestBody Integer studentId) {
        return teamRepository.findById(id)
                .map(team -> studentRepository.findById(studentId)
                        .map(student -> {
                            team.getStudents().add(student);
                            student.getTeams().add(team);
                            return team;
                        })
                        .orElseThrow(() -> new ResourceNotFound("Student not found")))
                .orElseThrow(() -> new ResourceNotFound("Team not found"));
    }

    @PostMapping("/update-bot")
    public void updateBot(@RequestBody int teamId) {
        var teamOptional = teamRepository.findById(teamId);
        teamOptional.ifPresent(team -> {
            team.setLastUpdated(LocalDateTime.now());
            botTester.testTeamBot(team);
        });
    }
}
