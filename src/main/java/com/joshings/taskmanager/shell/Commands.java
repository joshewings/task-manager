package com.joshings.taskmanager.shell;

import com.joshings.taskmanager.model.Priority;
import com.joshings.taskmanager.model.Process;
import com.joshings.taskmanager.model.TaskManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.Optional;

@ShellComponent
public class Commands {

    @Autowired
    TaskManager taskManager;

    @ShellMethod("Add a process.")
    public String addProcess(
            @ShellOption(defaultValue = "low") Priority priority
    ) {
        try {
            Process process = taskManager.addProcess(priority);
            return "Created process with ID " + process.getProcessId();
        } catch (RuntimeException e) {
            return e.getMessage();
        }
    }

    @ShellMethod("Kill a process.")
    public String killProcess(
            @ShellOption Integer processId
    ) {
        Optional<Process> process = taskManager.getProcess(processId);

        if (process.isPresent()) {
            process.get().kill();
            return "Killed process with ID " + process.get().getProcessId();
        } else {
            return "Could not find a process with ID " + processId;
        }
    }
}