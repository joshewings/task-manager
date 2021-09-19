package com.joshings.taskmanager.model;

public enum Priority {
    LOW("low"),
    MEDIUM("medium"),
    HIGH("high");

    private String priority;

    Priority(String priority) {
        this.priority = priority;
    }

    public String getPriority() {
        return priority;
    }
}
