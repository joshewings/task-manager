package com.joshings.taskmanager.service;

import com.joshings.taskmanager.model.Priority;
import com.joshings.taskmanager.model.Process;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Scope("singleton")
public class TaskManager {

    public enum CreationMode {
        Default("dflt"),
        Fifo("fifo"),
        Prio("prio");

        private final String modeName;

        CreationMode (String modeName) {
            this.modeName = modeName;
        }

        public String getModeName() {
            return this.modeName;
        }

        public static CreationMode fromString(String priorityName) {
            for (CreationMode creationMode : CreationMode.values()) {
                if (creationMode.modeName.equalsIgnoreCase(priorityName)) {
                    return creationMode;
                }
            }
            throw new IllegalArgumentException("Unrecognized creation mode");
        }
    }

    public enum SortMode {
        Timestamp("timestamp"),
        Pid("pid"),
        Prio("prio");

        private final String modeName;

        SortMode (String modeName) {
            this.modeName = modeName;
        }

        public String getModeName() {
            return this.modeName;
        }

        public static SortMode fromString(String priorityName) {
            for (SortMode sortMode : SortMode.values()) {
                if (sortMode.modeName.equalsIgnoreCase(priorityName)) {
                    return sortMode;
                }
            }
            throw new IllegalArgumentException("Unrecognized sort mode");
        }
    }

    private final Integer maxProcesses;

    private AtomicInteger nextProcessId;
    private Map<Integer, Process> processMap;

    @Autowired
    public TaskManager(@Value("${com.joshings.taskmanager.maxProcesses}") Integer maxProcesses) {
        this.maxProcesses = maxProcesses;
        this.processMap = new HashMap<>();
        this.nextProcessId = new AtomicInteger();
    }

    public List<Process> getProcesses(SortMode sortMode) {
        switch (sortMode) {
            case Timestamp:
                return processMap.values().stream()
                        .sorted(Comparator.comparingLong(prc -> prc.getStartTime().getTime()))
                        .collect(Collectors.toList());
            case Pid:
                return processMap.keySet().stream()
                        .sorted()
                        .map(key -> getProcess(key).get()).collect(Collectors.toList());
            case Prio:
                return processMap.values().stream()
                        .sorted(Comparator.comparingInt(prc -> prc.getPriority().getPriorityValue()))
                        .collect(Collectors.toList());
            default:
                throw new IllegalArgumentException("Unrecognized sort mode");
        }
    }

    public Process addProcess(Priority priority, CreationMode creationMode) throws RuntimeException {

        if (maxProcesses.intValue() <= processMap.size()) {

            switch (creationMode) {
                case Default:
                    throw new RuntimeException(
                            new StringBuilder("Could not add a new process.\n")
                                    .append("The configured maximum number of processes (")
                                    .append(maxProcesses)
                                    .append(") has been reached")
                                    .toString()
                    );
                case Fifo:
                    killProcess(getOldestProcess().get());
                    break;
                case Prio:
                    if (getLowestPriorityProcess().get().getPriority().getPriorityValue() < priority.getPriorityValue()) {
                        killProcess(getLowestPriorityProcess().get());
                    } else {
                        throw new RuntimeException(
                                new StringBuilder("Could not add a new process.\n")
                                        .append("The configured maximum number of processes (")
                                        .append(maxProcesses)
                                        .append(") has been reached, ")
                                        .append("and there are no running processes with priority lower than ")
                                        .append(priority.getPriorityValue())
                                        .toString()
                        );
                    }
            }
        }

        Process newProcess = new Process(nextProcessId.getAndIncrement(), priority, this);
        processMap.put(newProcess.getProcessId(), newProcess);
        return newProcess;
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

    public void killGroup(Priority priority) {
        processMap.values().stream()
                .filter(prc -> prc.getPriority().equals(priority))
                .forEach(this::killProcess);
    }

    public void killAll() {
        processMap.values()
                .forEach(this::killProcess);
    }

    private Optional<Process> getOldestProcess() {
        return processMap.values().stream().min(Comparator.comparingLong(prc -> prc.getStartTime().getTime()));
    }

    private Optional<Process> getLowestPriorityProcess() {
        Optional<Process> lowestPriorityProcess = processMap.values().stream()
                .min(Comparator.comparingInt(prc -> prc.getPriority().getPriorityValue()));

        return lowestPriorityProcess.flatMap(process -> processMap.values().stream()
                .filter(prc -> prc.getPriority().equals(process.getPriority()))
                .min(Comparator.comparingLong(prc -> prc.getStartTime().getTime())));
    }
}
