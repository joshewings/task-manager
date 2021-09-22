package com.joshings.taskmanager.service.model;

public enum Priority {
    Low(1, "low"),
    Medium(2, "medium"),
    High(3, "high");

    private final long priorityValue;
    private final String priorityName;

    Priority(long priorityValue, String priorityName) {
        this.priorityValue = priorityValue;
        this.priorityName = priorityName;
    }

    public long getPriorityValue() {
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

    public static Priority fromLong(Long priorityValue) {
        for (Priority priority : Priority.values()) {
            if (priorityValue.intValue() == priority.getPriorityValue()) {
                return priority;
            }
        }
        throw new IllegalArgumentException("Unrecognized priority value");
    }
}
