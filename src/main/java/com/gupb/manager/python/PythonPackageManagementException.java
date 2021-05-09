package com.gupb.manager.python;

public class PythonPackageManagementException extends Exception {
    private final int exitStatus;
    private final String logs;

    public PythonPackageManagementException(String message, int exitStatus, String logs) {
        super(message);
        this.exitStatus = exitStatus;
        this.logs = logs;
    }

    int getExitStatus() {
        return exitStatus;
    }

    public String getLogs() {
        return logs;
    }
}
