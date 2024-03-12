package com.example.domyjob.repository;

import com.example.domyjob.model.JobChain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

public interface JobChainRepository extends CrudRepository<JobChain, Long> {

    @Query("SELECT j FROM JobChain j WHERE j.scheduleTime > :now ORDER BY j.scheduleTime ASC")
    JobChain findNextScheduledJobChain(LocalDateTime now);
}

