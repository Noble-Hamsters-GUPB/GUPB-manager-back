package com.gupb.manager.bots;

import com.gupb.manager.model.BotStatus;
import com.gupb.manager.model.Team;
import com.gupb.manager.python.PythonRunner;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class )
@SpringBootTest
public class BotTest {

    @Autowired
    private BotTester botTester;

    @Test
    public void runTest() throws IOException, GitAPIException {
        Team team = new Team("testTeam", "https://github.com/Tequilac/TestBot");
        team.setBotName("TestBot");
        team.setBotStatus(BotStatus.IN_TESTING);
        team.setControllerClassName("BotController");
        team.setPackageName("test_bot");
        botTester.testTeamBot(team);
    }
}
