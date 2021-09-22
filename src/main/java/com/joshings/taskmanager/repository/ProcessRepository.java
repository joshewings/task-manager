package com.joshings.taskmanager.repository;

import com.joshings.taskmanager.repository.model.ProcessEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.Optional;

/**
 * The interface Process repository.
 */
public interface ProcessRepository extends CrudRepository<ProcessEntity, Long> {

    /**
     * Find the oldest running process.
     *
     * @return the optional
     */
    Optional<ProcessEntity> findFirstByOrderByStartTime();

    /**
     * FInd the oldest running process with the give priority.
     *
     * @param priority the priority
     * @return the optional
     */
    Optional<ProcessEntity> findFirstByPriorityOrderByStartTime(Long priority);

    /**
     * Gets lowest priority of the running processes.
     *
     * @return the lowest priority
     */
    @Query("select min(p.priority) from ProcessEntity p")
    Optional<Long> getLowestPriority();

    /**
     * Get all the processes and order them by ID.
     *
     * @return the iterable
     */
    Iterable<ProcessEntity> findAllByOrderById();

    /**
     * Get all the processes and order them by priority.
     *
     * @return the iterable
     */
    Iterable<ProcessEntity> findAllByOrderByPriority();

    /**
     * Get all the processes and order them by start time.
     *
     * @return the iterable
     */
    Iterable<ProcessEntity> findAllByOrderByStartTime();

    /**
     * Delete all the processes with a given priority.
     *
     * @param priority the priority
     */
    @Transactional
    void deleteAllByPriority(Long priority);
}
