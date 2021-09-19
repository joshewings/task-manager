package com.joshings.taskmanager.model;

public enum Priority {
    Low(1, "low"),
    Medium(2, "medium"),
    High(3, "high");

    private final int priorityValue;
    private final String priorityName;

    Priority(int priorityValue, String priorityName) {
        this.priorityValue = priorityValue;
        this.priorityName = priorityName;
    }

    public int getPriorityValue() {
        return priorityValue;
    }

    public String getPriorityName() {
        return priorityName;
    }

    public static Priority fromString(String priorityName) {
        for (Priority priority : Priority.values()) {
            if (priority.priorityName.equalsIgnoreCase(priorityName)) {
                return priority;
            }
        }
        throw new IllegalArgumentException("Unrecognized priority name");
    }
}
