package com.gupb.manager.python;

import org.junit.jupiter.api.Test;

import java.io.File;

public class PythonRunnerTest {
    private static final PythonRunner p = new PythonRunner();

    private static final PythonPackageManager pm = new PythonPackageManager();

    private static final String pathToGUPBDir = "GUPB-master";
    private static final String virtualenvName = "GUPB-venv";

    @Test
    public void runTest() {
        try {
            pm.createVirtualEnvironment(pathToGUPBDir, virtualenvName);
            pm.installPackagesFromRequirements(pathToGUPBDir + File.separator + virtualenvName,
                    pathToGUPBDir + File.separator + "requirements.txt");
        } catch (PythonPackageManagementException ignored) { }
        p.run(pathToGUPBDir, virtualenvName);
    }
}
