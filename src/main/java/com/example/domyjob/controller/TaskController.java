package com.example.domyjob.controller;

import com.example.domyjob.dto.TaskRequest;
import com.example.domyjob.model.Task;
import com.example.domyjob.repository.TaskRepository;
import com.example.domyjob.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @PostMapping
    public Task createAndScheduleTask(@RequestBody Task task) {
        Task savedTask = taskRepository.save(task);
        taskService.scheduleNewTask(savedTask);
        return savedTask;
    }
}
