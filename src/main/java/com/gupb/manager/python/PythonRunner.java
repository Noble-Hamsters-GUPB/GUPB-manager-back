package com.gupb.manager.python;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.locks.ReentrantLock;

public class PythonRunner implements Runnable {
    private static final OSType operatingSystem = OSType.getOSType();
    private static final ReentrantLock lock = new ReentrantLock();
    private final String appRootDirPath = System.getProperty("user.dir");
    private final String[] pathToPythonElements = {"src", "main", "java", "com", "gupb", "manager", "python"};
    private final String runnerDirPathRelativeToApp = String.join(File.separator, pathToPythonElements) + File.separator;
    private final String runnerDirPath = String.join(File.separator, new String[]{appRootDirPath, runnerDirPathRelativeToApp});

    String[] argsWindowsArray = {runnerDirPath + "script.bat", "\"" + runnerDirPath + "\"", "GUPB-master"};
    String[] argsUnixArray = {"/usr/bin/env", "bash", "script.sh"};

    public PythonRunner() {}

    private void execProcess(String[] args) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(args);
        BufferedReader brError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        BufferedReader brOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ( (line = brError.readLine()) != null) {
            System.out.println(line);
        }
        while ( (line = brOutput.readLine()) != null) {
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
    }
}
