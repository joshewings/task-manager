package com.joshings.taskmanager.service.converter;

import com.joshings.taskmanager.repository.model.ProcessEntity;
import com.joshings.taskmanager.service.model.Priority;
import com.joshings.taskmanager.service.model.Process;
import org.springframework.stereotype.Component;

/**
 * Convert DAO entity to BO entity
 */
@Component
public class ProcessEntityConverter {

    /**
     * Convert process entity to process.
     *
     * @param processEntity the process entity
     * @return the process
     */
    public Process convert(ProcessEntity processEntity) {
        return new Process(
                processEntity.getId(),
                Priority.fromLong(processEntity.getPriority()),
                processEntity.getStartTime()
        );
    }
}
