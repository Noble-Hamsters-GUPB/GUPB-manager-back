package com.gupb.manager.GUPBManager;

import com.gupb.manager.python.PythonRunner;
import org.junit.jupiter.api.Test;

public class PythonRunnerTest {
    private static final PythonRunner p = new PythonRunner();

    @Test
    public void runTest() {
        Thread t = new Thread(p);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
