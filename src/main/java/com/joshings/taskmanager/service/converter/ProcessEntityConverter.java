package com.joshings.taskmanager.service.converter;

import com.joshings.taskmanager.repository.model.ProcessEntity;
import com.joshings.taskmanager.service.model.Priority;
import com.joshings.taskmanager.service.model.Process;
import org.springframework.stereotype.Component;

@Component
public class ProcessEntityConverter {

    public Process convert(ProcessEntity processEntity) {
        return new Process(
                processEntity.getId(),
                Priority.fromLong(processEntity.getPriority()),
                processEntity.getStartTime()
        );
    }
}
