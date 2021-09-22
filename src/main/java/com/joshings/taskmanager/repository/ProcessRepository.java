package com.joshings.taskmanager.repository;

import com.joshings.taskmanager.repository.model.ProcessEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ProcessRepository extends CrudRepository<ProcessEntity, Long> {

    Optional<ProcessEntity> findFirstByOrderByStartTime();

    Optional<ProcessEntity> findFirstByPriorityOrderByStartTime(Long priority);

    @Query("select min(p.priority) from ProcessEntity p")
    Optional<Long> getLowestPriority();

}
