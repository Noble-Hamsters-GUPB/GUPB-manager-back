package com.gupb.manager.scheduler;

import com.gupb.manager.mails.MailService;
import com.gupb.manager.model.Round;
import com.gupb.manager.providers.GameProvider;
import com.gupb.manager.python.PythonPackageManagementException;
import com.gupb.manager.python.PythonPackageManager;
import com.gupb.manager.python.PythonRunner;
import com.gupb.manager.python.RunType;
import com.gupb.manager.repositories.RoundRepository;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.Executors;

@Component
public class SchedulerConfig {

    private static final TaskScheduler scheduler = new ConcurrentTaskScheduler(Executors.newSingleThreadScheduledExecutor());

    private static final String pathToGUPBDir = "GUPB-master";

    private static final String virtualenvName = "GUPB-venv";

    private static final String pathToRequirements = "requirements.txt";

    private static final String pathToLogsDirectory = "saved_logs";

    @Autowired
    private GameProvider gameProvider;

    @Autowired
    private PythonPackageManager pythonPackageManager;

    @Autowired
    private PythonRunner pythonRunner;

    @Autowired
    private MailService mailSender;

    @Autowired
    private RoundRepository roundRepository;

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

        pythonRunner.run(pathToGUPBDir, virtualenvName, RunType.NormalRun, round);

        try {
            String resultsDirPath = pathToGUPBDir + File.separator + "results";
            File resultsDir = new File(resultsDirPath);
            String[] logFiles = resultsDir.list();
            String logDirName = logFiles[0].replaceAll("\\.json", "").replaceAll("\\.log", "");
            String pathToNewLogDir = pathToLogsDirectory + File.separator + logDirName;
            File logsDirectory = new File(pathToNewLogDir);
            logsDirectory.mkdir();
            for (String fileName : logFiles) {
                File file = new File(resultsDirPath + File.separator + fileName);
                File movedFile = new File(pathToNewLogDir + File.separator + fileName);
                FileUtils.moveFile(file, movedFile);
            }
            round.setLogsPath(pathToNewLogDir);
        } catch (Exception ignored) {
            round.setLogsPath(null);
        } finally {
            roundRepository.save(round);
        }

        try {
            File file = new File(pathToGUPBDir);
            if (file.exists()) {
                FileUtils.deleteDirectory(file);
            }
        } catch (IOException ignored) { }

        mailSender.sendEmailsAfterRound(round);
    }

    public void appointMailsSending(Round round) {
        Date date = Date.from(round.getDate().minusHours(24).atZone(ZoneId.systemDefault()).toInstant());
        scheduler.schedule(() -> mailSender.sendEmailsToStudentsBeforeRoundBegins(round), date);
    }
}
