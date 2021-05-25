package com.gupb.manager.python;

import com.gupb.manager.model.AccessMode;
import com.gupb.manager.model.Round;
import com.gupb.manager.model.Tournament;
import com.gupb.manager.repositories.RoundRepository;
import com.gupb.manager.repositories.TournamentRepository;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.time.LocalDateTime;

@RunWith(SpringRunner.class )
@SpringBootTest
public class PythonRunnerTest {
    @Autowired
    private PythonRunner p;

    private static final PythonPackageManager pm = new PythonPackageManager();

    private static final String pathToGUPBDir = "GUPB-master";
    private static final String virtualenvName = "GUPB-venv";

    @Test
    public void runTest() {

        Round round = new Round();

        try {
            pm.createVirtualEnvironment(pathToGUPBDir, virtualenvName);
            pm.installPackagesFromRequirements(pathToGUPBDir + File.separator + virtualenvName,
                    pathToGUPBDir + File.separator + "requirements.txt");
            PythonExitStatus es = p.run(pathToGUPBDir, virtualenvName, RunType.NormalRun, round);
            System.out.println("==========================================================");
            System.out.println("Exit status: " + es.getExitStatus());
            System.out.println("Exit message: " + es.getExitMessage());

        } catch (Exception ignored) { }
    }
}
