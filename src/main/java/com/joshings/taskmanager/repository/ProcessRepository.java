package com.joshings.taskmanager.repository;

import com.joshings.taskmanager.repository.model.ProcessEntity;
import org.springframework.data.repository.CrudRepository;

public interface ProcessRepository extends CrudRepository<ProcessEntity, Long> {
}
