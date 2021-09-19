package com.joshings.taskmanager.shell;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class Commands {

    @ShellMethod("Add a process.")
    public String addProcess(
            @ShellOption(defaultValue = "low") String priority
    ) {
        // invoke service
        return "test";
    }
}