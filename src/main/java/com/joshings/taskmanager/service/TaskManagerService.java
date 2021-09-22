package com.joshings.taskmanager.service;

import com.joshings.taskmanager.repository.ProcessRepository;
import com.joshings.taskmanager.repository.model.ProcessEntity;
import com.joshings.taskmanager.service.configuration.TaskManagerConfigurationProperties;
import com.joshings.taskmanager.service.converter.ProcessEntityConverter;
import com.joshings.taskmanager.service.model.Priority;
import com.joshings.taskmanager.service.model.Process;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * The Task manager (TM) service.
 * A software component that is designed for handling multiple processes inside an operating system.
 */
@Service
public class TaskManagerService {

    /**
     * The available modes for adding a process to the TM.
     */
    public enum AddMode {
        /**
         * Default add mode.
         * The default behaviour is that we can accept new processes till when there is capacity inside the Task
         * Manager, otherwise we won’t accept any new process.
         */
        Default("dflt"),
        /**
         * Fifo add mode.
         * Accept all new processes through the addProcess() method, killing and removing from the TM list the oldest
         * one (First-In, First-Out) when the max size is reached.
         */
        Fifo("fifo"),
        /**
         * Prio add mode.
         * Every call to the addProcess() method, when the max size is reached, should result into an evaluation: if the
         * new process passed in the add() call has a higher priority compared to any of the existing one, we remove the
         * lowest priority that is the oldest, otherwise we skip it.
         */
        Prio("prio");

        private final String modeName;

        AddMode(String modeName) {
            this.modeName = modeName;
        }

        /**
         * Gets mode name.
         *
         * @return the mode name
         */
        public String getModeName() {
            return this.modeName;
        }

        /**
         * Get the Add Mode from its name.
         *
         * @param modeName the mode name
         * @return the add mode
         */
        public static AddMode fromString(String modeName) {
            for (AddMode addMode : AddMode.values()) {
                if (addMode.modeName.equalsIgnoreCase(modeName)) {
                    return addMode;
                }
            }
            throw new IllegalArgumentException("Unrecognized creation mode");
        }
    }

    /**
     * The available modes for sorting the process list which is retrieved from the TM.
     */
    public enum SortMode {
        /**
         * Sort by start time.
         */
        StartTime("start_time"),
        /**
         * Sort by process ID.
         */
        Pid("pid"),
        /**
         * Sort by priority.
         */
        Prio("prio");

        private final String modeName;

        SortMode (String modeName) {
            this.modeName = modeName;
        }

        /**
         * Gets mode name.
         *
         * @return the mode name
         */
        public String getModeName() {
            return this.modeName;
        }

        /**
         * Get the mode from its name.
         *
         * @param modeName the mode name
         * @return the sort mode
         */
        public static SortMode fromString(String modeName) {
            for (SortMode sortMode : SortMode.values()) {
                if (sortMode.modeName.equalsIgnoreCase(modeName)) {
                    return sortMode;
                }
            }
            throw new IllegalArgumentException("Unrecognized sort mode");
        }
    }

    private final TaskManagerConfigurationProperties taskManagerConfigurationProperties;
    private final ProcessRepository processRepository;
    private final ProcessEntityConverter processEntityConverter;

    /**
     * Instantiates a new Task manager service.
     *
     * @param processRepository      the process repository
     * @param processEntityConverter the process entity converter
     */
    @Autowired
    public TaskManagerService(TaskManagerConfigurationProperties taskManagerConfigurationProperties,
                              ProcessRepository processRepository,
                              ProcessEntityConverter processEntityConverter
    ) {
        this.taskManagerConfigurationProperties = taskManagerConfigurationProperties;
        this.processRepository = processRepository;
        this.processEntityConverter = processEntityConverter;
    }

    /**
     * Gets processes.
     *
     * @param sortMode the sort mode
     * @return the processes
     */
    public List<Process> getProcesses(SortMode sortMode) {

        Iterable<ProcessEntity> processEntityIterable;

        switch (sortMode) {
            case StartTime:
                processEntityIterable = processRepository.findAllByOrderByStartTime();
                break;
            case Pid:
                processEntityIterable = processRepository.findAllByOrderById();
                break;
            case Prio:
                processEntityIterable = processRepository.findAllByOrderByPriority();
                break;
            default:
                throw new IllegalArgumentException("Unrecognized sort mode");
        }

        return StreamSupport.stream(processEntityIterable.spliterator(), false)
                .map(processEntityConverter::convert)
                .collect(Collectors.toList());
    }

    /**
     * Get a list of processes which is sorted by start time.
     *
     * @return the processes
     */
    public List<Process> getProcesses() {
        return getProcesses(SortMode.StartTime);
    }

    /**
     * Add a new process according to the provided add mode.
     *
     * @param priority the priority
     * @param addMode  the add mode
     * @return the process
     * @throws InstantiationException the instantiation exception
     */
    public Process addProcess(Priority priority, AddMode addMode) throws InstantiationException {
        this.beforeStartProcess(priority, addMode);
        return this.startProcess(priority);
    }

    /**
     * Add a new process accoring to the default mode.
     *
     * @param priority the priority
     * @return the process
     * @throws InstantiationException the instantiation exception
     */
    public Process addProcess(Priority priority) throws InstantiationException {
        return this.addProcess(priority, AddMode.Default);
    }

    /**
     * Kill a specific process.
     *
     * @param processId the process id
     */
    public void killProcess(Long processId) {
        processRepository.deleteById(processId);
    }

    /**
     * Kill group of processes with the given priority.
     *
     * @param priority the priority
     */
    public void killGroup(Priority priority) {
        Iterable<ProcessEntity> processEntityWithPriorityIterable =
                processRepository.findAllByPriorityOrderById(priority.getPriorityValue());

        List<Long> idsOfProcessesToKill = StreamSupport.stream(
                processEntityWithPriorityIterable.spliterator(),
                        false
                )
                .map(processEntityConverter::convert)
                .map(Process::getProcessId)
                .collect(Collectors.toList());

        processRepository.deleteAllById(idsOfProcessesToKill);
    }

    /**
     * Kill all processes.
     */
    public void killAll() {
        processRepository.deleteAll();
    }

    private void beforeStartProcess(Priority priority, AddMode addMode) throws InstantiationException {

        long runningProcessCount = processRepository.count();

        if (runningProcessCount >= taskManagerConfigurationProperties.getMaxProcesses()) {
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

        if (processRepository.count() >= taskManagerConfigurationProperties.getMaxProcesses()) {
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
