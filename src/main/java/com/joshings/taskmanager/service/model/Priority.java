package com.joshings.taskmanager.service.model;

/**
 * The enum for process Priorities.
 */
public enum Priority {
    /**
     * Low priority.
     */
    Low(1, "low"),
    /**
     * Medium priority.
     */
    Medium(2, "medium"),
    /**
     * High priority.
     */
    High(3, "high");

    private final long priorityValue;
    private final String priorityName;

    Priority(long priorityValue, String priorityName) {
        this.priorityValue = priorityValue;
        this.priorityName = priorityName;
    }

    /**
     * Gets priority value.
     *
     * @return the priority value
     */
    public long getPriorityValue() {
        return priorityValue;
    }

    /**
     * Gets priority name.
     *
     * @return the priority name
     */
    public String getPriorityName() {
        return priorityName;
    }

    /**
     * Get the priority from the priority name.
     *
     * @param priorityName the priority name
     * @return the priority
     */
    public static Priority fromString(String priorityName) {
        for (Priority priority : Priority.values()) {
            if (priority.priorityName.equalsIgnoreCase(priorityName)) {
                return priority;
            }
        }
        throw new IllegalArgumentException("Unrecognized priority name");
    }

    /**
     * Get the priority from the priority value.
     *
     * @param priorityValue the priority value
     * @return the priority
     */
    public static Priority fromLong(Long priorityValue) {
        for (Priority priority : Priority.values()) {
            if (priorityValue.intValue() == priority.getPriorityValue()) {
                return priority;
            }
        }
        throw new IllegalArgumentException("Unrecognized priority value");
    }
}
