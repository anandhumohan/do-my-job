package com.example.domyjob.controller;

import com.example.domyjob.model.JobChain;
import com.example.domyjob.model.Task;
import com.example.domyjob.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping("/update")
    public void updateOrder() {
        taskService.updateInvoiceStatusToPaid();
    }

    @PostMapping("/notify")
    public void sendNotification(@RequestBody Task task) {
        taskService.sendEmailNotification(task);
    }

    @PostMapping("/schedule")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<?> createJobChain(@RequestBody JobChain jobChain) {
        taskService.scheduleJobChain(jobChain);
        return ResponseEntity.ok().build();
    }
}
