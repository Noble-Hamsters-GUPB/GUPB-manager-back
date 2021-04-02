package com.gupb.manager.scheduler;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Component
public class SchedulerConfig {

    private TaskScheduler scheduler;

    private Date taskDate;

    @Async
    public void planTournament() {
        Runnable runnable = () -> System.out.println("Tournament planned for " + taskDate + " started at " + new Date());
        ScheduledExecutorService localExecutor = Executors.newSingleThreadScheduledExecutor();
        scheduler = new ConcurrentTaskScheduler(localExecutor);
        scheduler.schedule(runnable, taskDate);
    }

    public void appointTournament(Date date) {
        this.taskDate = date;
        planTournament();
    }
}
