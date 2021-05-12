package com.gupb.manager.python;

import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

@Component
public class PythonPackageManager {

    public void createVirtualEnvironment(String pathToVirtualEnvironmentParent, String virtualEnvironmentName) throws PythonPackageManagementException {
        String[] cmd = null;
        StringBuilder sb = new StringBuilder();

        switch (OSType.os) {
            case Win:
                sb.append("py -m pip install virtualenv && cd ")
                        .append(pathToVirtualEnvironmentParent)
                        .append(" && py -m virtualenv ")
                        .append(virtualEnvironmentName);
                cmd = new String[]{
                        "cmd",
                        "/C",
                        sb.toString()
                };
                break;
            case Linux: case MacOS:
                sb.append("python3 -m pip install virtualenv && cd ")
                        .append(pathToVirtualEnvironmentParent)
                        .append(" && python3 -m virtualenv ")
                        .append(virtualEnvironmentName);
                cmd = new String[]{
                        "/usr/bin/env",
                        "sh",
                        "-c",
                        sb.toString()
                };
                break;
        }

        commandRunner(cmd);
    }

    public void removeVirtualEnvironment(String pathToVirtualEnvironment) {
        File file = new File(pathToVirtualEnvironment);
        if (file.exists()) {
            FileSystemUtils.deleteRecursively(file);
        }
    }

    public void installPackagesFromRequirements(String pathToVirtualEnvironment, String pathToRequirements) throws PythonPackageManagementException {
        String[] cmd = null;
        StringBuilder sb = new StringBuilder();

        switch (OSType.os) {
            case Win:
                sb.append(pathToVirtualEnvironment)
                        .append(File.separator)
                        .append("Scripts")
                        .append(File.separator)
                        .append("activate.bat && py -m pip install -r ")
                        .append(pathToRequirements)
                        .append(" && deactivate");
                cmd = new String[]{
                        "cmd",
                        "/C",
                        sb.toString()
                };
                break;
            case Linux: case MacOS:
                sb.append(". ")
                        .append(pathToVirtualEnvironment)
                        .append(File.separator)
                        .append("bin")
                        .append(File.separator)
                        .append("activate && python3 -m pip install -r ")
                        .append(pathToRequirements)
                        .append(" && deactivate");
                cmd = new String[]{
                        "/usr/bin/env",
                        "sh",
                        "-c",
                        sb.toString()
                };
                break;
        }

        commandRunner(cmd);
    }

    private void commandRunner(String[] cmd) throws PythonPackageManagementException {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(cmd);
            switch (OSType.os) {
                case Win:
                    processBuilder.redirectOutput(new File("NUL"));
                    break;
                case Linux: case MacOS:
                    processBuilder.redirectOutput(new File("/dev/null"));
                    break;
            }
            Process process = processBuilder.start();
            process.waitFor();
            int exitStatus = process.exitValue();
            if (exitStatus != 0) {
                String exceptionMessage = "Command: " + String.join(" ", cmd);
                StringBuilder sb = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                    System.out.println(line);
                }
                throw new PythonPackageManagementException(exceptionMessage, exitStatus, sb.toString());
            }
            process.destroy();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}
