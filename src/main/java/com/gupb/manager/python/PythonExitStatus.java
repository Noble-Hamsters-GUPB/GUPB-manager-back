package com.gupb.manager.python;

public class PythonExitStatus {
    private final int shellSubprocessExitStatus;

    private final String exitMessage;

    PythonExitStatus(int shellSubprocessExitStatus, String exitMessage) {
        this.shellSubprocessExitStatus = shellSubprocessExitStatus;
        this.exitMessage = exitMessage;
    }

    public int getExitStatus() {
        return shellSubprocessExitStatus;
    }

    public String getExitMessage() {
        return exitMessage;
    }

    public boolean exitedWithError() {
        return shellSubprocessExitStatus != 0;
    }
}
