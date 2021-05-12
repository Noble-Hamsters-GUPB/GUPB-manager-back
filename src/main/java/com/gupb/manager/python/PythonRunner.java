package com.gupb.manager.python;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class PythonRunner {
    private static final OSType operatingSystem = OSType.os;

    private static final ReentrantLock lock = new ReentrantLock();

    private final String appRootPath = System.getProperty("user.dir");

    private String[] args = null;

    private void setExecutionPath(String pathFromAppRootToGUPBDir, String virtualenvName) {
        String pathToGUPBDir = appRootPath + File.separator + pathFromAppRootToGUPBDir;
        String pathToVirtualenv = pathToGUPBDir + File.separator + virtualenvName;
        setArgs(pathToGUPBDir, pathToVirtualenv, virtualenvName);
    }

    private void setArgs(String pathToGUPBDir, String pathToVirtualenv, String virtualenvName) {
        StringBuilder stringBuilder = new StringBuilder();

        switch (operatingSystem) {
            case Win:
                stringBuilder.append("cd ")
                        .append(pathToGUPBDir)
                        .append(" && py -m virtualenv ")
                        .append(virtualenvName)
                        .append(" && ")
                        .append(pathToVirtualenv)
                        .append(File.separator)
                        .append("Scripts")
                        .append(File.separator)
                        .append("activate.bat && py -m gupb && ")
                        .append(pathToVirtualenv)
                        .append(File.separator)
                        .append("Scripts")
                        .append(File.separator)
                        .append("deactivate.bat");
                args = new String[] {
                        "cmd",
                        "/c",
                        stringBuilder.toString()
                };
                break;
            case Linux: case MacOS:
                stringBuilder.append("cd ")
                        .append(pathToGUPBDir)
                        .append(" && python3 -m virtualenv ")
                        .append(virtualenvName)
                        .append(" && . ")
                        .append(pathToVirtualenv)
                        .append(File.separator)
                        .append("bin")
                        .append(File.separator)
                        .append("activate && python3 -m gupb && deactivate");
                args = new String[] {
                        "/usr/bin/env",
                        "sh",
                        "-c",
                        stringBuilder.toString()
                };
                break;
        }
    }

    private int execProcess() throws IOException, InterruptedException {
        System.out.println(String.join(" ", args));
        Process process = Runtime.getRuntime().exec(args);

        BufferedReader brError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        Thread errorStreamThread = new Thread(() -> processStreamOutput(brError,
                (line) -> {
                    System.out.println(line);
                }));

        BufferedReader brOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        Thread inputStreamThread = new Thread(() -> processStreamOutput(brOutput,
                (line) -> {
                    System.out.println(line);
                }));

        errorStreamThread.start();
        inputStreamThread.start();

        errorStreamThread.join();
        inputStreamThread.join();

        process.waitFor();
        System.out.println("Exit val: " + process.exitValue());
        int exitStatus = process.exitValue();
        process.destroy();

        return exitStatus;
    }

    public int run(String pathFromAppRootToGUPBDir, String virtualenvName) {
        int exitStatus = 1;
        try {
            lock.lock();
            setExecutionPath(pathFromAppRootToGUPBDir, virtualenvName);
            exitStatus = execProcess();
        }
        catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }

        return exitStatus;
    }

    private interface Callback {
        void call(String line);
    }

    private void processStreamOutput(BufferedReader br, Callback callback) {
        String line = null;
        while (true) {
            try {
                line = br.readLine();
            } catch (IOException ignored) { }

            if (line == null) break;

            callback.call(line);
        }
    }
}
