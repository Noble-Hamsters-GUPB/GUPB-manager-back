package com.gupb.manager.controllers;

import com.gupb.manager.bots.BotTester;
import com.gupb.manager.model.Student;
import com.gupb.manager.model.Team;
import com.gupb.manager.repositories.StudentRepository;
import com.gupb.manager.repositories.TeamRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/")
public class TeamController {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private BotTester botTester;

    @GetMapping("/teams")
    public Iterable<Team> getTeams() {
        return teamRepository.findAll();
    }

    @PostMapping("/teams")
    @Transactional
    public Team createTeam(@RequestBody String teamString) {
        JSONObject teamData = new JSONObject(teamString);
        Team team = new Team(teamData.getString("name"), teamData.getString("githubLink"));
        JSONArray members =  teamData.getJSONArray("members");
        for(int i = 0; i < members.length(); i++){
            JSONObject member = members.getJSONObject(i);
            Student student = new Student(team, member.getString("firstName"), member.getString("lastName"),
                    member.getString("indexNumber"), member.getString("emailAddress"));
            studentRepository.save(student);
        }

        botTester.testTeamBot(team);
        team.setLastUpdated(LocalDateTime.now());
        return teamRepository.save(team);
    }

    @PostMapping("/update-bot")
    public void updateBot(@RequestBody int teamId) {
        var teamOptional = teamRepository.findById(teamId);
        teamOptional.ifPresent(team -> {
            botTester.testTeamBot(team);
            team.setLastUpdated(LocalDateTime.now());
        });
    }
}
