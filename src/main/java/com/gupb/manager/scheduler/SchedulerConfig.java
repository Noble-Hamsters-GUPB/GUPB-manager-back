package com.gupb.manager.scheduler;

import com.gupb.manager.model.Round;
import com.gupb.manager.providers.GameProvider;
import com.gupb.manager.python.PythonPackageManagementException;
import com.gupb.manager.python.PythonPackageManager;
import com.gupb.manager.python.PythonRunner;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Component
public class SchedulerConfig {

    private static final TaskScheduler scheduler = new ConcurrentTaskScheduler(Executors.newSingleThreadScheduledExecutor());

    private static final String pathToGUPBDir = "GUPB-master";

    private static final String virtualenvName = "GUPB-venv";

    private static final String pathToRequirements = "requirements.txt";

    @Autowired
    private GameProvider gameProvider;

    @Autowired
    private PythonPackageManager pythonPackageManager;

    @Autowired
    private PythonRunner pythonRunner;

    @Async
    public void planRound(Date taskDate, Round round) {
        scheduler.schedule(() -> setupAndRun(round), taskDate);
    }

    public void appointRound(Round round) {
        Date taskDate = Date.from(round.getDate().atZone(ZoneId.systemDefault()).toInstant());
        System.out.println(taskDate);
        planRound(taskDate, round);
    }

    private void setupAndRun(Round round) {
        try {
            gameProvider.provideRound(pathToGUPBDir, round);
            pythonPackageManager.createVirtualEnvironment(pathToGUPBDir, virtualenvName);
            String virtualenvPath = pathToGUPBDir + File.separator + virtualenvName;
            String requirementsPath = pathToGUPBDir + File.separator + pathToRequirements;
            pythonPackageManager.installPackagesFromRequirements(virtualenvPath, requirementsPath);
        } catch (PythonPackageManagementException | IOException | GitAPIException e) {
            e.printStackTrace();
        }

        pythonRunner.run(pathToGUPBDir, virtualenvName);
    }
}
