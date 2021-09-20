package com.joshings.taskmanager.service.model;

import com.joshings.taskmanager.service.TaskManagerService;
import lombok.Getter;

import java.sql.Timestamp;

public final class Process {

    @Getter
    private final Long processId;

    @Getter
    private final Priority priority;

    @Getter
    private final Timestamp startTime;

    public Process(Long processId, Priority priority, Timestamp startTime) {
        this.processId = processId;
        this.priority = priority;
        this.startTime = startTime;
    }

    public void kill(TaskManagerService taskManagerService) {
        taskManagerService.killProcess(this.processId);
    }

    public String toString() {
        return String.format("Process %5d - prio %6s - started at %s", processId, priority, startTime.toString());
    }
}
