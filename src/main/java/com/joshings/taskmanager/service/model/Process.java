package com.joshings.taskmanager.service.model;

import com.joshings.taskmanager.service.TaskManagerService;
import lombok.Getter;

import java.sql.Timestamp;

/**
 * The type Process.
 */
public final class Process {

    @Getter
    private final Long processId;

    @Getter
    private final Priority priority;

    @Getter
    private final Timestamp startTime;

    /**
     * Instantiates a new Process.
     *
     * @param processId the process id
     * @param priority  the priority
     * @param startTime the start time
     */
    public Process(Long processId, Priority priority, Timestamp startTime) {
        this.processId = processId;
        this.priority = priority;
        this.startTime = startTime;
    }

    /**
     * Kill the process.
     *
     * @param taskManagerService the task manager service
     */
    public void kill(TaskManagerService taskManagerService) {
        taskManagerService.killProcess(this.processId);
    }

    public String toString() {
        return String.format("Process %5d - prio %6s - started at %s", processId, priority, startTime.toString());
    }
}
