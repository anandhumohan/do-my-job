package com.example.domyjob.repository;

import com.example.domyjob.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    int updateStatus(String completed, String description);
}