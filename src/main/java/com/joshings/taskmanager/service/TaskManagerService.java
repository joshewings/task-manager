package com.joshings.taskmanager.service;

import com.joshings.taskmanager.repository.ProcessRepository;
import com.joshings.taskmanager.repository.model.ProcessEntity;
import com.joshings.taskmanager.service.converter.ProcessEntityConverter;
import com.joshings.taskmanager.service.model.Priority;
import com.joshings.taskmanager.service.model.Process;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class TaskManagerService {

    public enum AddMode {
        Default("dflt"),
        Fifo("fifo"),
        Prio("prio");

        private final String modeName;

        AddMode(String modeName) {
            this.modeName = modeName;
        }

        public String getModeName() {
            return this.modeName;
        }

        public static AddMode fromString(String priorityName) {
            for (AddMode addMode : AddMode.values()) {
                if (addMode.modeName.equalsIgnoreCase(priorityName)) {
                    return addMode;
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

    private final ProcessRepository processRepository;
    private final ProcessEntityConverter processEntityConverter;
    private final Integer maxProcesses;

    @Autowired
    public TaskManagerService(ProcessRepository processRepository,
                              ProcessEntityConverter processEntityConverter,
                              @Value("${com.joshings.taskmanager.maxProcesses}") Integer maxProcesses
    ) {
        this.maxProcesses = maxProcesses;
        this.processRepository = processRepository;
        this.processEntityConverter = processEntityConverter;
    }

    public List<Process> getProcesses(SortMode sortMode) {

        Iterable<ProcessEntity> processEntityIterable = processRepository.findAll();

        return StreamSupport.stream(processEntityIterable.spliterator(), false)
                .map(processEntityConverter::convert)
                .sorted(getComparator(sortMode))
                .collect(Collectors.toList());
    }

    public List<Process> getProcesses() {
        return getProcesses(SortMode.Timestamp);
    }

    public Process addProcess(Priority priority, AddMode addMode) throws InstantiationException {
        this.beforeStartProcess(priority, addMode);
        return this.startProcess(priority);
    }

    public Process addProcess(Priority priority) throws InstantiationException {
        return this.addProcess(priority, AddMode.Default);
    }

    public Optional<Process> getProcess(Long processId) {
        Optional<ProcessEntity> processEntity = processRepository.findById(processId);
        return processEntity.map(processEntityConverter::convert);
    }

    public void killProcess(Long processId) {
        processRepository.deleteById(processId);
    }

    public void killGroup(Priority priority) {
        Iterable<ProcessEntity> processEntityIterable = processRepository.findAll();

        List<Long> idsOfProcessesToKill = StreamSupport.stream(processEntityIterable.spliterator(), false)
                .map(processEntityConverter::convert)
                .filter(prc -> prc.getPriority().equals(priority))
                .map(Process::getProcessId)
                .collect(Collectors.toList());

        processRepository.deleteAllById(idsOfProcessesToKill);
    }

    public void killAll() {
        processRepository.deleteAll();
    }

    private static Comparator<Process> getComparator(SortMode sortMode) {
        switch (sortMode) {
            case Timestamp:
                return Comparator.comparingLong(prc -> prc.getStartTime().getTime());
            case Pid:
                return Comparator.comparingLong(Process::getProcessId);
            case Prio:
                return Comparator.comparingLong(prc -> prc.getPriority().getPriorityValue());
            default:
                throw new IllegalArgumentException("Unrecognized sort mode");
        }
    }

    private void beforeStartProcess(Priority priority, AddMode addMode) throws InstantiationException {

        long runningProcessCount = processRepository.count();

        if (runningProcessCount >= this.maxProcesses) {
            switch (addMode) {
                case Default:
                    throw new InstantiationException("Add mode: " + AddMode.Default + ". " +
                            "The maximum allowed number of processes is running.");
                case Fifo:
                    Optional<Process> oldestProcess = this.getOldestProcess();
                    oldestProcess.ifPresent(process -> this.killProcess(process.getProcessId()));
                    break;
                case Prio:
                    Optional<Priority> lowestPriority = this.getLowestProcessPriority();
                    if (lowestPriority.isPresent() &&
                            lowestPriority.get().getPriorityValue() < priority.getPriorityValue()
                    ) {
                        Optional<Process> oldestProcessWithPriority =
                                this.getOldestProcessWithPriority(lowestPriority.get());
                        oldestProcessWithPriority.ifPresent(process -> this.killProcess(process.getProcessId()));
                    } else {
                        throw new InstantiationException("Add mode: " + AddMode.Prio + ". " +
                                "The maximum allowed number of processes is running and no processes with " +
                                "priority lower than " + priority.getPriorityName() + " were found.");
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized sort mode");
            }
        }
    }

    private Process startProcess(Priority priority) throws InstantiationException {

        if (processRepository.count() >= this.maxProcesses) {
            throw new InstantiationException("The maximum allowed number of processes is running.");
        }

        ProcessEntity newProcessEntity = new ProcessEntity(
                priority.getPriorityValue(),
                new Timestamp(new Date().getTime())
        );

        return processEntityConverter.convert(processRepository.save(newProcessEntity));
    }

    private Optional<Process> getOldestProcess() {
        return processRepository.findFirstByOrderByStartTime().map(processEntityConverter::convert);
    }

    private Optional<Process> getOldestProcessWithPriority(Priority priority) {
        return processRepository.findFirstByPriorityOrderByStartTime(priority.getPriorityValue()).map(processEntityConverter::convert);
    }

    private Optional<Priority> getLowestProcessPriority() {
        return processRepository.getLowestPriority().map(Priority::fromLong);
    }
}
