package com.gupb.manager.providers;

import com.gupb.manager.git.GitUtilities;
import com.gupb.manager.model.Round;
import com.gupb.manager.model.Team;
import com.gupb.manager.repositories.TeamRepository;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.List;

@Component
public class GameProvider {

    private static final String source = "https://github.com/Prpht/GUPB";

    private static final String configFile = "gupb" + File.separator + "default_config.py";

    private static final String controllerDirectoryName = "gupb" + File.separator + "controller" + File.separator;

    @Autowired
    private GitUtilities gitUtilities;

    @Autowired
    private TeamRepository teamRepository;

    public void provideRound(String path, String dirName, Round round) throws GitAPIException, IOException {

        List<Team> teamsInRound = teamRepository.findByTournament(round.getTournament());

        gitUtilities.cloneRepository(source, path + File.separator + dirName, "master");
        String destination = path + File.separator + dirName + File.separator + controllerDirectoryName;

        for (Team team : teamsInRound) {
            gitUtilities.cloneRepository(team.getGithubLink(), destination, "master");
        }

        StringBuilder stringBuilder = new StringBuilder();
        File file = new File(path + File.separator + dirName + File.separator + configFile);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
            if (line.contains("'runs_no': 300,")) {
                stringBuilder.append("\t'runs_no': ").append(round.getNumberOfRuns()).append(",").append("\n");
            }
            else if (line.contains("random.RandomController(\"Alice\"),")) {
                for (Team team : teamsInRound) {
                    stringBuilder.append("\t\t").append(team.getPackageName()).append(".")
                            .append(team.getControllerClassName()).append("(").append(")\n");
                }
                for (int i = 0; i < 3; i++) {
                    br.readLine();
                }
            }
            else {
                stringBuilder.append(line).append("\n");
                if (line.contains("from gupb.controller import random")) {
                    for (Team team : teamsInRound) {
                        stringBuilder.append("from gupb.controller import ").append(team.getPackageName()).append("\n");
                    }
                }
            }
        }
        br.close();
        System.out.println(stringBuilder.toString());
        BufferedWriter bw = new BufferedWriter(new FileWriter(file, false));
        bw.write(stringBuilder.toString());
        bw.close();
    }

    public void provideTestRoundWithBot(String path, String dirName, Team team) throws GitAPIException, IOException {

        gitUtilities.cloneRepository(source, path + File.separator + dirName, "master");
        String destination = path + File.separator + dirName + File.separator + controllerDirectoryName;
        gitUtilities.cloneRepository(team.getGithubLink(), destination, "master");

        StringBuilder stringBuilder = new StringBuilder();
        File file = new File(path + File.separator + dirName + File.separator + configFile);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
            if (line.contains("'runs_no': 300,")) {
                stringBuilder.append("\t'runs_no': 5,").append("\n");
            }
            else {
                stringBuilder.append(line).append("\n");
                if (line.contains("from gupb.controller import random")) {
                    stringBuilder.append("from gupb.controller import ").append(team.getPackageName()).append("\n");
                }
                else if (line.contains("random.RandomController(\"Darius\"),")) {
                    stringBuilder.append("\t\t").append(team.getPackageName()).append(".")
                            .append(team.getControllerClassName()).append("(").append(")\n");
                }
            }
        }
        br.close();
        System.out.println(stringBuilder.toString());
        BufferedWriter bw = new BufferedWriter(new FileWriter(file, false));
        bw.write(stringBuilder.toString());
        bw.close();
    }
}
