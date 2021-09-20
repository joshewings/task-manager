package com.joshings.taskmanager.shell;

import com.joshings.taskmanager.service.model.Priority;
import com.joshings.taskmanager.service.model.Process;
import com.joshings.taskmanager.service.TaskManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.List;
import java.util.stream.Collectors;

@ShellComponent
public class Commands {

    public static final String DFLT = "dflt";
    @Autowired
    TaskManagerService taskManagerService;

    @ShellMethod("Add a process.")
    public String addProcess(
            @ShellOption String prio,
            @ShellOption(defaultValue = DFLT) String addMode
    ) {
        Process process;
        try {
            Priority priority = Priority.fromString(prio);
            process = addMode.equals(DFLT) ?
                    taskManagerService.addProcess(priority) :
                    taskManagerService.addProcess(
                            priority,
                            TaskManagerService.AddMode.fromString(addMode)
                    );
        } catch (Exception e) {
            return e.getMessage();
        }

        return "Created process with ID " + process.getProcessId();
    }

    @ShellMethod("Kill a process.")
    public String killProcess(
            @ShellOption Long pid
    ) {
        try {
            taskManagerService.killProcess(pid);
        } catch (Exception e) {
            return e.getMessage();
        }

        return "Killed process with id " + pid;
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

        taskManagerService.killGroup(priority);
        return "Killed process with priority " + priority.getPriorityName();
    }

    @ShellMethod("Kill all processes.")
    public String killAll() {

        taskManagerService.killAll();
        return "Killed all processes";
    }

    @ShellMethod("Get processes.")
    public String getProcesses(
            @ShellOption(defaultValue = DFLT) String sortMode
    ) {
        List<Process> processList;
        try {
            processList = sortMode.equals(DFLT) ?
                    taskManagerService.getProcesses() :
                    taskManagerService.getProcesses(TaskManagerService.SortMode.fromString(sortMode));
        } catch (Exception e) {
            return e.getMessage();
        }

        return processList.stream().map(Process::toString).collect(Collectors.joining("\n"));
    }
}