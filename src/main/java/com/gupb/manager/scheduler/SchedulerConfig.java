package com.gupb.manager.scheduler;

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

    private Date taskDate;

    @Async
    public void planTournament() {
        Runnable runnable = () -> System.out.println("Tournament planned for " + taskDate + " started at " + new Date());
        ScheduledExecutorService localExecutor = Executors.newSingleThreadScheduledExecutor();
        scheduler = new ConcurrentTaskScheduler(localExecutor);
        scheduler.schedule(runnable, taskDate);
    }

    public void appointTournament(LocalDateTime date) {
        this.taskDate = Date.from(date.atZone(ZoneId.systemDefault()).toInstant());
        planTournament();
    }
}
