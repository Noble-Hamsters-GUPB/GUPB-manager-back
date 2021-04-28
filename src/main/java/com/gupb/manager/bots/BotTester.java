package com.gupb.manager.bots;

import com.gupb.manager.model.Team;
import com.gupb.manager.python.PythonRunner;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.gupb.manager.providers.GameProvider;

import java.io.*;

@Component
public class BotTester {

    private static final String[] pathToGUPBTest = {"src", "main", "java", "com", "gupb", "manager", "python"};

    private static final String dirName = "GUPB-test";

    private static final String pathRelativeToApp = String.join(File.separator, pathToGUPBTest) + File.separator;

    @Autowired
    private GameProvider gameProvider;

    @Autowired
    private PythonRunner pythonRunner;

    public void testTeamBot(Team team) throws IOException, GitAPIException {

        gameProvider.provideTestRoundWithBot(pathRelativeToApp, dirName, team);
        pythonRunner.setExecutionPath(pathRelativeToApp, dirName);
        pythonRunner.setTeam(team);
        pythonRunner.run();
    }
}
