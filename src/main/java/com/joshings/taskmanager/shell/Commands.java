package com.joshings.taskmanager.shell;

import com.joshings.taskmanager.model.Priority;
import com.joshings.taskmanager.model.Process;
import com.joshings.taskmanager.service.TaskManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ShellComponent
public class Commands {

    @Autowired
    TaskManager taskManager;

    @ShellMethod("Add a process.")
    public String addProcess(
            @ShellOption(defaultValue = "low") String prio,
            @ShellOption(defaultValue = "dflt") String creationMode
    ) {
        Priority priority;
        try {
            priority = Priority.fromString(prio);
        } catch (Exception e) {
            return e.getMessage();
        }

        try {
            Process process = taskManager.addProcess(
                    priority,
                    creationMode.equals("__NONE__") ? TaskManager.CreationMode.Default : TaskManager.CreationMode.fromString(creationMode));
            return "Created process with ID " + process.getProcessId();
        } catch (RuntimeException e) {
            return e.getMessage();
        }
    }

    @ShellMethod("Kill a process.")
    public String killProcess(
            @ShellOption Integer pid
    ) {
        Optional<Process> process = taskManager.getProcess(pid);

        if (process.isPresent()) {
            process.get().kill();
            return "Killed process with ID " + process.get().getProcessId();
        } else {
            return "Could not find a process with ID " + pid;
        }
    }

    @ShellMethod("Kill processes with the given priority.")
    public String killGroup(
            @ShellOption String prio
    ) {
        Priority priority;
        try {
            priority = Priority.fromString(prio);
        } catch (Exception e) {
            return e.getMessage();
        }

        taskManager.killGroup(priority);
        return "Killed process with priority " + priority.getPriorityName();
    }

    @ShellMethod("Kill all processes.")
    public String killAll() {

        taskManager.killAll();
        return "Killed all processes";
    }

    @ShellMethod("Get processes.")
    public String getProcesses(
            @ShellOption(defaultValue = "timestamp") String sortMode
    ) {

        List<Process> processList = taskManager.getProcesses(
                sortMode.equals("__NONE__") ? TaskManager.SortMode.Timestamp : TaskManager.SortMode.fromString(sortMode));

        return processList.stream().map(Process::toString).collect(Collectors.joining("\n"));
    }
}