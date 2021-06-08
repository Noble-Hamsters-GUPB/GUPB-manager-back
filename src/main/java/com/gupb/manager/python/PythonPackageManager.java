package com.gupb.manager.python;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

@Component
public class PythonPackageManager {

    private static final String[] pipOperators = new String[] {"<", ">", "<=", ">=", "==", "!=", "~="};

    private static final String pypiSearchUrl = "https://pypi.org/search/";

    private static final String pypiRequestParamQ = "q";

    private static final String pypiRequestParamPage = "page";

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
                process.destroy();
                throw new PythonPackageManagementException(exceptionMessage, exitStatus, sb.toString());
            }
            process.destroy();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public boolean pythonPackageExists(String packageString) {

        for (String op : pipOperators) {
            packageString = packageString.split(op)[0];
        }

        final String processedName = packageString.trim();

        String uriString = UriComponentsBuilder.fromHttpUrl(pypiSearchUrl)
                .queryParam(pypiRequestParamQ, processedName)
                .queryParam(pypiRequestParamPage, 1)
                .build()
                .toUriString();

        try {
            Document doc = Jsoup.connect(uriString).get();
            return doc.select("a[class*=\"snippet\"]")
                    .stream()
                    .map(snippet -> snippet.select("span[class*=\"name\"]"))
                    .map(Elements::text)
                    .anyMatch(text -> text.equals(processedName));
        } catch (IOException e) {
            return false;
        }
    }
}
