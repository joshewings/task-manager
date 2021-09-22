package com.joshings.taskmanager.service.configuration;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="com.joshings.taskmanager")
public class TaskManagerConfigurationProperties {

    @Range(min=1)
    @Getter
    @Setter
    private long maxProcesses;
}
