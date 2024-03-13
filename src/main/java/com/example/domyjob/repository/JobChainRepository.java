package com.example.domyjob.repository;

import com.example.domyjob.model.JobChain;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface JobChainRepository extends CrudRepository<JobChain, Long> {

    @Query("SELECT j FROM JobChain j WHERE j.scheduleTime > :now ORDER BY j.scheduleTime ASC")
    JobChain findNextScheduledJobChain(LocalDateTime now);
}

