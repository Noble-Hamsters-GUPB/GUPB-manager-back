package com.gupb.manager.python;

import com.gupb.manager.model.Round;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class PythonRunner {
    private static final OSType operatingSystem = OSType.os;

    private static final ReentrantLock lock = new ReentrantLock();

    private final String appRootPath = System.getProperty("user.dir");

    private String[] args = null;

    @Autowired
    private OutputProcessing outputProcessing;

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

    private int execProcess(Callback stdOutputCallback, Callback stdErrorCallback) throws IOException, InterruptedException {
        System.out.println(String.join(" ", args));
        Process process = Runtime.getRuntime().exec(args);

        BufferedReader brError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        Thread errorStreamThread = new Thread(() -> processStream(brError, stdErrorCallback));

        BufferedReader brOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        Thread inputStreamThread = new Thread(() -> processStream(brOutput, stdOutputCallback));

        errorStreamThread.start();
        inputStreamThread.start();

        errorStreamThread.join();
        inputStreamThread.join();

        process.waitFor();
        int exitStatus = process.exitValue();
        process.destroy();

        return exitStatus;
    }

    public PythonExitStatus run(String pathFromAppRootToGUPBDir, String virtualenvName, RunType runType, Round round) {
        PythonExitStatus pythonExitStatus;
        try {
            lock.lock();
            setExecutionPath(pathFromAppRootToGUPBDir, virtualenvName);
            outputProcessing.reset();
            int exitStatus = 1;

            String outputDirPath = appRootPath + File.separator + pathFromAppRootToGUPBDir + File.separator + "results";
            String outputFilePath = outputDirPath + File.separator + "output.txt";
            File outputDir = new File(outputDirPath);
            outputDir.mkdir();
            File outputFile = new File(outputFilePath);
            outputFile.createNewFile();
            FileWriter outputFileWriter = new FileWriter(outputFilePath);

            switch (runType) {
                case TestRun:
                    exitStatus = execProcess(
                            line -> {
                                System.out.println(line);
                            },
                            line -> {
                                System.out.println(line);
                                outputProcessing.checkAndProcessTraceback(line);
                            }
                    );
                    break;
                case NormalRun:
                    exitStatus = execProcess(
                            line -> {
                                System.out.println(line);
                                synchronized (outputFileWriter) {
                                    try {
                                        outputFileWriter.write(line + "\n");
                                    } catch (IOException ignored) { }
                                }
                                outputProcessing.checkAndProcessScoreLine(line);
                            },
                            line -> {
                                System.out.println(line);
                                synchronized (outputFileWriter) {
                                    try {
                                        outputFileWriter.write(line + "\n");
                                    } catch (IOException ignored) { }
                                }
                                outputProcessing.checkAndProcessTqdmProgressBar(line, round);
                            }
                    );
                    break;
                case DevRun:
                    exitStatus = execProcess(System.out::println, System.out::println);
                    break;
            }

            outputFileWriter.close();

            pythonExitStatus = new PythonExitStatus(exitStatus, outputProcessing.getErrorMessage());
        }
        catch (InterruptedException | IOException e) {
            e.printStackTrace();
            pythonExitStatus = new PythonExitStatus(-1, "");
        }
        finally {
            lock.unlock();
        }

        return pythonExitStatus;
    }

    private interface Callback {
        void call(String line);
    }

    private void processStream(BufferedReader br, Callback callback) {
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
