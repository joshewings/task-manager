package com.joshings.taskmanager.repository.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = ProcessEntity.PROCESS)
@NoArgsConstructor
public class ProcessEntity {

    public static final String PROCESS = "process";
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Getter
    private Long id;

    @Getter
    private Long priority;

    @Getter
    private Timestamp startTime;

    public ProcessEntity(Long priority, Timestamp startTime) {
        this.priority = priority;
        this.startTime = startTime;
    }

}
