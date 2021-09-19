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
            @ShellOption String prio
    ) {
        Priority priority;
        try {
            priority = Priority.fromString(prio);
        } catch (Exception e) {
            return e.getMessage();
        }

        try {
            Process process = taskManager.addProcess(priority);
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

    @ShellMethod("Get processes.")
    public String getProcesses() {

        List<Process> processList = taskManager.getProcesses();

        return processList.stream().map(Process::toString).collect(Collectors.joining("\n"));
    }
}