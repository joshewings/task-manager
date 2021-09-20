package com.joshings.taskmanager.repository.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity
@NoArgsConstructor
public class ProcessEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Getter
    private Long id;

    @Getter
    private String priority;

    @Getter
    private Timestamp startTime;

    public ProcessEntity(String priority, Timestamp startTime) {
        this.priority = priority;
        this.startTime = startTime;
    }

}
