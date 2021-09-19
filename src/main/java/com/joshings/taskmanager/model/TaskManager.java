package com.joshings.taskmanager.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Scope("singleton")
public class TaskManager {

    private final Integer maxProcesses;

    private AtomicInteger nextProcessId;
    private Map<Integer, Process> processMap;

    @Autowired
    public TaskManager(@Value("com.joshings.taskmanager.maxProcesses") Integer maxProcesses) {
        this.maxProcesses = maxProcesses;
        this.processMap = new HashMap<>();
        this.nextProcessId = new AtomicInteger();
    }

    public List<Process> getTasks() {
        return new ArrayList<>(processMap.values());
    }

    public Process addProcess(Priority priority) throws RuntimeException {

        if (maxProcesses.intValue() > processMap.size()) {
            Process newProcess = new Process(nextProcessId.getAndIncrement(), priority, this);
            processMap.put(newProcess.getProcessId(), newProcess);
            return newProcess;
        } else {
            throw new RuntimeException(
                    new StringBuilder("Could not add a new process.\n")
                            .append("The configured maximum number of processes (")
                            .append(maxProcesses)
                            .append(") has been reached")
                            .toString()
            );
        }
    }

    public Optional<Process> getProcess(Integer processId) {
        Process process = processMap.get(processId);

        if (process == null) {
            return Optional.empty();
        }
        return Optional.of(process);
    }

    public void killProcess(Process process) {
        processMap.remove(process.getProcessId());
    }
}
