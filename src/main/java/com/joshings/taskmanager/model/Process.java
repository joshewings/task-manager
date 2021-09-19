package com.joshings.taskmanager.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public final class Process {

    @Getter
    private final Integer processId;

    @Getter
    private final Priority priority;

    private final TaskManager taskManager;

    public void kill() {
      taskManager.killProcess(this);
    }
}
