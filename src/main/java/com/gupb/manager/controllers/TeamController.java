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

@CrossOrigin
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

        Team team = tournamentRepository.findById(teamData.getInt("tournament_id"))
                .map(tournament -> new Team(tournament, name, teamData.getString("branchName"), playerName,
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

    @PostMapping("/teams/edit")
    @Transactional
    public Team editTeam(@RequestBody String teamString) throws ResourceConflict {
        JSONObject teamData = new JSONObject(teamString);

        Optional<Team> teamOptional = teamRepository.findById(teamData.getInt("id"));
        Team team = teamOptional.orElseThrow(() -> new ResourceNotFound("Team not found"));
        String name = teamData.getString("name");
        String playerName = teamData.getString("playerName");

        Optional<Team> anotherTeamOptional;

        if(!name.equals(team.getName())) {
            anotherTeamOptional = teamRepository.findByName(name);
            if(anotherTeamOptional.isPresent()) {
                throw new ResourceConflict("Team with this name already exists");
            }
            team.setName(name);
        }

        if(!playerName.equals(team.getPlayerName())) {
            anotherTeamOptional = teamRepository.findByPlayerName(playerName);
            if (anotherTeamOptional.isPresent()) {
                throw new ResourceConflict("Team with this player name already exists");
            }
            team.setPlayerName(playerName);
        }

        team.setMainClassName(teamData.getString("mainClassName"));
        team.setInvitationCode(teamData.getString("invitationCode"));

        boolean changedRepo = false;
        String githubLink = teamData.getString("githubLink");
        String branchName = teamData.getString("branchName");

        if(!githubLink.equals(team.getGithubLink()) || !branchName.equals(team.getBranchName())) {
            changedRepo = true;
            team.setGithubLink(githubLink);
            team.setBranchName(branchName);
        }

        team = teamRepository.save(team);

        if(changedRepo) {
            team.setLastUpdated(LocalDateTime.now());
            botTester.testTeamBot(team);
        }
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
    @Transactional
    public void updateBot(@RequestParam(name = "teamId") int teamId) {
        var teamOptional = teamRepository.findById(teamId);
        teamOptional.ifPresent(team -> {
            team.setLastUpdated(LocalDateTime.now());
            botTester.testTeamBot(team);
        });
    }
}
