package com.gupb.manager.bots;

import com.gupb.manager.model.BotStatus;
import com.gupb.manager.model.Team;
import com.gupb.manager.python.PythonRunner;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.gupb.manager.providers.GameProvider;

import java.io.*;

@Component
public class BotTester {

    private static final String dirName = "GUPB-test";

    private static final String virtualenvName = "GUPB-venv";

    @Autowired
    private GameProvider gameProvider;

    @Autowired
    private PythonRunner pythonRunner;

    public void testTeamBot(Team team) {
        int exitStatus;
        String teamDirName = dirName + team.getName().replaceAll("[^a-zA-Z0-9]", "");

        try {
            gameProvider.provideTestRoundWithBot(teamDirName, team);
            exitStatus = pythonRunner.run(teamDirName, virtualenvName);
        } catch (IOException | GitAPIException e) {
            exitStatus = 1;
        }
        if (exitStatus != 0) {
            team.setBotStatus(BotStatus.INCOMPLETE);
            team.setMessage("The bot couldn't be tested properly.");
        }
        else {
            team.setBotStatus(BotStatus.READY);
            team.setMessage("No errors.");
        }
        try {
            File file = new File(teamDirName);
            if (file.exists()) {
                FileUtils.deleteDirectory(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
