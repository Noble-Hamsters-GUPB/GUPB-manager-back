package com.gupb.manager.python;

import com.gupb.manager.model.BotStatus;
import com.gupb.manager.model.Team;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

@Component
public class PythonRunner implements Runnable {
    private static final OSType operatingSystem = OSType.getOSType();
    private static final ReentrantLock lock = new ReentrantLock();
    private final String appRootDirPath = System.getProperty("user.dir");
    private final String[] pathToPythonElements = {"src", "main", "java", "com", "gupb", "manager", "python"};
    private String runnerDirPathRelativeToApp = String.join(File.separator, pathToPythonElements) + File.separator;
    private String runnerDirPath = String.join(File.separator, new String[]{appRootDirPath, runnerDirPathRelativeToApp});
    private String gupbDirName = "GUPB-master";
    private String[] argsWindowsArray = {runnerDirPath + "script.bat", "\"" + runnerDirPath + "\"", gupbDirName};
    private String[] argsUnixArray = {"/usr/bin/env", "bash", runnerDirPath + "script.sh", "\"" + runnerDirPath + "\"", gupbDirName};

    private List<String> errorMessages;

    private List<String> outputMessages;

    private Team team;

    public PythonRunner() {}

    public void setExecutionPath(String path, String dirName) {
        runnerDirPathRelativeToApp = path;
        runnerDirPath = String.join(File.separator, new String[]{appRootDirPath, runnerDirPathRelativeToApp});
        gupbDirName = dirName;
        argsWindowsArray = new String[]{runnerDirPath + "script.bat", "\"" + runnerDirPath + "\"", gupbDirName};
        argsUnixArray = new String[]{"/usr/bin/env", "bash", runnerDirPath + "script.sh", "\"" + runnerDirPath + "\"", gupbDirName};
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    private void execProcess(String[] args) throws IOException, InterruptedException {
        errorMessages = new ArrayList<>();
        outputMessages = new ArrayList<>();

        Process process = Runtime.getRuntime().exec(args);
        BufferedReader brError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        BufferedReader brOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ( (line = brError.readLine()) != null) {
            errorMessages.add(line);
            System.out.println(line);
        }
        while ( (line = brOutput.readLine()) != null) {
            outputMessages.add(line);
            System.out.println(line);
        }

        process.waitFor();
        process.destroy();

    }

    @Override
    public void run() {
        try {
            lock.lock();
            switch (operatingSystem) {
                case Win:
                    execProcess(argsWindowsArray);
                    break;
                case Linux: case MacOS:
                    execProcess(argsUnixArray);
                    break;
                case Other:
                    throw new UnsupportedOS("Unsupported Operating System");
            }
        }
        catch (InterruptedException | IOException | UnsupportedOS e) {
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }
        processOutput();
    }

    private void processOutput() {
        switch (gupbDirName) {
            case "GUPB-master":
                break;
            case "GUPB-test":
                BotStatus status = null;
                for (String message : errorMessages) {
                    if (message.contains("Traceback (most recent call last):")) {
                        status = BotStatus.INCOMPLETE;
                        break;
                    }
                }
                if (status == null) {
                    if (!outputMessages.isEmpty()) {
                        status = BotStatus.READY;
                    }
                }
                team.setBotStatus(status);
                break;
        }
    }
}
