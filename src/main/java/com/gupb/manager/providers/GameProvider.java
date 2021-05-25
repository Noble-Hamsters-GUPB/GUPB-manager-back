package com.gupb.manager.providers;

import com.gupb.manager.git.GitUtilities;
import com.gupb.manager.model.PlayerStatus;
import com.gupb.manager.model.Round;
import com.gupb.manager.model.Team;
import com.gupb.manager.python.PythonPackageManagementException;
import com.gupb.manager.python.PythonPackageManager;
import com.gupb.manager.repositories.TeamRepository;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.List;

@Component
public class GameProvider {

    private static final String source = "https://github.com/janusz-tracz/gupb-original";//"https://github.com/Prpht/GUPB";

    private static final String configFile = "gupb" + File.separator + "default_config.py";

    private static final String controllerDirectoryName = "gupb" + File.separator + "controller" + File.separator;

    private static final String virtualenvName = "GUPB-venv";

    private static final String requirementsRelativePath = "requirements.txt";

    @Autowired
    private GitUtilities gitUtilities;

    @Autowired
    private RequirementProvider requirementProvider;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private PythonPackageManager pythonPackageManager;


    public void provideRound(String dirName, Round round) throws GitAPIException, IOException {

        List<Team> teamsInRound = teamRepository.findByTournament(round.getTournament());

        gitUtilities.cloneRepository(source, dirName, "master");
        String destination = dirName + File.separator + controllerDirectoryName;

        for (Team team : teamsInRound) {
            gitUtilities.cloneRepository(team.getGithubLink(), destination + File.separator + team.getSafeName(), "master");
        }

        StringBuilder stringBuilder = new StringBuilder();
        File file = new File(dirName + File.separator + configFile);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
            if (line.contains("'runs_no': 300,")) {
                stringBuilder.append("\t'runs_no': ").append(round.getNumberOfRuns()).append(",").append("\n");
            }
            else if (line.contains("random.RandomController(\"Alice\"),")) {
                for (Team team : teamsInRound) {
                    if (team.getPlayerStatus() == PlayerStatus.READY) {
                        stringBuilder.append("\t\t").append(team.getSafeName()).append(".")
                                .append(team.getMainClassName()).append("(").append("),\n");
                    }
                }
                for (int i = 0; i < 3; i++) {
                    br.readLine();
                }
            }
            else {
                stringBuilder.append(line).append("\n");
                if (line.contains("from gupb.controller import random")) {
                    for (Team team : teamsInRound) {
                        if (team.getPlayerStatus() == PlayerStatus.READY) {
                            stringBuilder.append("from gupb.controller import ").append(team.getSafeName()).append("\n");
                        }
                    }
                }
            }
        }
        br.close();
        System.out.println(stringBuilder.toString());
        BufferedWriter bw = new BufferedWriter(new FileWriter(file, false));
        bw.write(stringBuilder.toString());
        bw.close();

        requirementProvider.setRequirements(round.getTournament(), dirName + File.separator + requirementsRelativePath);

        String virtualenvPath = dirName + File.separator + virtualenvName;
        try {
            pythonPackageManager.createVirtualEnvironment(dirName, virtualenvName);
            pythonPackageManager.installPackagesFromRequirements(virtualenvPath, dirName + File.separator + requirementsRelativePath);
        } catch (PythonPackageManagementException e) {
            System.out.println(e.getLogs());
            e.printStackTrace();
        }
    }

    public void provideTestRoundWithBot(String dirName, Team team) throws GitAPIException, IOException {

        gitUtilities.cloneRepository(source, dirName, "master");
        String destination = dirName + File.separator + controllerDirectoryName + File.separator + team.getSafeName();
        gitUtilities.cloneRepository(team.getGithubLink(), destination, "master");

        StringBuilder stringBuilder = new StringBuilder();
        File file = new File(dirName + File.separator + configFile);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
            if (line.contains("'runs_no': 300,")) {
                stringBuilder.append("\t'runs_no': 5,").append("\n");
            }
            else {
                stringBuilder.append(line).append("\n");
                if (line.contains("from gupb.controller import random")) {
                    stringBuilder.append("from gupb.controller import ").append(team.getSafeName()).append("\n");
                }
                else if (line.contains("random.RandomController(\"Darius\"),")) {
                    stringBuilder.append("\t\t").append(team.getSafeName()).append(".")
                            .append(team.getMainClassName()).append("(").append(")\n");
                }
            }
        }
        br.close();
        System.out.println(stringBuilder.toString());
        BufferedWriter bw = new BufferedWriter(new FileWriter(file, false));
        bw.write(stringBuilder.toString());
        bw.close();

        requirementProvider.setRequirements(team.getTournament(), dirName + File.separator + requirementsRelativePath);

        String virtualenvPath = dirName + File.separator + virtualenvName;
        try {
            pythonPackageManager.createVirtualEnvironment(dirName, virtualenvName);
            pythonPackageManager.installPackagesFromRequirements(virtualenvPath, dirName + File.separator + requirementsRelativePath);
        } catch (PythonPackageManagementException e) {
            System.out.println(e.getLogs());
            e.printStackTrace();
        }
    }
}
