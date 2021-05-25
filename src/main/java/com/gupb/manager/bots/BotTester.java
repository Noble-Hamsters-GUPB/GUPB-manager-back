package com.gupb.manager.bots;

import com.gupb.manager.model.PlayerStatus;
import com.gupb.manager.model.Team;
import com.gupb.manager.python.PythonExitStatus;
import com.gupb.manager.python.PythonRunner;
import com.gupb.manager.python.RunType;
import com.gupb.manager.repositories.TeamRepository;
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

    @Autowired
    private TeamRepository teamRepository;

    public void testTeamBot(Team team) {
        String teamDirName = dirName + team.getSafeName();
        Thread thread = new Thread(() -> {
            team.setPlayerStatus(PlayerStatus.IN_TESTING);
            teamRepository.save(team);
            try {
                gameProvider.provideTestRoundWithBot(teamDirName, team);
            } catch (IOException | GitAPIException e) {
                team.setPlayerStatus(PlayerStatus.INCOMPLETE);
                team.setMessage("The bot couldn't be tested properly.");
                return;
            }

            PythonExitStatus pythonExitStatus = pythonRunner.run(teamDirName, virtualenvName, RunType.TestRun, null);

            if (pythonExitStatus.exitedWithError()) {
                team.setPlayerStatus(PlayerStatus.INCOMPLETE);
                String message = pythonExitStatus.getExitMessage();
                if (message.length() > Team.MESSAGE_MAX_LENGTH) {
                    message = message.substring(message.length() - Team.MESSAGE_MAX_LENGTH);
                }
                team.setMessage(message);
            }
            else {
                team.setPlayerStatus(PlayerStatus.READY);
                team.setMessage("No errors.");
            }
            teamRepository.save(team);
            try {
                File file = new File(teamDirName);
                if (file.exists()) {
                    FileUtils.deleteDirectory(file);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        thread.start();
    }
}
