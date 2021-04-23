package com.gupb.manager.scheduler;

import com.gupb.manager.python.PythonRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Component
public class SchedulerConfig {

    private TaskScheduler scheduler;

    @Autowired
    private PythonRunner pythonRunner;

    private Date taskDate;

    @Async
    public void planRound() {
        ScheduledExecutorService localExecutor = Executors.newSingleThreadScheduledExecutor();
        scheduler = new ConcurrentTaskScheduler(localExecutor);
        scheduler.schedule(pythonRunner, taskDate);
    }

    public void appointRound(LocalDateTime date) {
        this.taskDate = Date.from(date.atZone(ZoneId.systemDefault()).toInstant());
        planRound();
    }
}
