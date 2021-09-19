package com.joshings.taskmanager.model;

import com.joshings.taskmanager.service.TaskManager;
import lombok.Getter;

import java.sql.Timestamp;
import java.util.Date;

public final class Process {

    @Getter
    private final Integer processId;

    @Getter
    private final Priority priority;

    @Getter
    private final Timestamp startTime;

    private final TaskManager taskManager;

    public Process(Integer processId, Priority priority, TaskManager taskManager) {
        this.processId = processId;
        this.priority = priority;
        this.taskManager = taskManager;

        this.startTime = new Timestamp(new Date().getTime());
    }

    public void kill() {
      taskManager.killProcess(this);
    }

    public String toString() {
        return String.format("Process %5d - prio %4s - started at %s", processId, priority, startTime.toString());
    }
}
